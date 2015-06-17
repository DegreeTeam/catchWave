package com.catchwave.view;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        
        new Timer().schedule(new TimerTask() {          
		    @Override
		    public void run() {
		    	Intent intent = new Intent(LogoActivity.this, BleListActivity.class);
				startActivity(intent);
				finish();
		    }
		}, 1000);
		
    }
}
