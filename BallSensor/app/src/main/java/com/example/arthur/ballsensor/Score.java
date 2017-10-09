package com.example.arthur.ballsensor;

public class Score {

	private int prio;
	private String titre;
	private int value;

	public Score(int prio, String titre, int value) {
		this.titre = titre;
		this.value = value;
		if(prio>4 || prio <0)
			this.prio = 4;
		else
			this.prio = prio;
	}

	public Score(String titre, int value) {
		this.titre = titre;
		this.value = value;
		this.prio = 4;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio( int prio ) {
		this.prio = prio;
	}

	public String getTitre() {
		return titre;
	}

	public int getValue(){ return value; }

	public void setTitre( String titre ) {
		this.titre = titre;
	}
}