package com.catchwave.service;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.catchwave.view.LogoActivity;
import com.catchwave.view.PopupActivity;
import com.catchwave.view.R;
import com.catchwave.vo.BleVO;

public class notService extends Service {

	public static boolean IsNoticeService = false;

	private ArrayList<BleVO> ble_list;
	private BluetoothAdapter mBluetoothAdapter;
	private PowerManager pm;
	private int count_noti = 0;
	private boolean running = true;

	// BluetoothCallBack
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@SuppressWarnings("deprecation")
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {

			String ssid = new String();
			String pw = new String();
			final Byte[] id = new Byte[4];

			for (int i = 9; i < 17; i++) {
				ssid += (char) scanRecord[i];
			}
			for (int i = 17; i < 25; i++) {
				pw += (char) scanRecord[i];
			}
			for (int i = 25; i < 29; i++) {
				id[i % 25] = scanRecord[i];
			}

			if (checkUUID(ssid, pw, rssi, id)) {
				if (pm.isScreenOn()) {
					/* Screen on State - Call notification */
					if (!PopupActivity.IsPopup)
						NotificationBuilder(ssid + pw);
				} else {
					/*
					 * Screen off State - Call popup activity
					 */
					Intent popupIntent = new Intent(notService.this,
							PopupActivity.class);
					popupIntent.putExtra("PUUID", ssid + pw);
					popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(popupIntent);
				}
			}
			scanLeDevice(running);

		}
	};

	// Check UUID
	boolean checkUUID(String ssid, String pw, int rssi, Byte[] id) {
		Byte[] check = { 0x33, 0x00, 0x00, 0x00 };

		// CatchWave만 잡게
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
				return true;
			} else
				return false;

		}
		return false;

	}

	// Scanning UUID
	private void scanLeDevice(final boolean enable) {
		Log.i("BLETEST", "SCANLEDEVICE");
		if (enable) {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	// OnBluetooth
	public void bluetoothcheck() {
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT)
					.show();
		}

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mBluetoothAdapter.enable();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT)
					.show();
			return;
		}
	}

	// Notification
	public void NotificationBuilder(String uuid) {

		++count_noti;
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent pendingIntent = null;

		Log.i("TEST", String.valueOf(count_noti));
		Intent trans = new Intent(this, LogoActivity.class);
		trans.putExtra("UUID", uuid);

		pendingIntent = PendingIntent.getActivity(this, count_noti, trans,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder mBuilder = new Notification.Builder(this);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setTicker("CATCH WAVE !!");
		mBuilder.setWhen(System.currentTimeMillis());
		mBuilder.setContentTitle("Catch Wave Signal");
		mBuilder.setContentText(uuid.substring(0, 8) + " 클릭하시면 앱이 실행 됩니다.");

		Log.d("TEST", uuid.substring(0, 8) + "가 잡혔습니다. ");

		mBuilder.setDefaults(Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE);
		// mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		mBuilder.setAutoCancel(true);
		mBuilder.setContentIntent(pendingIntent);

		// mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

		nm.notify(count_noti, mBuilder.build());

	}

	// OnCreate
	@Override
	public void onCreate() {
		super.onCreate();
		IsNoticeService = true;
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		ble_list = new ArrayList<BleVO>();
		bluetoothcheck();
		scanLeDevice(running);
	}

	// OnBind
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	// OnDestroy
	@Override
	public void onDestroy() {
		running = false;
		IsNoticeService = false;
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		super.onDestroy();
	}
}
