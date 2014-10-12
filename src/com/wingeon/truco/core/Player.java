package com.wingeon.truco.core;

public abstract class Player {
	public static int CARDS = 3;
	
	protected Card m_cards[] = new Card[3];
	protected Card m_playedCard = null;
	protected Team m_team = null;
	
	public abstract boolean isVirtual();
	public abstract boolean isHuman();
	
	public void receiveCard(Card card) {
		for(int i = 0; i < CARDS; ++i) {
			if(m_cards[i] == null) {
				m_cards[i] = card;
				break;
			}
		}
	}
	
	public void removeCards() {
		for(int i = 0; i < CARDS; ++i)
			m_cards[i] = null;
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
	
	public void setTeam(Team team) {
		m_team = team;
	}
	
	public Team getTeam() {
		return m_team;
	}
}
