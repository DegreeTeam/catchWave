package com.util;

import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UdpGetter extends Thread{
	private static final int TIMEOUT = 2000;
	public static final String SERVER_NAME = "192.168.42.1";
	protected static final int PORT = 2008;
	Socket sock;
	private AudioTrack audioTrack;
	private boolean thread_flag;
	Handler mHandler;

	public UdpGetter(AudioTrack audioTrack, Handler mHandler) {
		this.audioTrack = audioTrack;
		thread_flag = true;
		this.mHandler = mHandler;
	}

	public void setThread_flag(boolean thread_flag) {
		this.thread_flag = thread_flag;
	}

	@Override
	public void run() {
		byte[] msg = new byte[1024];
		byte[] ack = {1};
		DataInputStream input;
		InetAddress serverAddr;
		Integer udp_port;
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket= new DatagramSocket();
			serverAddr = InetAddress.getByName(SERVER_NAME);
			sock = new Socket(serverAddr, PORT);
			input = new DataInputStream(sock.getInputStream());
			udp_port = input.readInt();
			Log.d("tes", "udp_port is "+ udp_port);
			sock.close();
			
			 
			datagramSocket.setSoTimeout(TIMEOUT);
			DatagramPacket outPacket = new DatagramPacket(ack, 1, serverAddr, udp_port);
		    DatagramPacket inPacket = new DatagramPacket(msg, msg.length);
		    sleep(1000);
			while (thread_flag) {
				datagramSocket.send(outPacket);    
		        datagramSocket.receive(inPacket);  
				audioTrack.write(msg, 0, msg.length);
			}

		} catch (Exception e) {
			e.printStackTrace();
			datagramSocket.close();

			// 예외 메시지
			Message mess = mHandler.obtainMessage();
			mess.what = 1;
			mHandler.sendMessage(mess);
		}
	}
}

