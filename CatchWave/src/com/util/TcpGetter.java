package com.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TcpGetter extends Thread {
	public static final String SERVER_NAME = "192.168.42.1";
	protected static final int PORT = 2008;
	Socket sock;
	private AudioTrack audioTrack;
	private boolean thread_flag;
	Handler mHandler;

	public TcpGetter(AudioTrack audioTrack, Handler mHandler) {
		this.audioTrack = audioTrack;
		thread_flag = true;
		this.mHandler = mHandler;
	}

	public void setThread_flag(boolean thread_flag) {
		this.thread_flag = thread_flag;
	}

	@Override
	public void run() {
		byte[] datafile = new byte[32];

		DataInputStream input;
		InetAddress serverAddr;
		try {
			serverAddr = InetAddress.getByName(SERVER_NAME);
			Log.d("tes", "before sock");
			sock = new Socket(serverAddr, PORT);
			Log.d("tes", "after sock");
			input = new DataInputStream(sock.getInputStream());

			while (thread_flag) {
				input.read(datafile);
				audioTrack.write(datafile, 0, datafile.length);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				sock.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d("tes", "sock error");

			// 예외 메시지
			Message mess = mHandler.obtainMessage();
			mess.what = 1;
			mHandler.sendMessage(mess);
		}
	}
}
