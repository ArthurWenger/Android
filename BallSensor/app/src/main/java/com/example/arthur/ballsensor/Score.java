package com.example.arthur.ballsensor;

public class Score {

	private int rank;
	private String name;
	private int value;
	// a implementer
	private double lat;
	private double lng;

	public Score( int rank, String name, int value) {
		this.name = name;
		this.value = value;
		if( rank >4 || rank <0)
			this.rank = 4;
		else
			this.rank = rank;
	}

	public Score( String name, int value) {
		this.name = name;
		this.value = value;
		this.rank = 4;
	}

	public int getRank() {
		return rank;
	}

	public void setRank( int rank ) {
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public int getValue(){ return value; }

	public void setName( String name ) {
		this.name = name;
	}
}