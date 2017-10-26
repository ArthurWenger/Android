package com.example.arthur.ballsensor.Objects;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.arthur.ballsensor.Interfaces.AnimatedSprite;

import java.util.ArrayList;

public class Hero extends AnimatedSprite {

	Bitmap pacmanSpriteSheet;
	private int drawCounter = 0;
	private final int moveAnimationNumFrames = 7;
	private final int idleAnimationNumFrames = 2;
	private final int moveAnimationDrawsPerFrame = 2;
	private final int idleAnimationDrawsPerFrame = 14;
	private final int pacmanSpriteWidth = 72;
	private final int pacmanSpriteHeight = 72;

	//private final float dragConstant = 0.125f;
	private final float dragConstant = 0.1f;
	private final float angularDragConstant = 0.2f;

	private Paint heroPaint = new Paint();
	private PointF acceleration = new PointF(0f,0f);
	//private final float accelerationScale = 0.012f;
	private final float maxAccelerationLength = 1f;
	private final float maxVelocityLength = 10f;
	private final float angularAccelerationScale = 0.05f;
	private final float minSpeed = 0.05f;
	//private final float minAngularSpeed = 0.005f;
	//private final float brakeForceMagnitude = 0.1f;
	//private final float angularBrakeForceMagnitude = 0.006f;

	private int lives = 3;
	private boolean invulnerable = false;

	private ArrayList<PointF> temporaryAccelerations = new ArrayList<PointF>();

	public Hero(PointF location, float size, AssetManager assets) {
		super(location, size);
		pacmanSpriteSheet = bitmapFromAssetNamed( "pacman.png", assets);
	}

	public boolean detectCoinCollision(PointF coinCenter, float coinRadius) {
		return Math2D.circleIntersection(getCenter(), radius, coinCenter, coinRadius);
	}

	public boolean detectAndResolveExtraCollision( PointF extraCenter, float extraRadius) {
		if(Math2D.circleIntersection(getCenter(), radius, extraCenter, extraRadius)) {
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
	
	public void getHit(AnimatedSprite other) {
		if(!invulnerable) {
			invulnerable = true;
			startInvulnerableState();
			lives = Math.max( 0, lives - 1 );
			if ( !other.getCenter().equals( this.getCenter() ) ) {
				temporaryAccelerations.add( Math2D.scale( Math2D.normalize( Math2D.subtract( getCenter(), other.getCenter() ) ), speed() + 2.2f ) );
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

		/* public void update(PointF inputVector1, PointF inputVector2) {

		if(velocity.length() > speed()) {
			velocity = Math2D.scale(velocity, speed()/velocity.length());
		}

		if(inputVector1 == null) {
			if(speed() > minSpeed) {
				PointF brakeForce = Math2D.scale(velocity,-brakeForceMagnitude/speed());
				velocity = Math2D.add(velocity, brakeForce);
			}
			else {
				velocity.set(0,0);
			}
			if(Math.abs(angularVelocity) > minAngularSpeed) {
				float angularBrakeForce = (angularVelocity/Math.abs(angularVelocity)) * -angularBrakeForceMagnitude;
				angularVelocity += angularBrakeForce;
			}
			else {
				angularVelocity = 0.0f;
			}
			inputVector1 = new PointF(0,0);
		}

		if(inputVector1.length() > 0) {
			float angleBetweenInputAndFacing = Math2D.angle(inputVector1,facing);
			float angularAcceleration = -angleBetweenInputAndFacing * angularAccelerationScale;
			angularVelocity += angularAcceleration;
		}

		if(Math.abs(angularVelocity) > 0.0f) {
			float angularDrag = -angularVelocity*angularDragConstant;
			angularVelocity += angularDrag;
		}

		facing = Math2D.rotate(facing, angularVelocity);

		PointF acceleration = Math2D.scale(facing, inputVector1.length()*accelerationScale);
		float accelerationLength = acceleration.length();
		if(accelerationLength > maxAccelerationLength) {
			acceleration = Math2D.scale(acceleration,maxAccelerationLength/accelerationLength);
		}

		velocity = Math2D.add(velocity, acceleration);

		for(PointF accel : temporaryAccelerations) {
			velocity = Math2D.add(velocity, accel);
		}
		temporaryAccelerations.removeAll(temporaryAccelerations);

		if(speed() > 0.0f) {
			float dragMagnitude = speed()*dragConstant;
			PointF drag = Math2D.scale(velocity,-dragMagnitude/speed());
			velocity = Math2D.add(velocity, drag);

		}
		setCenter(Math2D.add(getCenter(),velocity));
	} */

}
