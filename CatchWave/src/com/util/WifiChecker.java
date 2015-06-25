package com.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class WifiChecker extends AsyncTask<Void, Void, Void> {
	private ProgressDialog progressDialog;
	private Context mContext;

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
		// WifiManager wManager = (WifiManager) mContext
		// .getSystemService(Context.WIFI_SERVICE);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
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