package com.wingeon.truco;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import com.wingeon.net.BluetoothConnect;
import com.wingeon.net.BluetoothServer;

public class RoomActivity extends Activity {
	
	BluetoothServer m_server;
	BluetoothConnect m_connect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);
		
		Intent intent = getIntent();
		String connectType = intent.getStringExtra("type");
		if(connectType.equals("host")) {
			m_server = new BluetoothServer();
			m_server.start();
		}
		else if(connectType.equals("guest")) {
			BluetoothDevice device = intent.getParcelableExtra("device");
			m_connect = new BluetoothConnect(device);
			m_connect.start();
		}
	}
	
	@Override
	protected void onDestroy() {
		if(m_server != null) {
			m_server.cancel();
			m_server = null;
		}
		m_connect = null;
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
}
