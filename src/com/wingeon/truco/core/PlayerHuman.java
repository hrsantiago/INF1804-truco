package com.wingeon.truco.core;

public class PlayerHuman extends Player {
	private boolean m_isLocal = true;
	
	@Override
	public boolean isVirtual() {
		return false;
	}

	@Override
	public boolean isHuman() {
		return true;
	}
	
	public boolean isLocal() {
		return m_isLocal;
	}
	
	public void playCard(int id, boolean closed) {
		m_playedCard = m_cards[id];
		m_playedCard.setVisible(!closed);
		m_cards[id] = null;
	}
}
