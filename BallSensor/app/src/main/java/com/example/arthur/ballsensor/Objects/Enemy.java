package com.example.arthur.ballsensor.Objects;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.arthur.ballsensor.Interfaces.AnimatedSprite;

import java.util.Set;

public class Enemy extends AnimatedSprite {

	private Hero hero;
	private Bitmap ghostSpriteSheet;
	private final int ghostSpriteWidth = 128;
	private final int ghostSpriteHeight = 128;
	private Paint ghostPaint = new Paint();

	public Enemy(PointF location, float size, AssetManager assets, Hero hero) {
		super(location, size*0.6f);
		velocity.set( 2f,0 );
		this.hero = hero;
		ghostSpriteSheet = bitmapFromAssetNamed("ghost.png", assets);
	}
	
	public void draw(android.graphics.Canvas canvas) {
		//canvas.drawCircle(getCenter().x, getCenter().y, radius, new Paint());
		Rect src = new Rect(0,0,ghostSpriteWidth,ghostSpriteHeight);
		canvas.drawBitmap(ghostSpriteSheet, src, unrotatedGhostRect(), ghostPaint);
	}
	
	public void update(Set<LineSegment2D> nearbyWalls, float wallThickness) {		
		setCenter(Math2D.add(getCenter(),velocity));
		for (LineSegment2D wall : nearbyWalls) {
			if(detectAndResolveWallCollision(wall, wallThickness)) {
				setDirection();
			}
		}
	}
	
	public boolean detectAndResolveCollisionWithHero(Hero hero) {
		RotatedRect vulnerableRect = hero.vulnerableRect();
		if(vulnerableRect.intersectsCircle(getCenter(),radius)) {
			hero.getHit(this);
		}
		/*RotatedRect dangerousRect = hero.dangerousRect();
		if(dangerousRect != null && dangerousRect.intersectsCircle(getCenter(),radius)) {
			return true;
		}*/
		return false; 
	}

	public boolean crossRoad(){
		return true;
	}


	
	public void setDirection() {
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

	private Rect unrotatedGhostRect() {

		float sizeOffset = speed()*0.4f;
		float ghostHalfWidth = radius - sizeOffset;
		float ghostHalfHeight = radius + sizeOffset;
		PointF center = getCenter();
		return new Rect((int)(center.x-ghostHalfWidth),(int)(center.y-ghostHalfHeight), (int)(center.x+ghostHalfWidth), (int)(center.y+ghostHalfHeight));
	}

}
