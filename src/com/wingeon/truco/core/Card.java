package com.wingeon.truco.core;

public class Card {
	public enum Suit {
		HEARTS,
		DIAMONDS,
		CLUBS,
		SPADES,
	};

	public enum Value {
		_A,
		_2,
		_3,
		_4,
		_5,
		_6,
		_7,
		_8,
		_9,
		_10,
		_J,
		_Q,
		_K,
	};
	
	private Suit m_suit;
	private Value m_value;
	
	public Card(Value value, Suit suit) {
		m_value = value;
		m_suit = suit;
	}
	
	public Value getValue() {
		return m_value;
	}
	
	public Suit getSuit() {
		return m_suit;
	}
	
	public void dump() {
		System.out.println(m_value.name() + " of " + m_suit.name());
	}
}
