package com.wingeon.truco.core;

public class Player {
	static int CARDS = 3;
	
	private Card m_cards[] = new Card[3];
	private Card m_playedCard = null;
	
	public void playCard() {
		for(int i = 0; i < CARDS; ++i) {
			if(m_cards[i] != null) {
				m_playedCard = m_cards[i];
				m_cards[i] = null;
			}
		}
	}
	
	public void receiveCard(Card card) {
		for(int i = 0; i < CARDS; ++i) {
			if(m_cards[i] == null) {
				m_cards[i] = card;
				break;
			}
		}
	}
	
	public void removePlayedCard() {
		m_playedCard = null;
	}
	
	public Card getCard(int index) {
		return m_cards[index];
	}
	
	public Card getPlayedCard() {
		return m_playedCard;
	}
}
