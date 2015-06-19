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
	int time = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// Get UUID
				try {
					Intent intent1 = getIntent();
					uuidData = intent1.getExtras().getString("UUID");
					String ssid = uuidData.substring(0, 8);
					String pw = uuidData.substring(9, 16);
					Log.d("TEST", "Notification or Popupactivity : " + uuidData);
					WifiConnector wifi = new WifiConnector(LogoActivity.this);

					// delay Ω√≈∞±‚
					wifi.isConnect();
					wifi.connectWifi(ssid, pw);

					Intent intent = new Intent(LogoActivity.this,
							PlayerActivity.class);
					intent.putExtra("UUID", ssid);
					LogoActivity.this.startActivity(intent);

					finish();

					// }
				} catch (Exception e) {
					Log.d("TEST", "normal");
					Intent intent = new Intent(LogoActivity.this,
							BleListActivity.class);
					startActivity(intent);
					finish();
				}

			}
		}, time);

	}
}
