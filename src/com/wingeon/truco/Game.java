package com.wingeon.truco;

import java.util.Random;

public class Game {
	private Random m_random = new Random();
	private Deck m_deck = new Deck();
	
	void start() {
		m_random.setSeed(0);
		m_deck.fillFrench();
		m_deck.shuffle(m_random);
		m_deck.removeCardOfValue(Card.Value.Card_8);
		m_deck.removeCardOfValue(Card.Value.Card_9);
		m_deck.removeCardOfValue(Card.Value.Card_10);
		m_deck.dump();
	}
	
	void stop() {
		
	}
}
