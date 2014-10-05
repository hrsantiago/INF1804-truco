package com.wingeon.truco;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

	private Game m_game = new Game();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		m_game.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		m_game.stop();
	}
}
