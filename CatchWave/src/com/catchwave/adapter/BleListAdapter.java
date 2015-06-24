package com.catchwave.adapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.catchwave.view.PlayerActivity;
import com.catchwave.view.R;
import com.catchwave.vo.BleVO;
import com.gc.materialdesign.views.ButtonRectangle;
import com.util.WifiConnector;

public class BleListAdapter extends BaseAdapter {
	Context maincon;
	LayoutInflater inflater;
	ArrayList<BleVO> arSrc;
	int layout;

	public BleListAdapter(Context context, ArrayList<BleVO> arSrc) {
		// TODO Auto-generated constructor stub
		this.maincon = context;
		this.arSrc = arSrc;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arSrc.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = inflater.inflate(R.layout.ble_item, parent, false);
		TextView ssid = (TextView) convertView.findViewById(R.id.ssid);
		ButtonRectangle wifi = (ButtonRectangle) convertView
				.findViewById(R.id.wifi_connect);

		final String ssidName = arSrc.get(position).getSsid();
		ssid.setText(ssidName);
		wifi.setTag(position);
		wifi.setOnClickListener(new Button.OnClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onClick(View view) {
				try {
					int position = (Integer) view.getTag();
					Log.d("test", arSrc.get(position).getSsid());
					WifiConnector connector = new WifiConnector(maincon);
					boolean wififlag = connector.isConnect();

					if (wififlag) {
						if (connector.connectWifi(
								arSrc.get(position).getSsid(),
								arSrc.get(position).getPw())) {
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									if (!PlayerActivity.IsPlayer) {
										Intent intent = new Intent(maincon,
												PlayerActivity.class);
										intent.putExtra("UUID", ssidName);
										maincon.startActivity(intent);
									}
								}
							}, 500);
						}
					} else {
						Toast.makeText(maincon, "연결 버튼을 한번 더 눌러주세요", 0).show();
					}
				} catch (Exception e) {
					Log.e("ERROR", "BLELISTADAPTER");
				}
			}
		});

		return convertView;

	}
}
