package com.wingeon.truco;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingeon.truco.core.Card;
import com.wingeon.truco.core.Game;
import com.wingeon.truco.core.Player;

public class GameActivity extends Activity {

	private static String HELP_WEBSITE = "http://www.jogatina.com/regras-como-jogar-truco.html";
	private Game m_game = new Game();
	
	private ImageView m_playersViews[][] = new ImageView[4][4];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		m_playersViews[0][0] = (ImageView)findViewById(R.id.player_0_card_1);
		m_playersViews[0][1] = (ImageView)findViewById(R.id.player_0_card_2);
		m_playersViews[0][2] = (ImageView)findViewById(R.id.player_0_card_3);
		m_playersViews[0][3] = (ImageView)findViewById(R.id.player_0_card);
		
		m_playersViews[1][0] = (ImageView)findViewById(R.id.player_1_card_1);
		m_playersViews[1][1] = (ImageView)findViewById(R.id.player_1_card_2);
		m_playersViews[1][2] = (ImageView)findViewById(R.id.player_1_card_3);
		m_playersViews[1][3] = (ImageView)findViewById(R.id.player_1_card);
		
		m_playersViews[2][0] = (ImageView)findViewById(R.id.player_2_card_1);
		m_playersViews[2][1] = (ImageView)findViewById(R.id.player_2_card_2);
		m_playersViews[2][2] = (ImageView)findViewById(R.id.player_2_card_3);
		m_playersViews[2][3] = (ImageView)findViewById(R.id.player_2_card);
		
		m_playersViews[3][0] = (ImageView)findViewById(R.id.player_3_card_1);
		m_playersViews[3][1] = (ImageView)findViewById(R.id.player_3_card_2);
		m_playersViews[3][2] = (ImageView)findViewById(R.id.player_3_card_3);
		m_playersViews[3][3] = (ImageView)findViewById(R.id.player_3_card);
	}
	
	@Override
	protected void onStart() {
		System.out.println("Start");
		super.onStart();
		m_game.start();
		
		m_game.getPlayer(0).playCard();
		
		updatePlayer(0);
		updatePlayer(1);
		updatePlayer(2);
		updatePlayer(3);
	}

	@Override
	protected void onStop() {
		System.out.println("End");
		super.onStop();
		m_game.stop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
		String name = prefs.getString("name", "Jogador 1");

		TextView p1Name = (TextView) findViewById(R.id.player_0_name);
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
	
	private void updatePlayer(int id) {
		Player player = m_game.getPlayer(id);
		for(int i = 0; i < 3; ++i) {
			ImageView imageView = m_playersViews[id][i];
			Card card = player.getCard(i);
			if(card == null)
				imageView.setVisibility(View.INVISIBLE);
			else if(id != 0)
				imageView.setImageResource(getCardResourceId(null));
			else
				imageView.setImageResource(getCardResourceId(card));
		}
	}
	
	private int getCardResourceId(Card card) {
		if(card == null)
			return R.drawable.card_red_v;
		
		Card.Value v = card.getValue();
		Card.Suit s = card.getSuit();
		
		if(s == Card.Suit.CLUBS) {
			if(v == Card.Value._4)
				return R.drawable.card_clubs_4;
			else if(v == Card.Value._5)
				return R.drawable.card_clubs_5;
			else if(v == Card.Value._6)
				return R.drawable.card_clubs_6;
			else if(v == Card.Value._7)
				return R.drawable.card_clubs_7;
			else if(v == Card.Value._Q)
				return R.drawable.card_clubs_q;
			else if(v == Card.Value._J)
				return R.drawable.card_clubs_j;
			else if(v == Card.Value._K)
				return R.drawable.card_clubs_k;
			else if(v == Card.Value._A)
				return R.drawable.card_clubs_a;
			else if(v == Card.Value._2)
				return R.drawable.card_clubs_2;
			else if(v == Card.Value._3)
				return R.drawable.card_clubs_3;
		}
		else if(s == Card.Suit.HEARTS) {
			if(v == Card.Value._4)
				return R.drawable.card_hearts_4;
			else if(v == Card.Value._5)
				return R.drawable.card_hearts_5;
			else if(v == Card.Value._6)
				return R.drawable.card_hearts_6;
			else if(v == Card.Value._7)
				return R.drawable.card_hearts_7;
			else if(v == Card.Value._Q)
				return R.drawable.card_hearts_q;
			else if(v == Card.Value._J)
				return R.drawable.card_hearts_j;
			else if(v == Card.Value._K)
				return R.drawable.card_hearts_k;
			else if(v == Card.Value._A)
				return R.drawable.card_hearts_a;
			else if(v == Card.Value._2)
				return R.drawable.card_hearts_2;
			else if(v == Card.Value._3)
				return R.drawable.card_hearts_3;
		}
		else if(s == Card.Suit.SPADES) {
			if(v == Card.Value._4)
				return R.drawable.card_spades_4;
			else if(v == Card.Value._5)
				return R.drawable.card_spades_5;
			else if(v == Card.Value._6)
				return R.drawable.card_spades_6;
			else if(v == Card.Value._7)
				return R.drawable.card_spades_7;
			else if(v == Card.Value._Q)
				return R.drawable.card_spades_q;
			else if(v == Card.Value._J)
				return R.drawable.card_spades_j;
			else if(v == Card.Value._K)
				return R.drawable.card_spades_k;
			else if(v == Card.Value._A)
				return R.drawable.card_spades_a;
			else if(v == Card.Value._2)
				return R.drawable.card_spades_2;
			else if(v == Card.Value._3)
				return R.drawable.card_spades_3;
		}
		else if(s == Card.Suit.DIAMONDS) {
			if(v == Card.Value._4)
				return R.drawable.card_diamonds_4;
			else if(v == Card.Value._5)
				return R.drawable.card_diamonds_5;
			else if(v == Card.Value._6)
				return R.drawable.card_diamonds_6;
			else if(v == Card.Value._7)
				return R.drawable.card_diamonds_7;
			else if(v == Card.Value._Q)
				return R.drawable.card_diamonds_q;
			else if(v == Card.Value._J)
				return R.drawable.card_diamonds_j;
			else if(v == Card.Value._K)
				return R.drawable.card_diamonds_k;
			else if(v == Card.Value._A)
				return R.drawable.card_diamonds_a;
			else if(v == Card.Value._2)
				return R.drawable.card_diamonds_2;
			else if(v == Card.Value._3)
				return R.drawable.card_diamonds_3;
		}
		return R.drawable.card_red_v;
	}
}
