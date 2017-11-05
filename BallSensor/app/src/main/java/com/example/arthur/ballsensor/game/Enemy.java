package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.PointF;

import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;

import java.util.Set;

/** Classe modélisant un ennemi (fantome) dans le jeu  **/
public class Enemy extends SimpleSprite{

	public Enemy(PointF location, float size, AssetManager assets, float speed) {
		super(location, size, assets, "sprites/ghost.png" );
		velocity.set( speed,0 );
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
		PointF hC = hero.getCenter();
		float hRadius = hero.getRadius();
		if(Math2D.circleIntersection( hC, hRadius, getCenter(), radius )) {
			hero.getHit( this );
			return true;
		}
		return false; 
	}
	
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
	}

	// TODO: ameliorer le deplacement des ennemis

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
}
