package com.example.arthur.ballsensor.old;

/**
 * Created by Arthur on 20/10/2017.
 */ /*
     * Each of our particle holds its previous and current position, its
     * acceleration. for added realism each particle has its own friction
     * coefficient.
     */
class BallOld {
	private float posX;
	private float posY;
	private float accelX;
	private float accelY;
	private float lastPosX;
	private float lastPosY;
	private float oneMinusFriction;
	private float slowFactor = 0.8f;

	BallOld( float sFriction) {
		// make each particle a bit different by randomizing its
		// coefficient of friction
		final float r = ( (float) Math.random() - 0.5f ) * 0.2f;
		oneMinusFriction = 1.0f - sFriction + r;
	}

	public void computePhysics( float sx, float sy, float dT, float dTC ) {
		// Force of gravity applied to our virtual object
		//final float m = 1000.0f; // mass of our virtual object
		//final float gx = -sx * m;
		//final float gy = -sy * m;
            /*
            * •F = mA <=> A = •F / m We could simplify the code by
            * completely eliminating "m" (the mass) from all the
            * equations, but it would hide the concepts from this
            * sample code.
            */
		//final float invm = 1.0f / m;
		//final float ax = gx * invm;
		//final float ay = gy * invm;

		final float ax = -sx*slowFactor;
		final float ay = -sy*slowFactor;

        /*
        * Time-corrected Verlet integration The position Verlet
        * integrator is defined as x(t+Æt) = x(t) + x(t) -
        * x(t-Æt) + a(t)Ætö2 However, the above equation doesn't
        * handle variable Æt very well, a time-corrected version
        * is needed: x(t+Æt) = x(t) + (x(t) - x(t-Æt)) *
        * (Æt/Æt_prev) + a(t)Ætö2 We also add a simple friction
        * term (f) to the equation: x(t+Æt) = x(t) +(1-f)
        * (x(t) - x(t-Æt)) * (Æt/Æt_prev) + a(t)Ætö2
        */
		final float dTdT = dT * dT;
		final float x = posX + oneMinusFriction * dTC * ( posX - lastPosX ) + accelX * dTdT;
		final float y = posY + oneMinusFriction * dTC * ( posY - lastPosY ) + accelY * dTdT;
		lastPosX = posX;
		lastPosY = posY;
		posX = x;
		posY = y;
		accelX = ax;
		accelY = ay;
	}

	/*
	 * Resolving constraints and collisions with the Verlet integrator
	 * can be very simple, we simply need to move a colliding or
	 * constrained particle in such way that the constraint is
	 * satisfied.
	 */
	public void resolveCollisionWithBounds( float mHorizontalBound, float mVerticalBound) {
		final float xmax = mHorizontalBound;
		final float ymax = mVerticalBound;
		final float x = posX;
		final float y = posY;
		if ( x > xmax ) {
			posX = xmax;
		} else if ( x < -xmax ) {
			posX = -xmax;
		}
		if ( y > ymax ) {
			posY = ymax;
		} else if ( y < -ymax ) {
			posY = -ymax;
		}
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosX( float mPosX ) {
		this.posX = mPosX;
	}

	public void setPosY( float mPosY ) {
		this.posY = mPosY;
	}
}
