package com.wingeon.truco;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingeon.truco.core.Card;
import com.wingeon.truco.core.Game;
import com.wingeon.truco.core.Player;
import com.wingeon.truco.core.PlayerHuman;
import com.wingeon.truco.core.Team;

public class GameActivity extends Activity {
	private static String HELP_WEBSITE = "http://www.jogatina.com/regras-como-jogar-truco.html";
	
	private Handler m_handler = new Handler() {
		@Override
		public void handleMessage(Message message) { onHandleMessage(message); }
	};
	private Game m_game;
	private ImageView m_playersViews[][] = new ImageView[Game.PLAYERS][4];
	private ImageView m_turnView;
	private TextView m_teamsView[] = new TextView[2];
	private Button m_trucoView;
	private Button m_closeView;
	private boolean m_cardClosed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		m_game = new Game(new Messenger(m_handler));
		
		m_teamsView[0] = (TextView)findViewById(R.id.team0_score);
		m_teamsView[1] = (TextView)findViewById(R.id.team1_score);
		
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
		
		m_turnView = (ImageView)findViewById(R.id.card_turn);
		
		m_trucoView = (Button)findViewById(R.id.truco);
		m_trucoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { onTrucoClicked(); }
		});
		
		m_cardClosed = false;
		m_closeView = (Button)findViewById(R.id.close);
		m_closeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!m_cardClosed)
					m_closeView.setText(getResources().getString(R.string.open));
				else
					m_closeView.setText(getResources().getString(R.string.closed));
				m_cardClosed = !m_cardClosed;
			}
		});
		
		m_playersViews[0][0].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { onCardClicked(0); }
		});
		m_playersViews[0][1].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { onCardClicked(1); }
		});
		m_playersViews[0][2].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { onCardClicked(2); }
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		m_game.setRunning(false);
		m_game.interrupt();
		m_game = null;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		m_game.setRunning(true);
		if(m_game.getState() == Thread.State.NEW)
			m_game.start();

		updatePlayers();
		updateTurn();
		updateScore();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
		String name = prefs.getString("name", getResources().getString(R.string.player_0));

		TextView p1Name = (TextView)findViewById(R.id.player_0_name);
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
	
	private void onHandleMessage(Message message) {
		if(m_game == null)
			return;
		
		Game.Update update = Game.Update.values()[message.arg1];
		switch(update) {
		case PLAYER:
			updatePlayer(message.arg2);
			break;
		case PLAYERS:
			updatePlayers();
			break;
		case TURN:
			updateTurn();
			break;
		case SCORE:
			updateScore();
			break;
		case START_ROUND:
			processStartRound();
			break;
		case FINISH_ROUND:
			processFinishRound();
			break;
		case ROUND_WINNER:
			processRoundWinner(message.arg2);
			break;
		case CAN_CLOSE_CARD:
			updateCloseButton(message.arg2 != 0);
			break;
		}
	}
	
	private void onTrucoClicked() {
		
	}
	
	private void onCardClicked(int id) {
		if(m_game.playCard(id, m_cardClosed)) {
			synchronized(m_game) {
				m_game.notify();
			}
		}
	}
	
	private void updatePlayer(int id) {
		Player player = m_game.getPlayer(id);
		for(int i = 0; i < Player.CARDS; ++i) {
			ImageView imageView = m_playersViews[id][i];
			Card card = player.getCard(i);
			updateCard(imageView, card, id != 0);
		}
		
		Card playedCard = player.getPlayedCard();
		updateCard(m_playersViews[id][3], playedCard, false);
	}
	
	private void updatePlayers() {
		for(int i = 0; i < Game.PLAYERS; ++i)
			updatePlayer(i);
	}
	
	private void updateTurn() {
		Card card = m_game.getTurn();
		m_turnView.setImageResource(getCardResourceId(card));
	}
	
	private void updateScore() {
		for(int i = 0; i < m_teamsView.length; ++i) {
			Team team = m_game.getTeam(i);
			TextView textView = m_teamsView[i];
			textView.setText(String.format("Time %d: %d | %d", i+1, team.getScore(), team.getHandScore()));
		}
	}
	
	private void updateCard(ImageView cardView, Card card, boolean forceClose) {
		if(card == null)
			cardView.setVisibility(View.INVISIBLE);
		else {
			cardView.setVisibility(View.VISIBLE);
			if(!card.isVisible() || forceClose)
				cardView.setImageResource(getCardResourceId(null));
			else
				cardView.setImageResource(getCardResourceId(card));
		}
	}
	
	private void updateCloseButton(boolean canClose) {
		if(canClose)
			m_closeView.setVisibility(View.VISIBLE);
		else
			m_closeView.setVisibility(View.INVISIBLE);
	}
	
	private void processStartRound() {
		for(int i = 0; i < Game.PLAYERS; ++i) {
			ImageView cardView = m_playersViews[i][3];
			cardView.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
		}
	}
	
	private void processFinishRound() {
		for(int i = 0; i < Game.PLAYERS; ++i) {
			ImageView cardView = m_playersViews[i][3];
			cardView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		}
	}
	
	private void processRoundWinner(int id) {
		m_playersViews[id][3].setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
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
