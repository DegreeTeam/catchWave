package com.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class BleScanner{

	private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private Context context;
    private static final long SCAN_PERIOD = 5000;

    public BleScanner(BluetoothAdapter mBluetoothAdapter, Handler mHandler, Context context) {
		super();
		this.mBluetoothAdapter = mBluetoothAdapter;
		this.mHandler = mHandler;
		this.context = context;
	}
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
   
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			Log.d("BLE", "device name = " + device.getName());
			Log.d("BLE", "device address = " + device.getAddress());
			Log.d("BLE", "device rssi = " + rssi);

			
			String ssid = new String();
			String pw = new String(); 
			Byte[] id = new Byte[4];
			Byte[] check = {0x33,0x00,0x00,0x00};
			for(int i=9;i<17;i++){ ssid += (char)scanRecord[i];}
			for(int i=17;i<25;i++){ pw += (char)scanRecord[i];}
			for(int i=25;i<29;i++){ id[i%25] = scanRecord[i];}
  
			//CatchWave¸¸ Àâ°Ô
			if(id[0] == check[0] && id[1] == check[1] && id[2] == check[2] && id[3] == check[3]
					&& -1* rssi < 70){
				Log.d("BLE", "ssid = " + ssid); 
				Log.d("BLE", "pw = " + pw);
				Log.d("BLE", "id = " + id);
				
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
			
		}
    };
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}
