package com.catchwave.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.catchwave.floatingactionbutton.FloatingActionButton;
import com.catchwave.service.PlayService;

public class PlayerActivity extends Activity {

	public static Activity playerActivity;
	private FloatingActionButton mFloatingButton1;
	private ToggleButton tgn;
	String uuidData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		playerActivity = this;
		setContentView(R.layout.activity_player);

		// ActionBar 설정 변경
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.action_layout);
		TextView textViewTitle = (TextView) findViewById(R.id.mytext);
		textViewTitle.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/Generally_Speaking.ttf"));
		textViewTitle.setPaintFlags(textViewTitle.getPaintFlags()
				| Paint.FAKE_BOLD_TEXT_FLAG);
		textViewTitle.setTextScaleX(1.8f);
		textViewTitle.setText("CATCH WAVE");

		// Get UUID
		Intent intent = getIntent();
		uuidData = intent.getExtras().getString("UUID");
		String name = uuidData.substring(0, 8);

		// TextView 설정
		TextView tv = (TextView) findViewById(R.id.title);
		tv.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/Generally_Speaking.ttf"));
		tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
		tv.setTextScaleX(2.0f);
		tv.setText(name);

		// Refresh Button
		mFloatingButton1 = (FloatingActionButton) findViewById(R.id.scan2);
		mFloatingButton1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		// ToggleButton
		tgn = (ToggleButton) findViewById(R.id.playbtn);
		tgn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (tgn.isChecked()) {
					startService(new Intent(getApplicationContext(),
							PlayService.class));
					tgn.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.center_stop));
				} else {
					stopService(new Intent(getApplicationContext(),
							PlayService.class));
					tgn.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.center));
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopService(new Intent(getApplicationContext(), PlayService.class));
	}
}
