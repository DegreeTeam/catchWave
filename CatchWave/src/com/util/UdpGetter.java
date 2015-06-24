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

import com.catchwave.jni.DSPforJNI;

public class UdpGetter extends Thread {
	private static final int TIMEOUT = 2000;
	public static final String SERVER_NAME = "192.168.42.1";
	protected static final int PORT = 9000;
	protected boolean flag = false;
	Socket sock;
	private AudioTrack audioTrack;
	private boolean thread_flag;
	Handler mHandler;
	DSPforJNI dsp;

	public UdpGetter(AudioTrack audioTrack, Handler mHandler) {
		this.audioTrack = audioTrack;
		thread_flag = true;
		this.mHandler = mHandler;
		dsp = new DSPforJNI();
	}

	public void setThread_flag(boolean thread_flag) {
		this.thread_flag = thread_flag;
	}

	public boolean isSameByte(byte[] src, byte[] desc) {
		for (int i = 0; i < 20; i++) {
			if (src[i] != desc[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() {
		byte[] msg = new byte[2048 / 2];
		byte[] comp = new byte[2048 / 2];
		byte[] ack = { 1 };
		byte[] udp_port_byte = new byte[4];
		DataInputStream input;
		InetAddress serverAddr;
		Integer udp_port = 0;
		DatagramSocket datagramSocket = null;
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
				datagramSocket.send(outPacket);
				datagramSocket.receive(inPacket);
				if (!isSameByte(msg, comp)) {
					// audioTrack.write(msg, 0, msg.length);
					audioTrack.write(dsp.playAfterDSP(msg), 0, msg.length * 2);
					for (int i = 0; i < 20; i++) {
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
