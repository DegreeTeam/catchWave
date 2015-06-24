package com.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UdpGetter extends Thread {
	private static final int TIMEOUT = 2000;
	public static final String SERVER_NAME = "192.168.42.1";
	protected static final int PORT = 9000;
	protected boolean flag = false;
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

	public boolean isSameByte(byte[] src, byte[] desc){
		for(int i=0;i<20; i++){
			if(src[i] != desc[i]){
				return false;
			}
		}
		return true;
	}
	@Override
	public void run() {
		byte[] msg = new byte[2048];
		byte[] comp = new byte[2048];
		byte[] ack = { 1 };
		byte[] udp_port_byte = new byte[4];
		DataInputStream input;
		InetAddress serverAddr;
		Integer udp_port = 0;
		DatagramSocket datagramSocket = null;
		long count =0;
		long count2 =0;
		try {
			datagramSocket = new DatagramSocket();
			serverAddr = InetAddress.getByName(SERVER_NAME);
			sock = new Socket(serverAddr, PORT);
			input = new DataInputStream(sock.getInputStream());
			input.read(udp_port_byte);
			udp_port += (int) udp_port_byte[0];
			udp_port += (int) udp_port_byte[1] * 256;
			Log.d("tes", "udp_port is " + udp_port);
			sock.close();

			datagramSocket.setSoTimeout(TIMEOUT);
			DatagramPacket outPacket = new DatagramPacket(ack, 1, serverAddr,
					udp_port);
			DatagramPacket inPacket = new DatagramPacket(msg, msg.length);
			sleep(1000);
			
			while (thread_flag) {
				boolean send_flag = true;
				while(send_flag){
					try{
						datagramSocket.send(outPacket);
					}
					catch(IOException e){
						send_flag = false;
					}
					finally{
						if(send_flag){
							break;
						}
						send_flag = true;
					}
				}
				datagramSocket.receive(inPacket);
				count++;
				if(!isSameByte(msg, comp)){
					count2++;
					audioTrack.write(msg, 0, msg.length);
					for(int i=0;i<20;i++){
						comp[i] = msg[i];
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			datagramSocket.close();

			// 예외 메시지
			Message mess = mHandler.obtainMessage();
			mess.what = 1;
			mHandler.sendMessage(mess);
		}
		datagramSocket.close();
	}
}
