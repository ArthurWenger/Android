package com.example.arthur.ballsensor.game;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**Classe permettant de gérer les Coeurs**/
public class Heart extends AnimatedSprite {

	private final Bitmap heartSpriteSheet;
	private final float spriteHalfWidth;
	private final float spriteHalfHeight;
	private final Paint heartPaint = new Paint();

	/**Constructeur**/
	public Heart( PointF location, float size, AssetManager assets ) {
		super( location, size*0.7f );//On appelle le constructeur de AnimatedSprite.
		heartSpriteSheet = bitmapFromAssetNamed( "heart.png", assets );//On récupère l'image correspondant à un coeur.
		spriteHalfWidth = heartSpriteSheet.getWidth()/2;//On récupère la moitié de la largeur de la hitbox du coeur.
		spriteHalfHeight = heartSpriteSheet.getHeight()/2;//On récupère la moitié de la hauteur de la hitbox du coeur.
	}

	/**Méthode draw, permettant de dessiner le coeur à l'écran**/
	@Override
	public void draw( Canvas canvas ) {
		PointF c = getCenter();//On récupère la position du centre du coeur
		canvas.drawBitmap( heartSpriteSheet, c.x-spriteHalfWidth, c.y-spriteHalfHeight, heartPaint );//Et on le dessine à l'écran.
	}
}
