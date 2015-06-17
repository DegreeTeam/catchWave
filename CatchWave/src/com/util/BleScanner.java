package com.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.catchwave.view.BleListActivity;
import com.catchwave.view.LogoActivity;
import com.catchwave.vo.BleVO;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BleScanner extends Thread{

	private ArrayList<BleVO> ble_list;
	private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private Context context;
    private static final long SCAN_PERIOD = 3000;
        
    
    @Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
	@Override
	public void run() {
		scanLeDevice(true);
		 new Timer().schedule(new TimerTask() {          
			    @Override
			    public void run() {
			    	Message mess = mHandler.obtainMessage();
					mess.what =1;
					mess.obj= ble_list;
		
					mHandler.sendMessage(mess);
			    }
			}, 3000);
	}
	public BleScanner(BluetoothAdapter mBluetoothAdapter, Handler mHandler) {
		super();
		this.mBluetoothAdapter = mBluetoothAdapter;
		this.mHandler = mHandler;
		ble_list = new ArrayList<BleVO>();
	}
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
   
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
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
				
				BleVO ble = new BleVO();
				ble.setSsid(ssid);
				ble.setPw(pw);
				
				boolean flag = true;
				for(int i=0;i<ble_list.size();i++){
					if(ble_list.get(i).getSsid().equals(ble.getSsid())){
						flag = false;
					}
				}
				if(flag){
					ble_list.add(ble);
				}
				
			}
			
		}
    };
    public void scanLeDevice(final boolean enable) {
    	ble_list.clear();
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
