package com.wingeon.truco.core;

public class Player {
	private Card m_cards[] = new Card[3];
	
	public Card playCard() {
		for(int i = 0; i < 3; ++i) {
			if(m_cards[i] != null) {
				Card card = m_cards[i];
				m_cards[i] = null;
				return card;
			}
		}
		return null;
	}
	
	public void receiveCard(Card card) {
		for(int i = 0; i < 3; ++i) {
			if(m_cards[i] == null) {
				m_cards[i] = card;
				break;
			}
		}
	}
	
	public Card getCard(int index) {
		return m_cards[index];
	}
}
