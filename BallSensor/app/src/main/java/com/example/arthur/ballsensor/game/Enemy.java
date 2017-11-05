package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;

import java.util.Set;

/**Classe permettant de représenter les Ennemis**/
public class Enemy extends AnimatedSprite {

	private final Bitmap ghostSpriteSheet;
	private final int spriteHalfWidth;
	private final int spriteHalfHeight;
	private final Paint ghostPaint = new Paint();

	/**Constructeur**/
	public Enemy(PointF location, float size, AssetManager assets) {
		super(location, size*0.8f);//On appelle le constructeur de AnimatedSprite
		velocity.set( 2f,0 );//On fixe la vitesse
		ghostSpriteSheet = bitmapFromAssetNamed("ghost2.png", assets);//On récupère l'image correspondant à un ennemi
		spriteHalfWidth = ghostSpriteSheet.getWidth()/2;//On récupère la moitié de la largeur de la hitbox de l'ennemi
		spriteHalfHeight = ghostSpriteSheet.getHeight()/2;//On récupère la moitié de la hauteur de la hitbox de l'ennemi
	}

	/**Méthode draw, permettant de dessiner l'ennemi à l'écran**/
	@Override
	public void draw(android.graphics.Canvas canvas) {
		PointF c = getCenter();//On récupère la position du centre de l'ennemi
		canvas.drawBitmap(ghostSpriteSheet, c.x-spriteHalfWidth, c.y-spriteHalfHeight, ghostPaint);//Et on le dessine à l'écran.
	}

	/**Méthode permettant de mettre à jour la direction dans laquelle se déplace l'ennemi**/
	public void update( Set<LineSegment2D> nearbyWalls, float wallThickness, Hero hero) {
		setCenter( Math2D.add(getCenter(),velocity));//On déplace l'ennemi à sa nouvelle position
		for (LineSegment2D wall : nearbyWalls) {//Pour tous les murs
			if(detectAndResolveWallCollision(wall, wallThickness)) {//Si un mur entre en collision avec l'ennemi
				setDirection(hero);//L'ennemi se déplace dans la direction du héro.
			}
		}
	}

	/**Méthode permettant de détecter et résoudre les collision avec le joueur**/
	public boolean detectAndResolveCollisionWithHero(Hero hero) {//On récupère le héro en paramètre
		PointF hC = hero.getCenter();//On récupère la position du héro
		float hRadius = hero.getRadius();//et sa taille
		if(Math2D.circleIntersection( hC, hRadius, getCenter(), radius )) {//Si le héro est dans la hitbox de l'ennemi
			hero.getHit( this );//Le héro prend un coup
			return true;//et on revoie vrai pour l'indiquer.
		}
		return false;//Sinon, on renvoie faux.
	}

	/**Méthode permettant de diriger l'ennemi dans la direction du héro**/
	private void setDirection( Hero hero ) {//On récupère le héro en paramètre.
		PointF distVector = Math2D.subtract( hero.getCenter(), getCenter() );//On récupère la distance cartésienne entre l'ennemi et le héro.
		float absX = Math.abs( distVector.x );//On récupère cette valeur sur l'axe horizontal
		float absY = Math.abs( distVector.y );//puis vertical.
		if(absX >= absY){//Si la distance horizontale est supérieure à la distance verticale
			velocity.x = (distVector.x>0)? 2f : -2f;//l'ennemi va se déplacer vers le héro horizontalement.
			velocity.y = 0;
		} else{//Sinon
			velocity.x = 0;
			velocity.y = (distVector.y>0)? 2f : -2f;//l'ennemi va se déplacer vers le héro verticalement.
		}
	}
}
