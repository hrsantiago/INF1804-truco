package com.wingeon.truco;

public class Card {
	public enum Suit {
		Card_Hearts,
		Card_Diamonds,
		Card_Clubs,
		Card_Spades,
	};

	public enum Value {
		Card_A,
		Card_2,
		Card_3,
		Card_4,
		Card_5,
		Card_6,
		Card_7,
		Card_8,
		Card_9,
		Card_10,
		Card_J,
		Card_Q,
		Card_K,
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
