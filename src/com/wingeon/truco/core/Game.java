package com.wingeon.truco.core;

import java.util.Random;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class Game extends Thread {
	public enum Update {
		PLAYERS,
		PLAYER,
		TURN,
		SCORE,
	};
	
	public static int PLAYERS = 4;
	
	private Messenger m_messenger = null;
	private boolean m_isRunning = false;
	private Random m_random = new Random();
	private Deck m_deck = new Deck();
	private Player m_players[] = new Player[PLAYERS];
	private Team m_teams[] = new Team[2];
	private Card m_turnCard = null;
	private int m_currentPlayerId = 0;
	
	public Game(Messenger messenger) {
		super();
		m_messenger = messenger;
	}
	
	@Override
	public void start() {
		//m_random.setSeed(0);
		
		m_teams[0] = new Team();
		m_teams[1] = new Team();
		
		m_players[0] = new PlayerHuman();
		m_players[1] = new PlayerVirtual();
		m_players[2] = new PlayerVirtual();
		m_players[3] = new PlayerVirtual();
		
		m_deck.fillFrench();
		m_deck.removeCardsOfValue(Card.Value._8);
		m_deck.removeCardsOfValue(Card.Value._9);
		m_deck.removeCardsOfValue(Card.Value._10);
		m_deck.shuffle(m_random);
		
		m_isRunning = true;
		
		super.start();
	}
	
	@Override
	public void run() {
		synchronized(this) {
			while(m_isRunning) {
				startHand();
				
				for(int i = 0; i < PLAYERS; ++i) {
					Player player = m_players[m_currentPlayerId];
					if(player.isHuman()) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						PlayerVirtual playerVirtual = (PlayerVirtual)player;
						playerVirtual.playFirstCard();
					}
					emitUpdate(Update.PLAYER, m_currentPlayerId, null);
					
					++m_currentPlayerId;
					if(m_currentPlayerId >= PLAYERS)
						m_currentPlayerId = 0;
				}
				
				finishHand();
			}
		}
	}
	
	public void setRunning(boolean running) {
		m_isRunning = running;
	}
	
	public Player getPlayer(int id) {
		return m_players[id];
	}
	
	public Team getTeam(int id) {
		return m_teams[id];
	}
	
	public Card getTurn() {
		return m_turnCard;
	}
	
	private void startHand() {
		m_teams[0].resetHandScore();
		m_teams[1].resetHandScore();
		
		for(int i = 0; i < Player.CARDS; ++i) {
			for(int p = 0; p < PLAYERS; ++p)
				m_players[p].receiveCard(m_deck.removeFirstCard());
		}
		
		m_turnCard = m_deck.removeFirstCard();
		emitUpdate(Update.TURN, 0, null);
		emitUpdate(Update.PLAYERS, 0, null);
	}
	
	private void finishHand() {
		// compared played cards
		// add points to teams
		// check team points to update score
		// collect all played cards
		// collect all hand cards if finished
	}
	
	private void emitUpdate(Update update, int arg2, Object obj) {
		Message message = Message.obtain();
		message.arg1 = update.ordinal();
		message.arg2 = arg2;
		message.obj = obj;
		try {
			m_messenger.send(message);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Card.Value[] getValueOrder() {
		return new Card.Value[]{Card.Value._4, Card.Value._5, Card.Value._6, Card.Value._7, Card.Value._Q, Card.Value._J, Card.Value._K, Card.Value._A, Card.Value._2, Card.Value._3};
	}
	
	public static Card.Suit[] getSuitOrder() {
		return new Card.Suit[]{Card.Suit.DIAMONDS, Card.Suit.SPADES, Card.Suit.HEARTS, Card.Suit.CLUBS};
	}
}
