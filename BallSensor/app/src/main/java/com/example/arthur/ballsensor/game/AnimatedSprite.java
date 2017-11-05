package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.Log;

import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;

import java.io.IOException;
import java.io.InputStream;

/** Classe abstraite permettant de représenter les objets du jeu  **/
public abstract class AnimatedSprite {

	private PointF center = new PointF();
	private PointF prevCenter = new PointF();
	protected PointF velocity = new PointF();

	protected PointF facing = new PointF(1.0f,0.0f);
	protected float angularVelocity = 0;

	protected float radius = 25;

	/**Constructeur**/
	public AnimatedSprite( PointF location, float size) {
		center.set(location);//On définit ici le centre de l'objet
		prevCenter.set(location);//et ici son ancient centre, identique au centre courant car l'objet est nouveau
		radius = size;//et on récupère sa taille.
	}

	/**Méthode pour récupérer la position de l'objet**/
	public PointF getLocation() {
		return center;
	}//Pour récupérer sa position, on renvoie son centre

	/**Méthode de détection et de résolution des collisions**/
	public boolean detectAndResolveWallCollision( LineSegment2D wall, float wallThickness) {//On récupère en paramètres un mur et son épaisseur
		PointF currentOffset = wall.circleIntersectionResolutionOffset(center, radius, wallThickness);//On récupère le déplacement après une éventuelle collision
		if(currentOffset != null) {//Et si ce déplacement n'est pas vide
			center.offset(currentOffset.x,currentOffset.y);//On remplace le déplacement actuel par celui ci.
			return true;//Et on renvoe vrai pour indiquer une collision
		}
		return false;//sinon, on renvoie faux.
	}

	/**Méthode abstraite draw**/
	public abstract void draw(android.graphics.Canvas canvas);

	/**Méthode pour obtenir un objet graphique**/
	protected Bitmap bitmapFromAssetNamed(String assetFileName, AssetManager assets) {
		Bitmap spriteSheet = null;
		InputStream spriteSheetStream;
		try {
			spriteSheetStream = assets.open(assetFileName);
			spriteSheet = BitmapFactory.decodeStream(spriteSheetStream);
			spriteSheetStream.close();
		} catch (IOException e) {
			Log.wtf("AnimatedSprite", e.getLocalizedMessage());
		}
		return spriteSheet;
	}

	/**Méthode pour obtenir l'angle de rotation de l'objet en degrés**/
	protected float rotationInDegrees() {
		return (float) (Math.atan2(facing.y, facing.x) * 180/Math.PI) + 90;
	}

	/**Méthode pour obtenir la vitesse de l'objet**/
	protected float speed() {
		return Math2D.subtract(center, prevCenter).length();
	}

	/**Méthode pour obtenir le centre de l'objet**/
	public PointF getCenter() {
		return center;
	}

	/**Méthode permettant de "déplacer" l'objet vers une nouvelle position**/
	protected void setCenter(PointF p) {//On récupère een paramètre la nouvelle position de l'objet
		prevCenter.set(center);//on enregistre l'ancienne position
		center.set(p);//et on remplace par la nouvelle.
	}

	/**Méthode pour récupérer la taille de l'objet**/
	public float getRadius(){
		return radius;
	}
}
