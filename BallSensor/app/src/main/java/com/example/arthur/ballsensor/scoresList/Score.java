package com.example.arthur.ballsensor.scoresList;

import java.io.Serializable;

public class Score implements Serializable {

	private int rank;
	private int value;
	private double lat;
	private double lng;

	public Score( int rank, int value, double lat, double lng)
	{
		this.rank = rank;
		this.value = value;
		this.lat = lat;
		this.lng = lng;
	}

	public int getRank() {
		return rank;
	}

	public void setRank( int rank ) {
		this.rank = rank;
	}

	public int getValue(){ return value; }

	public double getLongitude() {
		return lng;
	}

	public double getLatitude() {
		return lat;
	}
}