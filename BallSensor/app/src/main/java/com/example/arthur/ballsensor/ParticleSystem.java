package com.example.arthur.ballsensor;

/**
 * Created by Arthur on 20/10/2017.
 */ /*
     * A particle system is just a collection of particles
     */
class ParticleSystem {
	static final int NUM_PARTICLES = 10;
	// coefficient de friction avec l'air et le support des balles
	private static final float sFriction = 0.1f;
	private long lastT =0;
	private float lastDeltaT =0;
	// diamètre des balles en metres
	private final float ballDiameter;
	private final float ballDiameter2;

	private Particle balls[] = new Particle[ NUM_PARTICLES ];

	ParticleSystem(float ballDiameter) {
            /*
             * Initially our particles have no speed or acceleration
             */
            this.ballDiameter = ballDiameter;
			ballDiameter2 = ballDiameter * ballDiameter;

		for ( int i = 0; i < balls.length; i++ ) {
			balls[ i ] = new Particle(sFriction);
		}
	}

	/*
	 * Mise à jour de la position de chaque balle dans le système en utilisation
	 * l'integrateur Vertlet.
	 */
	private void updatePositions( float sx, float sy, long timestamp ) {
		final long t = timestamp;
		if ( lastT != 0 ) {
			final float dT = (float) ( t - lastT ) * ( 1.0f / 1000000000.0f );
			if ( lastDeltaT != 0 ) {
				final float dTC = dT / lastDeltaT;
				final int count = balls.length;
				for ( int i = 0; i < count; i++ ) {
					balls[ i ].computePhysics( sx, sy, dT, dTC );
				}
			}
			lastDeltaT = dT;
		}
		lastT = t;
	}

	/*
	 * On effectue une itération de la simulation. On commence par
	 * mettre à jour la position de toutes les balles puis on detecte
	 * les collisions.
	 */
	public void update( float sx, float sy, long now, float mHorizontalBound, float mVerticalBound ) {
		// update the system's positions
		updatePositions( sx, sy, now );

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
		for ( int k = 0; k < NUM_MAX_ITERATIONS && more; k++ ) {
			more = false;
			for ( int i = 0; i < count; i++ ) {
				Particle curr = balls[ i ];
				for ( int j = i + 1; j < count; j++ ) {
					Particle ball = balls[ j ];
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
						final float c = ( 0.5f * ( ballDiameter - d ) ) / d;

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
