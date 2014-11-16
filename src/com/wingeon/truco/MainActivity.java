package com.wingeon.truco;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity
{
	static int REQUEST_ENABLE_BT_CONNECT = 1;
	static int REQUEST_ENABLE_BT_DISCOVER_HOST = 2;
	static int DISCOVERABLE_DURATION = 300;

	private Button m_newGameButton;
	private Button m_connectButton;
	private Button m_hostButton;
	private Button m_statisticsButton;
	private Button m_optionsButton;
	private BluetoothAdapter m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		m_newGameButton = (Button)findViewById(R.id.new_game);
		m_newGameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				startActivity(intent);
			}
		});

		m_connectButton = (Button)findViewById(R.id.connect);
		m_connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_CONNECT);
			}
		});

		m_hostButton = (Button)findViewById(R.id.host);
		m_hostButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
				startActivityForResult(intent, REQUEST_ENABLE_BT_DISCOVER_HOST);
			}
		});

		m_statisticsButton = (Button)findViewById(R.id.statistics);
		m_statisticsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
				startActivity(intent);
			}
		});

		m_optionsButton = (Button)findViewById(R.id.options);
		m_optionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
				startActivity(intent);
			}
		});

		if(m_bluetoothAdapter == null) {
			m_connectButton.setEnabled(false);
			m_hostButton.setEnabled(false);
		}

		// Code to improve testing
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		String defaultColor = getResources().getString(R.color.table_green);
		int color = Color.parseColor(prefs.getString("table_color", defaultColor));
		findViewById(R.id.main_view).setBackgroundColor(color);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_ENABLE_BT_CONNECT) {
			if(resultCode == RESULT_OK) {
				Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
				startActivity(intent);
			}
		}
		else if(requestCode == REQUEST_ENABLE_BT_DISCOVER_HOST) {
			if(resultCode != RESULT_CANCELED) {
				Intent intent = new Intent(MainActivity.this, RoomActivity.class);
				intent.putExtra("type", "host");
				startActivity(intent);
			}
		}
	}
}
