package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.PointF;

import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;

import java.util.Set;

/** Classe modélisant un ennemi (fantome) dans le jeu  **/
public class Enemy extends SimpleSprite{

	public Enemy(PointF location, float size, AssetManager assets) {
		super(location, size, assets, "ghost2.png");
		velocity.set( 2f,0 );
	}
	
	public void update( Set<LineSegment2D> nearbyWalls, float wallThickness, Hero hero) {
		setCenter( Math2D.add(getCenter(),velocity));
		for (LineSegment2D wall : nearbyWalls) {
			if(detectAndResolveWallCollision(wall, wallThickness)) {
				setDirection(hero);
			}
		}
	}
	
	public boolean detectAndResolveCollisionWithHero(Hero hero) {
		//RotatedRect vulnerableRect = hero.vulnerableRect();
		PointF hC = hero.getCenter();
		float hRadius = hero.getRadius();
		if(Math2D.circleIntersection( hC, hRadius, getCenter(), radius )) {
			hero.getHit( this );
			return true;
		}
		/* if(vulnerableRect.intersectsCircle(getCenter(),radius)) {
			hero.getHit(this);
		} */
		return false; 
	}

	// TODO: detect intersection
	/*
	private boolean crossRoad(){
		return true;
	}
	*/

	// TODO: compute free direction
	/*
	private void availableDirection(){
	}
	*/

	
	private void setDirection( Hero hero ) {
		PointF distVector = Math2D.subtract( hero.getCenter(), getCenter() );
		float absX = Math.abs( distVector.x );
		float absY = Math.abs( distVector.y );
		if(absX >= absY){
			velocity.x = (distVector.x>0)? 2f : -2f;
			velocity.y = 0;
		} else{
			velocity.x = 0;
			velocity.y = (distVector.y>0)? 2f : -2f;
		}
		//velocity.set(2.0f,0f);
		//velocity = Math2D.rotate(velocity,(float)(Math.random()*2.0*Math.PI));
	}
}
