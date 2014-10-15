package com.wingeon.truco.core;

public class Team {
	private int m_score = 0;
	private int m_handScore = 0;
	//private Player m_players[] = new Player[2];
	
	public void addHandPoint() {
		m_handScore += 1;
	}
	
	public int getHandScore() {
		return m_handScore;
	}
	
	public void resetHandScore() {
		m_handScore = 0;
	}
	
	public void addPoints(int points) {
		m_score += points;
	}
	
	public int getScore() {
		return m_score;
	}
	
	public void resetScore() {
		m_score = 0;
	}
}
