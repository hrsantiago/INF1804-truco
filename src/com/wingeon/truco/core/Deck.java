package com.wingeon.truco.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class Deck {
	private Vector<Card> m_cards;
	
	public Deck() {}
	
	void fillFrench() {
		m_cards = new Vector<Card>(52);
		for(Card.Suit suit : Card.Suit.values()) {
			for(Card.Value value : Card.Value.values()) {
				m_cards.add(new Card(value, suit));
			}
		}
	}
	
	void shuffle(Random random) {
		Collections.shuffle(m_cards, random);
	}
	
	void insertCard(Card card) {
		card.setVisible(true);
		m_cards.add(card);
	}
	
	Card removeFirstCard() {
		if(m_cards.size() > 0) {
			Card card = m_cards.firstElement();
			m_cards.remove(0);
			return card;
		}
		return null;
	}
	
	Card removeCard(Card.Value value, Card.Suit suit) {
		Iterator<Card> it = m_cards.iterator();
		while (it.hasNext()) {
			Card card = it.next();
		    if(card.getValue() == value && card.getSuit() == suit) {
		    	it.remove();
		    	return card;
		    }
		}
		return null;
	}
	
	void removeCardsOfValue(Card.Value value) {
		Iterator<Card> it = m_cards.iterator();
		while (it.hasNext()) {
			Card card = it.next();
		    if(card.getValue() == value)
		    	it.remove();
		}
	}
	
	void dump() {
		for(Card card : m_cards)
			card.dump();
		System.out.println("Number of cards: " + m_cards.size());
	}
}
