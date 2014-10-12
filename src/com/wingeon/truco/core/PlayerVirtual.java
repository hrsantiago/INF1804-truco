package com.wingeon.truco.core;

public class PlayerVirtual extends Player {
	
	private int THINK_DURATION = 700;

	@Override
	public boolean isVirtual() {
		return true;
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	public void playFirstCard() {
		try {
			Thread.sleep(THINK_DURATION);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i = 0; i < CARDS; ++i) {
			if(m_cards[i] != null) {
				m_playedCard = m_cards[i];
				m_cards[i] = null;
				break;
			}
		}
	}
}
