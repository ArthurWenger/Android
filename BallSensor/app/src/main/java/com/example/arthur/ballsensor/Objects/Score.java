package com.example.arthur.ballsensor.Objects;

import java.io.Serializable;

public class Score implements Serializable {

	private int rank;
	//private String name;
	private int value;
	// a implementer
	private double lat;
	private double lng;

	public Score( int rank, int value, double lat, double lng)
	{
		this.rank = rank;
		this.value = value;
		this.lat = lat;
		this.lng = lng;
		/*
		if( rank >4 || rank <0)
			this.rank = 4;
		else
			this.rank = rank; */
	}

	/* public Score( int value) {
		//this.name = name;
		this.value = value;
		this.rank = 4;
	} */

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

	/*public String getName() {
		return name;
	}*/

	/*public void setName( String name ) {
		this.name = name;
	}*/
}