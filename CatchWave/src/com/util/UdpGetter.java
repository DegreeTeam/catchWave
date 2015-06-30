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
	public static final String SERVER_NAME = "192.168.42.1";
	protected static final int PORT = 9000;
	protected boolean flag = false;
	Socket sock;
	private boolean thread_flag;
	Handler mHandler;
	
	private static AudioTrack audioTrack;
	private static byte[] msg = new byte[1024];

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
		byte[] ack = { 1 };
		byte[] udp_port_byte = new byte[4];
		DataInputStream input;
		InetAddress serverAddr;
		Integer udp_port = 0;
		DatagramSocket datagramSocket = null;
		Audio audio = new Audio();
		audioTrack.play();
		try {
			datagramSocket = new DatagramSocket();
			serverAddr = InetAddress.getByName(SERVER_NAME);
			sock = new Socket(serverAddr, PORT);
			input = new DataInputStream(sock.getInputStream());
			input.read(udp_port_byte);
			udp_port += (int) udp_port_byte[0];
			udp_port += (int) udp_port_byte[1] * 256;
			Log.d("tes", "udp_port is " + udp_port);
			
			DatagramPacket outPacket = new DatagramPacket(ack, 1, serverAddr,udp_port);
			DatagramPacket inPacket = new DatagramPacket(msg, msg.length);
			sleep(1000);
			
			
			audio.start();
			while (thread_flag) {
				datagramSocket.send(outPacket);
				datagramSocket.receive(inPacket);
				Log.d("test", "receiving data~");
				audio.onAudio();
			}

		} catch (Exception e) {
			e.printStackTrace();
			audio.stopAudio();
			try {
				sock.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			datagramSocket.close();
			// 예외 메시지
			Message mess = mHandler.obtainMessage();
			mess.what = 1;
			mHandler.sendMessage(mess);
		}
		audio.stopAudio();
		datagramSocket.close();
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeSound(){
		audioTrack.write(msg, 0, msg.length);
	}
	
	class Audio extends Thread{
		private boolean audio_flag;
		private boolean on_air;
		Audio(){
			audio_flag = true;
		}
		public void run(){
			try {
				sleep(1000);
				while(audio_flag){
					if(on_air){
						writeSound();
						on_air = false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		public void onAudio(){
			this.on_air = true;
		}
		public void stopAudio(){
			this.audio_flag = false;
		}
	}
}
