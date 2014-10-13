package com.wingeon.truco;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class StatisticsActivity extends Activity {
	
	private TextView m_playedMatchesView;
	private TextView m_winsView;
	private TextView m_lossesView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		
		m_playedMatchesView = (TextView)findViewById(R.id.played_matches);
		m_winsView = (TextView)findViewById(R.id.wins);
		m_lossesView = (TextView)findViewById(R.id.losses);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StatisticsActivity.this);
		m_playedMatchesView.setText("" + prefs.getInt("played_matches", 0));
		m_winsView.setText("" + prefs.getInt("wins", 0));
		m_lossesView.setText("" + prefs.getInt("losses", 0));
	}
}
