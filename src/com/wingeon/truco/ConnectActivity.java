package com.wingeon.truco;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ConnectActivity extends Activity {
	
	private Button m_refreshButton;
	private ListView m_listView;
	private ArrayAdapter<String> m_arrayAdapter;
	private BluetoothAdapter m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Map<String, BluetoothDevice> m_devices;
	
	private final BroadcastReceiver m_receiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if(BluetoothDevice.ACTION_FOUND.equals(action)) {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            String name = device.getName() + "\n" + device.getAddress();
	            m_arrayAdapter.add(name);
	            m_devices.put(name, device);
	        }
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		
		m_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
		m_devices = new HashMap<String, BluetoothDevice>();
		
		m_refreshButton = (Button)findViewById(R.id.refresh_bt);
		m_refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_bluetoothAdapter.cancelDiscovery();
				m_arrayAdapter.clear();
				m_devices.clear();
				m_bluetoothAdapter.startDiscovery();
			}
		});
		
		m_listView = (ListView)findViewById(R.id.devices_bt);
		m_listView.setAdapter(m_arrayAdapter);
		
		m_listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	m_bluetoothAdapter.cancelDiscovery();
            	String itemValue = (String)m_listView.getItemAtPosition(position);
            	BluetoothDevice device = m_devices.get(itemValue);

            	Intent intent = new Intent(ConnectActivity.this, RoomActivity.class);
				intent.putExtra("type", "guest");
				intent.putExtra("device", device);
				startActivity(intent);
				finish();
            }

       }); 
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(m_receiver, filter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(m_receiver);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		m_arrayAdapter.clear();
		m_devices.clear();
		m_bluetoothAdapter.startDiscovery();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		m_bluetoothAdapter.cancelDiscovery();
	}
}
