package com.util;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.catchwave.vo.BleVO;

public class BleScanner extends Thread {

	private ArrayList<BleVO> ble_list;
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private ProgressDialog progressDialog;
	private Context mContext;
	private boolean IsCallBack = true;
	private int count = 0;

	@Override
	public void run() {
		scanLeDevice(true);
		while (IsCallBack) {
		}

		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		progressDialog.dismiss();
		Message mess = mHandler.obtainMessage();
		mess.what = 1;
		mess.obj = ble_list;

		mHandler.sendMessage(mess);
	}

	public BleScanner(Context context, BluetoothAdapter mBluetoothAdapter,
			Handler mHandler) {
		super();
		this.mContext = context;
		this.mBluetoothAdapter = mBluetoothAdapter;
		this.mHandler = mHandler;
		ble_list = new ArrayList<BleVO>();
		ble_list.clear();

		progressDialog = new ProgressDialog(mContext);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("BLUETOOTH SCANNING...");
		progressDialog.show();

		Log.i("BLE", "START");
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.i("BLE", "CALLBACK");
			String ssid = new String();
			String pw = new String();
			Byte[] id = new Byte[4];
			Byte[] check = { 0x33, 0x00, 0x00, 0x00 };
			for (int i = 9; i < 17; i++) {
				ssid += (char) scanRecord[i];
			}
			for (int i = 17; i < 25; i++) {
				pw += (char) scanRecord[i];
			}
			for (int i = 25; i < 29; i++) {
				id[i % 25] = scanRecord[i];
			}

			// CatchWave¸¸ Àâ°Ô
			if (id[0] == check[0] && id[1] == check[1] && id[2] == check[2]
					&& id[3] == check[3] && -1 * rssi < 70) {
				Log.d("BLE", "ssid = " + ssid);
				Log.d("BLE", "pw = " + pw);
				Log.d("BLE", "id = " + id);

				BleVO ble = new BleVO();
				ble.setSsid(ssid);
				ble.setPw(pw);

				boolean flag = true;
				for (int i = 0; i < ble_list.size(); i++) {
					if (ble_list.get(i).getSsid().equals(ble.getSsid())) {
						flag = false;
					}
				}
				if (flag) {
					ble_list.add(ble);
				}

			}
			if ((++count) >= 3)
				IsCallBack = false;
			scanLeDevice(IsCallBack);
		}
	};

	// Scanning UUID
	private void scanLeDevice(final boolean enable) {
		Log.i("BLETEST", "SCANLEDEVICE");
		if (enable) {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
}
