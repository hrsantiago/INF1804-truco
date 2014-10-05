package com.wingeon.truco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
	}
}
