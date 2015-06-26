package com.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.catchwave.jni.DSPforJNI;

public class TcpGetter extends Thread {
	public static final String SERVER_NAME = "192.168.42.1";
	private int TIMEOUT = 1000;
	protected static final int PORT = 2008;
	Socket sock;
	private AudioTrack audioTrack;
	private boolean thread_flag;
	Handler mHandler;
	DSPforJNI dsp;

	public TcpGetter(AudioTrack audioTrack, Handler mHandler) {
		this.audioTrack = audioTrack;
		this.mHandler = mHandler;
		thread_flag = true;
		dsp = new DSPforJNI();
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

			Log.i("SOCKET",
					"SOCKET_INPUT_AVALIABLE "
							+ String.valueOf(input.available()));
			sock.setSoTimeout(TIMEOUT);

			while (thread_flag) {
				input.read(datafile);
				audioTrack.write(dsp.playAfterDSP(datafile), 0,
						datafile.length * 2);
			}
			sock.close();
		} catch (SocketTimeoutException e1) {
			try {
				sock.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			// 예외 메시지
			Message mess = mHandler.obtainMessage();
			mess.what = 2;
			mHandler.sendMessage(mess);

		} catch (Exception e) {
			Log.i("ERROR", "EXCEPTION");
			e.printStackTrace();
			try {
				sock.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			Log.d("tes", "sock error");

			// 예외 메시지
			Message mess = mHandler.obtainMessage();
			mess.what = 1;
			mHandler.sendMessage(mess);
		}
	}
}
