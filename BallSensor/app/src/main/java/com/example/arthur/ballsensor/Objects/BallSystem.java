package com.example.arthur.ballsensor.Objects;

/**
 * Created by Arthur on 20/10/2017.
 */ /*
     * A particle system is just a collection of particles
     */
public class BallSystem {
	static final int NUM_PARTICLES = 10;
	// coefficient de friction avec l'air et le support des balles
	private static final float sFriction = 0.1f;
	// diamètre des balles en metres
	private final float ballDiameter;
	private final float ballDiameter2;

	private Ball balls[] = new Ball[ NUM_PARTICLES ];

	public BallSystem( float ballDiameter ) {
            /*
             * Initially our particles have no speed or acceleration
             */
            this.ballDiameter = ballDiameter;
			ballDiameter2 = ballDiameter * ballDiameter;

		for ( int i = 0; i < balls.length; i++ ) {
			balls[ i ] = new Ball(sFriction);
		}
	}

	/*
	 * Mise à jour de la position de chaque balle dans le système en utilisation
	 * l'integrateur Vertlet.
	 */
	private void updatePositions( float sx, float sy, float dT ) {
		final int count = balls.length;
		for ( int i = 0; i < count; i++ ) {
			// ici dT = 0.025 car FPS = 40 donc dT = 1/40 = 0.025
			// dTC = 1 car dT = lastDeltaT donc dT/lastDeltaT = 1
			balls[ i ].computePhysics( sx, sy, dT, 1 );
		}
	}

	/*
	 * On effectue une itération de la simulation. On commence par
	 * mettre à jour la position de toutes les balles puis on detecte
	 * les collisions.
	 */
	public void update( float sx, float sy, float dT, float mHorizontalBound, float mVerticalBound ) {
		// update the system's positions
		updatePositions( sx, sy, dT );

		// We do no more than a limited number of iterations
		final int NUM_MAX_ITERATIONS = 10;

        /*
         * Resolve collisions, each particle is tested against every
         * other particle for collision. If a collision is detected the
         * particle is moved away using a virtual spring of infinite
         * stiffness.
         */
		boolean more = true;
		final int count = balls.length;
		// il est possible que le deplacement d'une balle après une collision entraine une nouvelle collision
		// on doit donc vérifier les collisions en chaine
		for ( int k = 0; k < NUM_MAX_ITERATIONS && more; k++ ) {
			more = false;
			for ( int i = 0; i < count; i++ ) {
				Ball curr = balls[ i ];
				for ( int j = i + 1; j < count; j++ ) {
					Ball ball = balls[ j ];
					float cx = curr.getPosX();
					float cy = curr.getPosY();
					float bx = ball.getPosX();
					float by = ball.getPosY();

					float dx = bx - cx;
					float dy = by - cy;
					float dd = dx * dx + dy * dy;
					// Check for collisions
					if ( dd <= ballDiameter2 ) {
	                    /*
	                     * add a little bit of entropy, after nothing is
	                     * perfect in the universe.
	                     */
						dx += ( (float) Math.random() - 0.5f ) * 0.0001f;
						dy += ( (float) Math.random() - 0.5f ) * 0.0001f;
						dd = dx * dx + dy * dy;
						// simulate the spring
						final float d = (float) Math.sqrt( dd );
						// spring damping (how much the spring releases energy)
						final float c = 0.5f * ( ballDiameter - d )  / d;
						//Log.i("C",""+c);

						curr.setPosX( cx - dx * c);
						curr.setPosY( cy - dy * c);
						ball.setPosX( bx + dx * c);
						ball.setPosY(by + dy * c);
						more = true;
					}
				}
                /*
                 * Finally make sure the particle doesn't intersects
                 * with the walls.
                 */
				curr.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);
			}
		}
	}

	public int getParticleCount() {
		return balls.length;
	}

	public float getPosX( int i ) {
		return balls[ i ].getPosX();
	}

	public float getPosY( int i ) {
		return balls[ i ].getPosY();
	}
}
