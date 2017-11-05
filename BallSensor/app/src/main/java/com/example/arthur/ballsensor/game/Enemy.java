package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;

import java.util.Set;

/** Classe modélisant un ennemi (fantome) dans le jeu  **/
public class Enemy extends SimpleSprite{

	public Enemy(PointF location, float size, AssetManager assets, float speed) {
		super(location, size, assets, "sprites/ghost.png" );//On appelle le constructeur de AnimatedSprite
		velocity.set( speed,0 );//On fixe la vitesse
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
		float dirVelocity = (velocity.x==0)? velocity.y : velocity.x;//On récupère la vitesse verticale si la vitesse horizontale est nulle et inversement.
		dirVelocity = Math.abs( dirVelocity );//Puis on prend la valeur absolue de cette valeur.
		PointF distVector = Math2D.subtract( hero.getCenter(), getCenter() );//On récupère la distance cartésienne entre l'ennemi et le héro.
		float absX = Math.abs( distVector.x );//On récupère cette valeur sur l'axe horizontal
		float absY = Math.abs( distVector.y );//puis vertical.
		if(absX >= absY){//Si la distance horizontale est supérieure à la distance verticale
			velocity.x = (distVector.x>0)? dirVelocity : -dirVelocity;//l'ennemi va se déplacer vers le héro horizontalement.
			velocity.y = 0;
		} else{//Sinon
			velocity.x = 0;
			velocity.y = (distVector.y>0)? dirVelocity : -dirVelocity;//l'ennemi va se déplacer vers le héro verticalement.
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
