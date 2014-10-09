package com.wingeon.truco;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button m_newGameButton;
	private Button m_connectButton;
	private Button m_optionsButton;
	
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
		m_connectButton.setEnabled(false);
		m_connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
		int color = Color.parseColor(prefs.getString("table_color", "white"));
		findViewById(R.id.main_view).setBackgroundColor(color);
	}
}
