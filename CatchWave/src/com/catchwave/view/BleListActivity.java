package com.catchwave.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.catchwave.adapter.BleListAdapter;
import com.catchwave.vo.BleVO;
import com.util.BleScanner;

public class BleListActivity extends Activity {
	private BluetoothAdapter mBluetoothAdapter;
	ArrayList<BleVO> ble_arr;
	BleListAdapter adapter;
	Button scan;
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_ble);
	        
	        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			    Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
			    //지원하지 않으면 서비스 종료
			    Toast.makeText(this, "BLE를 지원하지 않습니다.", 0);
			    finish();
			} 
	        final BluetoothManager bluetoothManager =
			        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
			
	        	        
			
	        ble_arr = new ArrayList<BleVO>();
	        BleVO test = new BleVO();
	        test.setSsid("test");
	        ble_arr.add(test);
	        adapter = new BleListAdapter(this, ble_arr);
	        
	        ListView list = (ListView) findViewById(R.id.ble_listView);
	        list.setAdapter(adapter);
	        list.setOnItemClickListener(mItemClickListener);
	        
	        scan = (Button) findViewById(R.id.scan);
	        scan.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					Log.d("tset", "button test");
					// Ensures Bluetooth is available on the device and it is enabled. If not,
					// displays a dialog requesting user permission to enable Bluetooth.
					if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
					    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					    startActivityForResult(enableBtIntent, 2);
					}
					else{
						BleScanner bleScanner = new BleScanner(mBluetoothAdapter, mHandler);
						bleScanner.start();
					}
				}
	        });
	    }
	 AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long l_position) {
				Log.d("tt", "ssid = " + ble_arr.get(position).getSsid());
			};
	 };
	 
	 @SuppressLint("HandlerLeak")
		public Handler mHandler = new Handler() {
			@Override
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					ble_arr.clear();
					ble_arr.addAll((ArrayList<BleVO>) msg.obj);
					adapter.notifyDataSetChanged();
					break;
				}
			}
	 };

}
