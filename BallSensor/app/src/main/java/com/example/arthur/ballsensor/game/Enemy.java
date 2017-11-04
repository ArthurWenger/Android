package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.PointF;

import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;

import java.util.ArrayList;
import java.util.Set;

/** Classe modélisant un ennemi (fantome) dans le jeu  **/
public class Enemy extends SimpleSprite{

	ArrayList<PointF> dirs = null;

	public Enemy(PointF location, float size, AssetManager assets, float speed) {
		super(location, size, assets, "ghost2.png");
		velocity.set( speed,0 );
	}

	/* @Override
	public void draw( Canvas canvas ) {
		spritePaint.setColor( Color.RED );
		canvas.drawCircle(getCenter().x, getCenter().y, radius, spritePaint);
		spritePaint.setColor( Color.BLACK );
		if(dirs!=null) {
			for ( PointF dir : dirs ) {
				canvas.drawCircle( dir.x, dir.y, radius, spritePaint );
			}
		}
	} */

	public void update( Set<LineSegment2D> nearbyWalls, float wallThickness, Hero hero) {
		setCenter( Math2D.add(getCenter(),velocity));

		 for (LineSegment2D wall : nearbyWalls) {
			if(detectAndResolveWallCollision(wall, wallThickness)) {
				setDirection(hero);
			}
		}

		//dirs = availableDirections(nearbyWalls, wallThickness);
		//setDirection2( hero, dirs );
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

	/* private ArrayList<PointF> availableDirections(Set<LineSegment2D> nearbyWalls, float wallThickness){
		PointF c = getCenter();

		PointF north = new PointF(c.x, c.y-2*radius);
		PointF south = new PointF(c.x, c.y+2*radius);
		PointF west = new PointF(c.x-2*radius, c.y);
		PointF east = new PointF(c.x+2*radius, c.y);

		ArrayList<PointF> directions = new ArrayList<PointF>( Arrays.asList(north, south, east, west ));

		for ( Iterator<LineSegment2D> wallIterator = nearbyWalls.iterator();  wallIterator.hasNext() && directions.size()>1; ) {
			LineSegment2D wall = wallIterator.next();
			for ( Iterator<PointF> dirIterator = directions.iterator(); dirIterator.hasNext(); ) {
				if(wall.intersectsCircle( dirIterator.next(), radius )){
					dirIterator.remove();
				}
			}
		}
		// for(int i =0; i<directions.size();i++){
		//	directions.set( i, Math2D.subtract( directions.get(i), c ));
		// }
		return directions;
	} */


	// TODO: compute free direction
	/*
	private void availableDirection(){
	}
	*/

	
	private void setDirection( Hero hero ) {
		float dirVelocity = (velocity.x==0)? velocity.y : velocity.x;
		dirVelocity = Math.abs( dirVelocity );
		PointF distVector = Math2D.subtract( hero.getCenter(), getCenter() );
		float absX = Math.abs( distVector.x );
		float absY = Math.abs( distVector.y );
		if(absX >= absY){
			velocity.x = (distVector.x>0)? dirVelocity : -dirVelocity;
			velocity.y = 0;
		} else{
			velocity.x = 0;
			velocity.y = (distVector.y>0)? dirVelocity : -dirVelocity;
		}
		//velocity.set(2.0f,0f);
		//velocity = Math2D.rotate(velocity,(float)(Math.random()*2.0*Math.PI));
	}

	/* private void setDirection2( Hero hero, ArrayList<PointF> directions ) {
		float dirVelocity = (velocity.x==0)? velocity.y : velocity.x;
		//Log.d("Directions", directions.toString());
		PointF max = directions.get( 0 );

		for(int i =1;i<directions.size();i++){
			PointF distVector = Math2D.subtract( hero.getCenter(), directions.get(i) );
			if(distVector.length()<max.length()){
				max = directions.get( i );
			}
		}
		max = Math2D.subtract( max, getCenter() );
		float absX = Math.abs( max.x );
		float absY = Math.abs( max.y );
		if(absX >= absY){
			velocity.x = (max.x>0)? dirVelocity : -dirVelocity;
			velocity.y = 0;
		} else{
			velocity.x = 0;
			velocity.y = (max.y>0)? dirVelocity : -dirVelocity;
		}
	} */
}
