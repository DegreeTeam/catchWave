package com.catchwave.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.catchwave.service.notService;

public class PopupActivity extends Activity {
	public static boolean IsPopup = false;

	private TextView tv;
	private String uuidData;
	private ImageButton imgbtn;
	int cur;

	// PopupActivity Click Event
	public void PopupClick(View v) {
		v.setBackgroundColor(Color.parseColor("#AAAAAA"));
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

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		tv = (TextView) findViewById(R.id.tv);
		imgbtn = (ImageButton) findViewById(R.id.setbtn);
		imgbtn.setAlpha(0.4f);
		imgbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ScanDialogRadio();
			}
		});

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		Intent intent = getIntent();
		uuidData = intent.getExtras().getString("PUUID");
	}

	// Dialog
	private void ScanDialogRadio() {
		final CharSequence[] scanMode = { "수동", "자동" };
		AlertDialog.Builder mode_d = new AlertDialog.Builder(this);
		mode_d.setIcon(R.drawable.setting);
		mode_d.setTitle("Select Mode");
		mode_d.setSingleChoiceItems(scanMode, cur,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						cur = item;
					}
				});
		mode_d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Action for 'YES' Button
				// Stop Service
				if (cur == 0) {
					Log.i("TEST", "MODE 0 STOP Service");
					BleListActivity.mode_cur = 0;
					// BR
					Intent intent = new Intent("com.catchwave.mode.signal");
					intent.putExtra("MODE", false);
					sendBroadcast(intent);
					// Service
					if (notService.IsNoticeService)
						stopService(new Intent(PopupActivity.this,
								notService.class));
				}
				// Start Service
				if (cur == 1) {
					Log.i("TEST", "MODE 1 START Service");
					BleListActivity.mode_cur = 1;
					// BR
					Intent intent = new Intent("com.catchwave.mode.signal");
					intent.putExtra("MODE", true);
					sendBroadcast(intent);
					// Service
					if (!notService.IsNoticeService)
						startService(new Intent(PopupActivity.this,
								notService.class));
				}
			}
		});
		mode_d.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Action for 'NO' Button
						dialog.cancel();
					}
				});

		AlertDialog alert = mode_d.create();
		alert.show();
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
		tv.setGravity(Gravity.CENTER);
		cur = BleListActivity.mode_cur;
	}

	// OnDestroy
	@Override
	protected void onDestroy() {
		IsPopup = false;
		super.onDestroy();

	}
}
