package com.catchwave.br;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.catchwave.service.notService;

public class BootBR extends BroadcastReceiver {

	boolean auto = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
			
			SharedPreferences pref = context.getSharedPreferences(
					"SaveBRState", 0);
			auto = pref.getBoolean("AMODE", false);

			ComponentName cn = new ComponentName(context.getPackageName(),
					notService.class.getName());
			if (auto) {
				ComponentName svcName = context.startService(new Intent()
						.setComponent(cn));
				if (svcName == null)
					Log.e("BOOTSVC", "Could not start service " + cn.toString());
			}

		} else if (action.equals("com.catchwave.mode.signal")) {
			auto = intent.getBooleanExtra("MODE", false);

			SharedPreferences pref = context.getSharedPreferences(
					"SaveBRState", 0);
			SharedPreferences.Editor edit = pref.edit();
			edit.putBoolean("AMODE", auto);
			edit.commit();

			Log.d("BLE", String.valueOf(auto));
		}
	}
}