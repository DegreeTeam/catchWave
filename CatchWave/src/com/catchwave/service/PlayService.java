package com.catchwave.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.catchwave.view.PlayerActivity;
import com.util.TcpGetter;
import com.util.UdpGetter;

public class PlayService extends Service {
	final int SAMPLE_RATE = 44100;
	int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT);
	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
			SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT, minSize, AudioTrack.MODE_STREAM);

	//TcpGetter getter;
	UdpGetter getter;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		audioTrack.play();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		getter = new UdpGetter(audioTrack, mHandler);
		getter.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getter.setThread_flag(false);
		super.onDestroy();
	}

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				stopSelf();
				PlayerActivity.playerActivity.finish();
			}
		}
	};

}
