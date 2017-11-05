package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.arthur.ballsensor.geometry.Math2D;
import com.example.arthur.ballsensor.sound.AudioPlayer;

import java.util.ArrayList;

/**Classe permettant de représenter le Héro**/
public class Hero extends AnimatedSprite {

	Bitmap pacmanSpriteSheet;
	private int drawCounter = 0;
	private final int moveAnimationNumFrames = 7;
	private final int idleAnimationNumFrames = 2;
	private final int moveAnimationDrawsPerFrame = 2;
	private final int idleAnimationDrawsPerFrame = 14;
	private final int pacmanSpriteWidth = 72;
	private final int pacmanSpriteHeight = 72;
	private final float dragConstant = 0.1f;
	private final float angularDragConstant = 0.2f;
	private Paint heroPaint = new Paint();
	private PointF acceleration = new PointF(0f,0f);
	private final float maxAccelerationLength = 1f;
	private final float maxVelocityLength = 10f;
	private final float angularAccelerationScale = 0.05f;
	private final float minSpeed = 0.05f;
	private HeroListener heroListener;
	private AudioPlayer audioPlayer;

	private int lives = 2;
	private boolean invulnerable = false;

	private ArrayList<PointF> temporaryAccelerations = new ArrayList<PointF>();

	/**Constructeur**/
	public Hero(final PointF location, final float size, final AssetManager assets) {
		super(location, size);//On appelle le constructeur de AnimatedSprite.
		audioPlayer = new AudioPlayer( assets );//On récupère le lecteur audio.
		pacmanSpriteSheet = bitmapFromAssetNamed( "pacman.png", assets);//On récupère l'image du héro.
	}

	/**Méthode permettant de détecter et résoudre les collision avec les pièces**/
	public boolean detectCoinCollision(PointF coinCenter, float coinRadius) {
		if(Math2D.circleIntersection(getCenter(), radius, coinCenter, coinRadius)){//S'il y a collision avec une pièce
			playChompSound();//On joue le son de l'acquisition d'une pièce
			return true;//Et on renvoie vrai pour le signaler.
		}
		return false;//Sinon, on renvoie faux.
	}

	/**Méthode permettant de détecter et résoudre les collision avec un coeur**/
	public boolean detectAndResolveHeartCollision( PointF heartCenter, float heartRadius) {
		if(Math2D.circleIntersection(getCenter(), radius, heartCenter, heartRadius)) {//S'il y a collision avec un coeur
			playHeartSound();//On joue le son de l'acquisition d'un coeur
			lives = Math.min( 5, lives+1);//On ajoute une vie au héro
			return true;//et on renvoie vrai pour le signaler.
		}
		return false;//Sinon, on renvoie faux.
	}

	/**Méthode permettant de détecter la collision avec la fin du niveau**/
	public boolean detectFinishCollision(PointF finishLocation) {
		return Math2D.pointInCircle(finishLocation.x,finishLocation.y, getCenter(), radius);//renvoie true si il y a collision avec la fin du niveau, false sinon.
	}

	/**Méthode draw, permettant de dessiner le héro à l'écran**/
	public void draw(android.graphics.Canvas canvas) {
		PointF currentCenter = getCenter();//On récupère la position courante du héro
		canvas.save();//On enregistre l'état actuel de l'écran
		canvas.rotate(rotationInDegrees(), currentCenter.x, currentCenter.y);//On fait tourner le contenu selon l'angle obtenu en appelant rotationInDegrees()
			int animationFrameIndex;
			if ( Math.abs( velocity.x ) > minSpeed || Math.abs( velocity.y ) > minSpeed ) {//Si la vitesse horizontale ou verticale est supérieure à la vitesse minimale
				animationFrameIndex = ( drawCounter / moveAnimationDrawsPerFrame ) % moveAnimationNumFrames;//On met à jour la valeur de l'index pour représenter un mouvement
			} else {//Sinon
				animationFrameIndex = ( drawCounter / idleAnimationDrawsPerFrame ) % idleAnimationNumFrames;//On met à jour la valeur de l'index pour indiquer que le héro rest sur place.
			}
		/**Ici, on récupère l'angle de rotation du dessin, de telle sorte que la "bouche" du héro soit toujours dans la direction dans laquelle il se déplace**/
			int srcLeft = pacmanSpriteWidth * animationFrameIndex;
			Rect src = new Rect( srcLeft, 0, srcLeft + pacmanSpriteWidth, pacmanSpriteHeight );
			Rect test = unrotatedHeroRect();
		if(!invulnerable || drawCounter%8 <=4) {//Si le héro n'est pas invulnérable ou on n'a pas déjà trop demandé de redessin
			canvas.drawBitmap( pacmanSpriteSheet, src, test, heroPaint );//On dessine le héro sur le canvas
		}
		canvas.restore();//on met à jour le canvas
		++drawCounter;//Et on incrémente le conteur de dessin.
	}

