package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class SimpleSprite extends Sprite{
	private Bitmap spriteSheet;
	private final Paint spritePaint = new Paint();
	private final int spriteHalfWidth;
	private final int spriteHalfHeight;

	public SimpleSprite( PointF location, float size, AssetManager assets, String spriteName ) {
		super( location, size );
		spriteSheet = bitmapFromAssetNamed( spriteName, assets );
		spriteHalfWidth = spriteSheet.getWidth() / 2;
		spriteHalfHeight = spriteSheet.getHeight() / 2;
	}

	@Override
	public void draw( Canvas canvas ) {
		PointF c = getCenter();
		canvas.drawBitmap( spriteSheet, c.x-spriteHalfWidth, c.y-spriteHalfHeight, spritePaint );
		//canvas.drawCircle(getCenter().x, getCenter().y, radius, heartPaint);
	}
}
