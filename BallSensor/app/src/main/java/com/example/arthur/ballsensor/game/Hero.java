package com.example.arthur.ballsensor.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.arthur.ballsensor.geometry.Math2D;
import com.example.arthur.ballsensor.sound.AudioPlayer;

import java.util.ArrayList;

/** Classe modélisant le héro (pacman) dans le jeu **/
public class Hero extends AnimatedSprite {

	private Bitmap pacmanSpriteSheet;
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

	public Hero(final PointF location, final float size, final AssetManager assets) {
		super(location, size);
		audioPlayer = new AudioPlayer( assets );
		pacmanSpriteSheet = bitmapFromAssetNamed( "pacman.png", assets);
	}

	public boolean detectCoinCollision(PointF coinCenter, float coinRadius) {
		if(Math2D.circleIntersection(getCenter(), radius, coinCenter, coinRadius)){
			playChompSound();
			return true;
		}
		return false;
	}

	public boolean detectAndResolveHeartCollision( PointF heartCenter, float heartRadius) {
		if(Math2D.circleIntersection(getCenter(), radius, heartCenter, heartRadius)) {
			playHeartSound();
			lives = Math.min( 5, lives+1);
			return true;
		}
		return false;		
	}

	public boolean detectFinishCollision(PointF finishLocation) {
		return Math2D.pointInCircle(finishLocation.x,finishLocation.y, getCenter(), radius);		
	}

	public void draw(android.graphics.Canvas canvas) {
		PointF currentCenter = getCenter();
		canvas.save();
		canvas.rotate(rotationInDegrees(), currentCenter.x, currentCenter.y);
			int animationFrameIndex;
			if ( Math.abs( velocity.x ) > minSpeed || Math.abs( velocity.y ) > minSpeed ) {
				animationFrameIndex = ( drawCounter / moveAnimationDrawsPerFrame ) % moveAnimationNumFrames;
			} else {
				animationFrameIndex = ( drawCounter / idleAnimationDrawsPerFrame ) % idleAnimationNumFrames;
			}

			int srcLeft = pacmanSpriteWidth * animationFrameIndex;
			Rect src = new Rect( srcLeft, 0, srcLeft + pacmanSpriteWidth, pacmanSpriteHeight );
			Rect test = unrotatedHeroRect();
		if(!invulnerable || drawCounter%8 <=4) {
			canvas.drawBitmap( pacmanSpriteSheet, src, test, heroPaint );
		}
		canvas.restore();
		++drawCounter;
	}

	private void startInvulnerableState(){
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				invulnerable = true;
				try {
					Thread.sleep(3000);
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
				invulnerable = false;
			}
		}).start();
	}

	/* public RotatedRect vulnerableRect() {
		RectF unrotatedHeroRectF = new RectF(unrotatedHeroRect());
		return new RotatedRect(unrotatedHeroRectF, new PointF(unrotatedHeroRectF.centerX(),unrotatedHeroRectF.centerY()), (rotationInDegrees()) * ((float)Math.PI/180.0f));
	} */

	public void setHeroListener(final HeroListener callback){
		this.heroListener = callback;
	}

	
	public void getHit(AnimatedSprite other) {
		if(!invulnerable) {
			playHitSound();
			lives = Math.max( 0, lives - 1 );
			if(lives>0){
				invulnerable = true;
				startInvulnerableState();
				if ( !other.getCenter().equals( this.getCenter() ) ) {
					temporaryAccelerations.add( Math2D.scale( Math2D.normalize( Math2D.subtract( getCenter(), other.getCenter() ) ), speed() + 2.2f ) );
				}
			} else if (heroListener !=null){
				playDeathSound();
				heroListener.notifyHeroDeath();
			}
		}
	}

	public int getLives(){
		return lives;
	}
	
	private Rect unrotatedHeroRect() {
		PointF center = getCenter();
		return new Rect((int)(center.x-radius),(int)(center.y-radius), (int)(center.x+radius), (int)(center.y+radius));
	}

	public void updateAccel( PointF acceleration ) {
		float accelerationLength = acceleration.length();
		if(accelerationLength > maxAccelerationLength) {
			acceleration = Math2D.scale(acceleration,maxAccelerationLength/accelerationLength);
		}
		this.acceleration = acceleration;
	}

	public void update() {
		float velocityLength = velocity.length();
		if(velocityLength > maxVelocityLength){
			velocity = Math2D.scale(velocity, maxVelocityLength/velocityLength);
		} /* else if(velocityLength > speed()) {
			velocity = Math2D.scale(velocity, speed()/velocity.length());
		} */

		if(velocity.length() > 0 ) {
			float angleBetweenInputAndFacing = Math2D.angle(velocity,facing);
			float angularAcceleration = -angleBetweenInputAndFacing * angularAccelerationScale;
			angularVelocity += angularAcceleration;
		}

		if(Math.abs(angularVelocity) > 0f) {
			float angularDrag = -angularVelocity*angularDragConstant;
			angularVelocity += angularDrag;
		}

		facing = Math2D.rotate(facing, angularVelocity);

		velocity = Math2D.add(velocity, acceleration);

		/*for(PointF accel : temporaryAccelerations) {
			velocity = Math2D.add(velocity, accel);
		}
		temporaryAccelerations.removeAll(temporaryAccelerations);*/

		if(speed() > 0.0f) {
			float dragMagnitude = speed()*dragConstant;
			PointF drag = Math2D.scale(velocity,-dragMagnitude/speed());
			velocity = Math2D.add(velocity, drag);
		}
		setCenter(Math2D.add(getCenter(),velocity));
	}

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
