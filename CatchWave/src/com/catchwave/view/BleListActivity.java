package com.catchwave.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.catchwave.adapter.BleListAdapter;
import com.catchwave.floatingactionbutton.FloatingActionButton;
import com.catchwave.service.notService;
import com.catchwave.vo.BleVO;
import com.util.BleScanner;

public class BleListActivity extends Activity {

	public static int mode_cur;
	private BluetoothAdapter mBluetoothAdapter;
	ArrayList<BleVO> ble_arr;
	BleListAdapter adapter;
	private FloatingActionButton mFloatingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ble);

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

		// BLE 지원 안되는거 종료
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
			// 지원하지 않으면 서비스 종료
			Toast.makeText(this, "BLE를 지원하지 않습니다.", 0);
			finish();
		}

		// 리스트뷰 세팅
		ble_arr = new ArrayList<BleVO>();
		adapter = new BleListAdapter(this, ble_arr);
		ListView list = (ListView) findViewById(R.id.ble_listView);
		list.setAdapter(adapter);

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// BLE 스캔 기능
		mFloatingButton = (FloatingActionButton) findViewById(R.id.scan);
		mFloatingButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Log.d("tset", "button test");
				// Ensures Bluetooth is available on the device and it is
				// enabled. If not,
				// displays a dialog requesting user permission to enable
				// Bluetooth.

				// 리스트뷰 세팅
				ble_arr = new ArrayList<BleVO>();
				adapter = new BleListAdapter(BleListActivity.this, ble_arr);
				ListView list = (ListView) findViewById(R.id.ble_listView);
				list.setAdapter(adapter);

				if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, 2);
				} else {
					BleScanner bleScanner = new BleScanner(
							BleListActivity.this, mBluetoothAdapter, mHandler);
					bleScanner.start();
				}
			}
		});

		// SharedPreferences
		SharedPreferences pref = getSharedPreferences("SaveState", 0);
		mode_cur = pref.getInt("MODE", 0);

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 2);
		} else {
			BleScanner bleScanner = new BleScanner(BleListActivity.this,
					mBluetoothAdapter, mHandler);
			bleScanner.start();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences pref = getSharedPreferences("SaveState", 0);
		SharedPreferences.Editor edit = pref.edit();
		edit.putInt("MODE", mode_cur);
		edit.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.blelist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == (R.id.setting)) {

			ScanDialogRadio();
		}
		return super.onOptionsItemSelected(item);
	}

	private void ScanDialogRadio() {
		final CharSequence[] scanMode = { "수동", "자동" };
		AlertDialog.Builder mode_d = new AlertDialog.Builder(this);
		mode_d.setIcon(R.drawable.setting);
		mode_d.setTitle("Select Mode");
		mode_d.setSingleChoiceItems(scanMode, mode_cur,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						mode_cur = item;
					}
				});
		mode_d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Action for 'YES' Button
				// Stop Service
				if (mode_cur == 0) {
					Log.i("TEST", "MODE 0 STOP Service");
					// BR
					Intent intent = new Intent("com.catchwave.mode.signal");
					intent.putExtra("MODE", false);
					sendBroadcast(intent);
					// Service
					if (notService.IsNoticeService)
						stopService(new Intent(BleListActivity.this,
								notService.class));
				}
				// Start Service
				if (mode_cur == 1) {
					Log.i("TEST", "MODE 1 START Service");
					// BR
					Intent intent = new Intent("com.catchwave.mode.signal");
					intent.putExtra("MODE", true);
					sendBroadcast(intent);
					// Service
					if (!notService.IsNoticeService)
						startService(new Intent(BleListActivity.this,
								notService.class));
				}
			}
		});
		mode_d.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Action for 'NO' Button
						dialog.cancel();
					}
				});

		AlertDialog alert = mode_d.create();
		alert.show();
	}

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				ble_arr.clear();
				ble_arr.addAll((ArrayList<BleVO>) msg.obj);
				if (ble_arr.size() == 0) {
					Toast.makeText(getApplicationContext(),
							"THERE IS NO BLE SIGNAL", Toast.LENGTH_LONG).show();
				}
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};

}
