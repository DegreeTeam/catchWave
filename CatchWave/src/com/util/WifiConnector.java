package com.util;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiConnector {

	private Context context;
	private ConnectivityManager connectivityManager;
	WifiManager wManager;

	public WifiConnector(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		connectivityManager = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

	}

	public boolean isEnable() {
		return wManager.isWifiEnabled();
	}

	public boolean isConnect() {
		wManager = (WifiManager) this.context
				.getSystemService(Context.WIFI_SERVICE);

		if (!(wManager.isWifiEnabled())) // WIFI on
		{
			wManager.setWifiEnabled(true);
			return false;
		}
		return true;
	}

	public boolean connectWifi(String ssid, String pw) {
		try {
			if (ssid == null || pw == null) {
				return false;
			}

			int networkId = -1;
			WifiConfiguration wfc = new WifiConfiguration();

			wfc.SSID = "\"".concat(ssid).concat("\"");
			wfc.preSharedKey = "\"".concat(pw).concat("\"");
			wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wfc.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			wfc.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

			List<WifiConfiguration> configList = wManager
					.getConfiguredNetworks();
			boolean flag = true;
			if (configList != null) {
				for (int i = 0; i < configList.size(); i++) {
					String str = configList.get(i).SSID;
					if (str.equals("\"".concat(ssid).concat("\""))) {
						networkId = configList.get(i).networkId;
						flag = false;
						break;
					}
				}
			}
			if (flag) {
				networkId = wManager.addNetwork(wfc);
			}

			if (networkId != -1) {
				wManager.enableNetwork(networkId, true);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("ERROR", "CONNECT_WIFI_ERROR");
			return false;
		}
	}

}
