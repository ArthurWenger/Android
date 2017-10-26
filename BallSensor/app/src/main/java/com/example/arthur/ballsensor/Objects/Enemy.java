package com.example.arthur.ballsensor.Objects;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.arthur.ballsensor.Interfaces.AnimatedSprite;

import java.util.Set;

public class Enemy extends AnimatedSprite {

	private Bitmap ghostSpriteSheet;
	private final int spriteHalfWidth;
	private final int spriteHalfHeight;
	private Paint ghostPaint = new Paint();

	public Enemy(PointF location, float size, AssetManager assets) {
		super(location, size*0.8f);
		velocity.set( 2f,0 );
		ghostSpriteSheet = bitmapFromAssetNamed("ghost2.png", assets);
		spriteHalfWidth = ghostSpriteSheet.getWidth()/2;
		spriteHalfHeight = ghostSpriteSheet.getHeight()/2;
	}
	
	public void draw(android.graphics.Canvas canvas) {
		//canvas.drawCircle(getCenter().x, getCenter().y, radius, new Paint());
		PointF c = getCenter();
		canvas.drawBitmap(ghostSpriteSheet, c.x-spriteHalfWidth, c.y-spriteHalfHeight, ghostPaint);
	}
	
	public void update(Set<LineSegment2D> nearbyWalls, float wallThickness, Hero hero) {
		setCenter(Math2D.add(getCenter(),velocity));
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
	private boolean crossRoad(){
		return true;
	}

	// TODO: compute free direction
	private void availableDirection(){
		//Rect src = unrotatedGhostRect();
		// top
		//src.offset( ghostSpriteWidth,0 );
		//if(src)

	}

	
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
