package com.wingeon.truco;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

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
		
		ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.card_red_v);
	}

	@Override
	protected void onStop() {
		super.onStop();
		m_game.stop();
	}
}
