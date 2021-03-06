package com.catchwave.view;

import java.util.Timer;
import java.util.TimerTask;

import com.util.WifiConnector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LogoActivity extends Activity {

	String uuidData;
	int time;;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);

		try {
			Intent intent1 = getIntent();
			uuidData = intent1.getExtras().getString("UUID");
			String ssid = uuidData.substring(0, 8);
			String pw = uuidData.substring(9, 16);
			Log.d("TEST", "Notification or Popupactivity : " + uuidData);
			WifiConnector wifi = new WifiConnector(LogoActivity.this);

			wifi.isConnect();

			intent = new Intent(LogoActivity.this, PlayerActivity.class);
			intent.putExtra("UUID", ssid);
			time = 3000;

			boolean wififlag = true;
			wifi.isConnect();
			while (wififlag) {
				Log.i("BLUE", "CHECKING");
				Thread.sleep(1000);
				if (wifi.isEnable()) {
					Log.i("BLUE", "CHECKING");
					if (wifi.connectWifi(ssid, pw)) {
						wififlag = false;
					}
				}
			}
		} catch (Exception e) {
			Log.d("TEST", "normal");
			intent = new Intent(LogoActivity.this, BleListActivity.class);
			time = 1000;
		}

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				LogoActivity.this.startActivity(intent);
				finish();
			}
		}, time);

	}
}
