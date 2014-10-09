package com.wingeon.truco;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class GameActivity extends Activity {

	private static String HELP_WEBSITE = "http://www.jogatina.com/regras-como-jogar-truco.html";
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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
		String name = prefs.getString("name", "Jogador 1");

		TextView p1Name = (TextView) findViewById(R.id.player_1_name);
		p1Name.setText(name);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.new_game:
			recreate();
			return true;
		case R.id.help:
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(HELP_WEBSITE));
			startActivity(intent);
			return true;
		case R.id.exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
