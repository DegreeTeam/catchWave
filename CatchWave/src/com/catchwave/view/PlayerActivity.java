package com.catchwave.view;

import com.catchwave.service.PlayService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlayerActivity extends Activity {

	Button playBtn;
	Button stopBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_player);
		
		playBtn = (Button) findViewById(R.id.playBtn);
		playBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				 startService(new Intent(getApplicationContext(),PlayService.class));
			}
        });
		
		stopBtn = (Button) findViewById(R.id.stopBtn);
		stopBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				 stopService(new Intent(getApplicationContext(),PlayService.class));
			}
        });
		
	}
}