	/**Méthode permettant de rendre le héro invulnérable**/
	private void startInvulnerableState(){
		new Thread(new Runnable()//On créé un nouveau Thread, définit comme suit:
		{
			@Override
			public void run()//Quand le thread fonctionne:
			{
				invulnerable = true;//on met la variable invulnerable à true
				try {//On éssaye
					Thread.sleep(3000);//de faire dormir le thread pendant 3 secondes,
				} catch ( InterruptedException e ) {//Si ça ne marche pas
					e.printStackTrace();//On reçoit l'erreur en ligne de commande
				}
				invulnerable = false;//Et on met invulnerable à false.
			}
		}).start();//Et on lance le thread
	}

	/**Méthode permettant de mettre l'écouteur sur le héro**/
	public void setHeroListener(final HeroListener callback){
		this.heroListener = callback;
	}

	/**Méthode permettant de détecter et gérer un coup**/
	public void getHit(AnimatedSprite other) {
		if(!invulnerable) {//Si le héro n'est pas invulnérable
			playHitSound();//On joue le son d'un coup.
			lives = Math.max( 0, lives - 1 );//on retire une vie
			if(lives>0){//S'il reste encore des vies
				invulnerable = true;//on met invulnerable à true
				startInvulnerableState();//Et on met le héro en état invulnérable
				if ( !other.getCenter().equals( this.getCenter() ) ) {//Si le sprite qui a donné un coup au héro n'est pas au même endroit que le héro
					temporaryAccelerations.add( Math2D.scale( Math2D.normalize( Math2D.subtract( getCenter(), other.getCenter() ) ), speed() + 2.2f ) );//On accélère temporairement le héro.
				}
			} else if (heroListener !=null){//Sinon, si il existe un écouteur sur le héro
				playDeathSound();//On joue la musique de mort du héro
				heroListener.notifyHeroDeath();//Et on l'écouteur notifie de la mort du héro.
			}
		}
	}

	/**Méthode permettant de récupérer le nombre de vies restantes**/
	public int getLives(){
		return lives;
	}

	/**Méthode permettant de récupérer la taille de la hitbox avant rotation du héro**/
	private Rect unrotatedHeroRect() {
		PointF center = getCenter();//On récupère la position du héro
		return new Rect((int)(center.x-radius),(int)(center.y-radius), (int)(center.x+radius), (int)(center.y+radius));//Et on renvoie un rectangle centré sur celle-ci de taille appropriée.
	}

	/**Méthode permettant de mettre à jour l'accélération du héro**/
	public void updateAccel( PointF acceleration ) {//On récupère en paramètre le nouveau point d'accélération
		float accelerationLength = acceleration.length();//On récupère la valeur de la nouvelle accélération
		if(accelerationLength > maxAccelerationLength) {//Si cette valeur est supérieure à la valeur maximale d'accélération
			acceleration = Math2D.scale(acceleration,maxAccelerationLength/accelerationLength);//On la remet à l'échelle
		}
		this.acceleration = acceleration;//Et on met à jour la valeur courante de l'accélération
	}

	/**Méthode permettant de mettre à jour la vitesse du héro**/
	public void update() {
		float velocityLength = velocity.length();//On récupère la vitesse du héro
		if(velocityLength > maxVelocityLength){//Si cette vitesse est supérieure à la vitesse maximale
			velocity = Math2D.scale(velocity, maxVelocityLength/velocityLength);//On la remet à l'échelle.
		}

		if(velocity.length() > 0 ) {//Si la vitesse est positive
			float angleBetweenInputAndFacing = Math2D.angle(velocity,facing);//on récupère l'angle entre la vitesse courante et la direction vers laquelle est tourné le héro
			float angularAcceleration = -angleBetweenInputAndFacing * angularAccelerationScale;//On calcule l'accélération angulaire
			angularVelocity += angularAcceleration;//Et l'ajoute à la vitesse angulaire.
		}

		if(Math.abs(angularVelocity) > 0f) {//Si la valeur absolue de la vitesse angulaire est supérieure à 0
			float angularDrag = -angularVelocity*angularDragConstant;//On calcule la traînée angulaire
			angularVelocity += angularDrag;//Et on l'enlève à la vitesse angulaire.
		}

		facing = Math2D.rotate(facing, angularVelocity);//On met à jour la direction vers laquelle est tourné le héro
		velocity = Math2D.add(velocity, acceleration);//On ajoute l'accélération à la vitesse.

		if(speed() > 0.0f) {//Si la vitesse de déplacement du héro est supérieure à 0
			float dragMagnitude = speed()*dragConstant;//On calcule la magnitude de trainée
			PointF drag = Math2D.scale(velocity,-dragMagnitude/speed());//Puis le point qui représente la traînée elle-même
			velocity = Math2D.add(velocity, drag);//On met à jour la vitesse du héro
		}
		setCenter(Math2D.add(getCenter(),velocity));//Et on déplace le héro.
	}

	/**Méthodes permettant de jouer les sons appropriés à la mort du héro, à la prise d'un coup, à la capture d'une pièce, et à la capture d'un coeur**/
	private void playDeathSound(){
		audioPlayer.startPlayer("pacman_death.ogg", 1f, false);
	}
	private void playHitSound(){
		audioPlayer.startPlayer("pacman_hit.ogg", 1f, false);
	}
	private void playChompSound(){
		audioPlayer.startPlayer("pacman_chomp.ogg", 0.5f, false);
	}
	private void playHeartSound(){
		audioPlayer.startPlayer("pacman_coin.ogg", 3f, false);
	}

}
