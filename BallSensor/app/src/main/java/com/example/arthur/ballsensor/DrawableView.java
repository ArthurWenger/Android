/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.arthur.ballsensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This is an example of using the accelerometer to integrate the device's
 * acceleration to a position using the Verlet method. This is illustrated with
 * a very simple particle system comprised of a few iron balls freely moving on
 * an inclined wooden table. The inclination of the virtual table is controlled
 * by the device's accelerometer.
 * 
 * @see SensorManager
 * @see SensorEvent
 * @see Sensor
 */

public class DrawableView extends SurfaceView implements Runnable {

	private boolean isRunning = false;
	private Thread gameThread;
	private SurfaceHolder holder;

	private final static int MAX_FPS = 40; //desired fps
	private final static int FRAME_PERIOD = 1000 / MAX_FPS; // the frame period


    // diameter of the balls in meters
    private final float sBallDiameter = 0.002f;

    private float dpiX;
    private float dpiY;
    private float metersToPixelsX;
    private float metersToPixelsY;
    private Bitmap ballBitmap;
    private Bitmap woodBitmap;
    private float originX;
    private float originY;
    private float sensorX;
    private float sensorY;
    private float horizontalBound;
    private float verticalBound;
    private final ParticleSystem particleSystem = new ParticleSystem(sBallDiameter);

    public DrawableView( Context context, AttributeSet attr)
    {
        super(context, attr);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        dpiX = metrics.xdpi;
        dpiY = metrics.ydpi;
        metersToPixelsX = dpiX / 0.0254f;
        metersToPixelsY = dpiY / 0.0254f;

        // rescale the ball so it's about 0.5 cm on screen
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        final int dstWidth = (int) (sBallDiameter * metersToPixelsX + 0.5f);
        final int dstHeight = (int) (sBallDiameter * metersToPixelsY + 0.5f);
        ballBitmap = Bitmap.createScaledBitmap(ball, dstWidth, dstHeight, true);

        Options opts = new Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        woodBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wood, opts);

	    holder = getHolder();
	    holder.addCallback(new SurfaceHolder.Callback() {
		    @Override
		    public void surfaceCreated(SurfaceHolder holder) {
		    }

		    @Override
		    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			    originX = (width - ballBitmap.getWidth()) * 0.5f;
			    originY = (height - ballBitmap.getHeight()) * 0.5f;
			    horizontalBound = ((width / metersToPixelsX - sBallDiameter) * 0.5f);
			    verticalBound = ((height / metersToPixelsY - sBallDiameter) * 0.5f);
		    }

		    @Override
		    public void surfaceDestroyed(SurfaceHolder holder) {
		    }
	    });
    }

    public void updateSensor( float sX, float sY){
        sensorX =sX;
        sensorY =sY;
    }


   /* @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // compute the origin of the screen relative to the origin of
            // the bitmap
            originX = (w - ballBitmap.getWidth()) * 0.5f;
            originY = (h - ballBitmap.getHeight()) * 0.5f;
            horizontalBound = ((w / metersToPixelsX - sBallDiameter) * 0.5f);
            verticalBound = ((h / metersToPixelsY - sBallDiameter) * 0.5f);
        } */


        protected void render(Canvas canvas) {
	        // draw the background
            canvas.drawBitmap( woodBitmap, 0, 0, null);

            final int count = particleSystem.getParticleCount();
            for (int i = 0; i < count; i++) {
                /*
                 * We transform the canvas so that the coordinate system matches
                 * the sensors coordinate system with the origin in the center
                 * of the screen and the unit is the meter.
                 */
                final float x = originX + particleSystem.getPosX(i) * metersToPixelsX;
                final float y = originY - particleSystem.getPosY(i) * metersToPixelsY;
                canvas.drawBitmap( ballBitmap, x, y, null);
            }
        }

        private void step(){
			/*
             * compute the new position of our object, based on accelerometer
             * data and present time.
             */
	        particleSystem.update( sensorX, sensorY, FRAME_PERIOD/1000.0f, horizontalBound, verticalBound );
        }


	/**
	 * Start or resume the game.
	 */
	public void resume() {
		isRunning = true;
		gameThread = new Thread(this);
		gameThread.start();
	}

	/**
	 * Pause the game loop
	 */
	public void pause() {
		isRunning = false;
		boolean retry = true;
		while (retry) {
			try {
				gameThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
	}

	@Override
	public void run() {
		while(isRunning) {
			// We need to make sure that the surface is ready
			if (! holder.getSurface().isValid()) {
				continue;
			}
			long started = System.currentTimeMillis();

			// update
			step();
			// draw
			Canvas canvas = holder.lockCanvas();
				if (canvas != null) {
				render(canvas);
				holder.unlockCanvasAndPost(canvas);
			}

			float deltaTime = (System.currentTimeMillis() - started);
			int sleepTime = (int) (FRAME_PERIOD - deltaTime);
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				}
				catch (InterruptedException e) {
				}
			}
			while (sleepTime < 0) {
				step();
				sleepTime += FRAME_PERIOD;
			}
		}
	}
}
