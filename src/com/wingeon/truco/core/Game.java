package com.wingeon.truco.core;

import java.util.Random;

public class Game {
	private Random m_random = new Random();
	private Deck m_deck = new Deck();
	private Player m_players[] = new Player[4];
	private Team m_teams[] = new Team[2];
	private Card m_turn = null;
	
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

		startHand();
	}
	
	public void stop() {
		
	}
	
	public Player getPlayer(int id) {
		return m_players[id];
	}
	
	public Team getTeam(int id) {
		return m_teams[id];
	}
	
	public Card getTurn() {
		return m_turn;
	}
	
	private void startHand() {
		m_teams[0].resetHandScore();
		m_teams[1].resetHandScore();
		
		for(int i = 0; i < 3; ++i) {
			for(int p = 0; p < 4; ++p)
				m_players[p].receiveCard(m_deck.removeFirstCard());
		}
		
		m_turn = m_deck.removeFirstCard();
	}
	
	public static Card.Value[] getValueOrder() {
		return new Card.Value[]{Card.Value._4, Card.Value._5, Card.Value._6, Card.Value._7, Card.Value._Q, Card.Value._J, Card.Value._K, Card.Value._A, Card.Value._2, Card.Value._3};
	}
	
	public static Card.Suit[] getSuitOrder() {
		return new Card.Suit[]{Card.Suit.DIAMONDS, Card.Suit.SPADES, Card.Suit.HEARTS, Card.Suit.CLUBS};
	}
}
