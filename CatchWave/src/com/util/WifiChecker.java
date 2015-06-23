package com.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class WifiChecker extends AsyncTask<Void, Void, Void> {
	private ProgressDialog progressDialog;
	private Context mContext;
	private boolean check = false;

	public WifiChecker(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("WIFI CONNECTING");
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... v) {
		WifiManager wManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);

		while (!check) {
			check = wManager.isWifiEnabled();
		}
		return (Void) null;
	}

	@Override
	protected void onProgressUpdate(Void... v) {
		// TODO show progress
	}

	@Override
	protected void onPostExecute(Void v) {
		progressDialog.dismiss();
	}
}