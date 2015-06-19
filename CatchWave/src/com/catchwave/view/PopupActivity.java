package com.catchwave.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class PopupActivity extends Activity {
	public static boolean IsPopup = false;

	private TextView tv;
	private String uuidData;

	// PopupActivity Click Event
	public void PopupClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			Intent intent = new Intent(this, LogoActivity.class);
			intent.putExtra("UUID", uuidData);
			startActivity(intent);
			finish();
			break;
		case R.id.btn2:
			finish();
			break;
		}
	}

	// OnCreate
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		IsPopup = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop);

		tv = (TextView) findViewById(R.id.tv);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		Intent intent = getIntent();
		uuidData = intent.getExtras().getString("PUUID");
	}

	// OnResume
	@Override
	protected void onResume() {
		super.onResume();
		String name = uuidData.substring(0, 8);
		tv.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/Generally_Speaking.ttf"));
		tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
		tv.setTextScaleX(2.0f);
		tv.setText(name);
	}

	// OnDestroy
	@Override
	protected void onDestroy() {
		IsPopup = false;
		super.onDestroy();

	}
}
