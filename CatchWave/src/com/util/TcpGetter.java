package com.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import android.media.AudioTrack;
import android.util.Log;

public class TcpGetter extends Thread {
	public static final String SERVER_NAME = "192.168.42.1";
	protected static final int PORT = 2008;
	Socket sock;
	private AudioTrack audioTrack;
	private boolean thread_flag;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TcpGetter(AudioTrack audioTrack) {
		this.audioTrack = audioTrack;
		thread_flag = true;
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
			
			while(thread_flag){
				input.read(datafile);
				Log.d("tes", datafile.toString());
				audioTrack.write(datafile, 0, datafile.length);
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			Log.d("tes", "sock error");
		}
	}
}
