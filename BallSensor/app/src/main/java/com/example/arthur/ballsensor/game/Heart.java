package com.example.arthur.ballsensor.game;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class Heart extends AnimatedSprite {

	private final Bitmap heartSpriteSheet;
	private final float spriteHalfWidth;
	private final float spriteHalfHeight;
	private final Paint heartPaint = new Paint();

	public Heart( PointF location, float size, AssetManager assets ) {
		super( location, size*0.7f );
		heartSpriteSheet = bitmapFromAssetNamed( "heart.png", assets );
		spriteHalfWidth = heartSpriteSheet.getWidth() / 2;
		spriteHalfHeight = heartSpriteSheet.getHeight()/2;
	}

	@Override
	public void draw( Canvas canvas ) {
		PointF c = getCenter();
		canvas.drawBitmap( heartSpriteSheet, c.x-spriteHalfWidth, c.y-spriteHalfHeight, heartPaint );
		//canvas.drawCircle(getCenter().x, getCenter().y, radius, heartPaint);
	}
}
