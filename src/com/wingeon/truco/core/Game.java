package com.wingeon.truco.core;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class Game extends Thread {
	public enum Update {
		PLAYERS,
		PLAYER,
		TURN,
		SCORE,
		START_ROUND,
		FINISH_ROUND,
		ROUND_WINNER,
		CAN_CLOSE_CARD,
		FINISH_GAME,
	};
	
	public static int TEAMS = 2;
	public static int PLAYERS = 4;
	public static int MAX_POINTS = 12;
	public static int FINISH_ROUND_DURATION = 2000;
	public static int FINISH_HAND_DURATION = 1500;
	
	private Messenger m_messenger = null;
	private boolean m_isRunning = false;
	private Random m_random;
	private Deck m_deck = null;
	private Player m_players[] = new Player[PLAYERS];
	private Team m_teams[] = new Team[TEAMS];
	private Card m_turnCard;
	private int m_currentPlayerId;
	private int m_roundPlays;
	private int m_handPlays;
	private int m_lastHandFirstPlayer;
	private boolean m_canLocalPlayCard;
	
	public Game(Messenger messenger) {
		super();
		m_messenger = messenger;
	}
	
	@Override
	public void start() {
		m_random = new Random();
		//m_random.setSeed(0);
		
		m_teams[0] = new Team();
		m_teams[1] = new Team();
		
		m_players[0] = new PlayerHuman();
		m_players[0].setTeam(m_teams[0]);
		m_players[1] = new PlayerVirtual();
		m_players[1].setTeam(m_teams[1]);
		m_players[2] = new PlayerVirtual();
		m_players[2].setTeam(m_teams[0]);
		m_players[3] = new PlayerVirtual();
		m_players[3].setTeam(m_teams[1]);
		
		m_deck = new Deck();
		m_deck.fillFrench();
		m_deck.removeCardsOfValue(Card.Value._8);
		m_deck.removeCardsOfValue(Card.Value._9);
		m_deck.removeCardsOfValue(Card.Value._10);
		m_deck.shuffle(m_random);
		
		m_isRunning = true;
		m_turnCard = null;
		m_currentPlayerId = 0;
		m_roundPlays = 0;
		m_handPlays = 0;
		m_lastHandFirstPlayer = m_random.nextInt(PLAYERS);
		m_canLocalPlayCard = false;
		
		super.start();
	}
	
	@Override
	public void run() {
		synchronized(this) {
			boolean handFinished = true;
			while(m_isRunning) {
				if(m_roundPlays == 0) {
					if(handFinished) {
						startHand();
						handFinished = false;
					}
					startRound();
				}
				
				Player player = m_players[m_currentPlayerId];
				if(player.isHuman()) {
					try {
						emitUpdate(Update.CAN_CLOSE_CARD, (m_roundPlays == 1 || m_roundPlays == 2) ? 1 : 0, null);
						m_canLocalPlayCard = true;
						this.wait();
						m_canLocalPlayCard = false;
						emitUpdate(Update.CAN_CLOSE_CARD, 0, null);
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
				
				++m_roundPlays;
				if(m_roundPlays >= PLAYERS) {
					++m_handPlays;
					handFinished = finishRound();
					m_roundPlays = 0;
				}
				
				if(handFinished)
					finishHand();
			}
		}
	}
	
	public boolean playCard(int id, boolean closed) {
		if(m_canLocalPlayCard) {
			PlayerHuman player = (PlayerHuman)m_players[0];
			if(m_roundPlays == 0 || m_roundPlays == 3)
				closed = false;
			player.playCard(id, closed);
			return true;
		}
		return false;
	}
	
	public void setRunning(boolean running) {
		m_isRunning = running;
	}
	
	public boolean isRunning() {
		return m_isRunning;
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
		for(int i = 0; i < Player.CARDS; ++i) {
			for(int p = 0; p < PLAYERS; ++p)
				m_players[p].receiveCard(m_deck.removeFirstCard());
		}
		emitUpdate(Update.PLAYERS, 0, null);
		
		m_turnCard = m_deck.removeFirstCard();
		emitUpdate(Update.TURN, 0, null);
		
		m_currentPlayerId = m_lastHandFirstPlayer + 1;
		if(m_currentPlayerId >= PLAYERS)
			m_currentPlayerId = 0;
		m_lastHandFirstPlayer = m_currentPlayerId;
	}
	
	private void finishHand() {
		if(m_teams[0].getHandScore() > m_teams[1].getHandScore())
			m_teams[0].addPoint();
		else if(m_teams[1].getHandScore() > m_teams[0].getHandScore())
			m_teams[1].addPoint();
		
		m_teams[0].resetHandScore();
		m_teams[1].resetHandScore();
		
		emitUpdate(Update.SCORE, 0, null);
		
		for(int p = 0; p < PLAYERS; ++p) {
			Player player = m_players[p];
			for(int i = 0; i < Player.CARDS; ++i) {			
				Card card = player.getCard(i);
				if(card != null)
					m_deck.insertCard(card);
			}
			player.removeCards();
		}
		
		m_deck.insertCard(m_turnCard);
		m_turnCard = null;
		emitUpdate(Update.TURN, 0, null);
		
		m_handPlays = 0;
		
		if(m_teams[0].getScore() >= MAX_POINTS || m_teams[1].getScore() >= MAX_POINTS) {
			setRunning(false);
			emitUpdate(Update.FINISH_GAME, m_teams[0].getScore() > m_teams[1].getScore() ? 1 : 0, null);
		}
		else {
			try {
				sleep(FINISH_HAND_DURATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void startRound() {
		emitUpdate(Update.START_ROUND, 0, null);
	}
	
	private boolean finishRound() {
		emitUpdate(Update.FINISH_ROUND, 0, null);
		
		processRoundWinner();
		
		try {
			sleep(FINISH_ROUND_DURATION);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < PLAYERS; ++i) {
			Player player = m_players[i];
			Card card = player.getPlayedCard();
			m_deck.insertCard(card);
			player.removePlayedCard();
		}
		emitUpdate(Update.PLAYERS, 0, null);
		
		boolean handFinished = false;
		if(m_handPlays == 3)
			handFinished = true;
		else if(m_handPlays == 2) {
			if(Math.abs(m_teams[0].getHandScore() - m_teams[1].getHandScore()) >= 1)
				handFinished = true;
		}
		return handFinished;
	}
	
	private void processRoundWinner() {
		Card.Suit[] aOrderSuit = getSuitOrder();
		List<Card.Suit> listSuit = Arrays.asList(aOrderSuit);
		
		Card.Value[] aOrderValue = getValueOrder();
		List<Card.Value> listValue = Arrays.asList(aOrderValue);
		int turnIndex = listValue.indexOf(m_turnCard.getValue());
		if(++turnIndex == aOrderValue.length)
			turnIndex = 0;
		
		Card.Value shackle = aOrderValue[turnIndex];
		
		Card winnerCard = null;
		for(int i = 0; i < PLAYERS; ++i) {
			Card card = m_players[i].getPlayedCard();
			if(card.isVisible()) {
				if(winnerCard == null)
					winnerCard = card;
				else {
					if(winnerCard.getValue() == shackle && card.getValue() == shackle) {
						int index = listSuit.indexOf(card.getSuit());
						int winnerIndex = listSuit.indexOf(winnerCard.getSuit());
						if(index > winnerIndex)
							winnerCard = card;
					}
					else if(winnerCard.getValue() != shackle && card.getValue() == shackle)
						winnerCard = card;
					else if(winnerCard.getValue() != shackle && card.getValue() != shackle) {
						int index = listValue.indexOf(card.getValue());
						int winnerIndex = listValue.indexOf(winnerCard.getValue());
						if(index > winnerIndex)
							winnerCard = card;
					}
				}
			}
		}
		
		Vector<Team> winnerTeams = new Vector<Team>(TEAMS);
		for(int i = 0; i < PLAYERS; ++i) {
			Player player = m_players[i];
			Card card = player.getPlayedCard();
			
			if(card.getValue() == winnerCard.getValue()) {
				Team team = player.getTeam();
				if(!winnerTeams.contains(team)) {
					player.getTeam().addHandPoint();
					winnerTeams.add(team);
					m_currentPlayerId = i;
				}
				emitUpdate(Update.ROUND_WINNER, i, null);
			}
		}
		emitUpdate(Update.SCORE, 0, null);
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
