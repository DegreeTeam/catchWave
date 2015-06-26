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
import android.widget.Toast;

import com.catchwave.view.PlayerActivity;
import com.util.TcpGetter;

public class PlayService extends Service {
	public static boolean IsPlaySer = false;

	final int SAMPLE_RATE = 22400 * 2;

	int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT);
	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
			SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_8BIT, minSize, AudioTrack.MODE_STREAM);

	TcpGetter getter;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		IsPlaySer = true;
		audioTrack.play();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		getter = new TcpGetter(audioTrack, mHandler);
		getter.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		IsPlaySer = false;
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
				Toast.makeText(getApplicationContext(), "다시 연결을 시도해 주세요.",
						Toast.LENGTH_LONG).show();
				PlayerActivity.playerActivity.finish();
				break;
			case 2:
				stopSelf();
				Toast.makeText(getApplicationContext(), "시청 가능한 최대 인원을 넘었습니다.",
						Toast.LENGTH_LONG).show();
				PlayerActivity.playerActivity.finish();
				break;
			}

		}
	};

}
