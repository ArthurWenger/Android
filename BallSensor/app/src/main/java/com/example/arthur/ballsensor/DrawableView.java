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
import android.view.View;

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

public class DrawableView extends View {

    // diameter of the balls in meters
    private final float sBallDiameter = 0.004f;

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
    private long sensorTimeStamp;
    private long cpuTimeStamp;
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
    }

    public void updateSensor( float sX, float sY, long sTime, long cpuTime){
        sensorX =sX;
        sensorY =sY;
        sensorTimeStamp = sTime;
        cpuTimeStamp = cpuTime;
	    invalidate();
    }


    @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // compute the origin of the screen relative to the origin of
            // the bitmap
            originX = (w - ballBitmap.getWidth()) * 0.5f;
            originY = (h - ballBitmap.getHeight()) * 0.5f;
            horizontalBound = ((w / metersToPixelsX - sBallDiameter) * 0.5f);
            verticalBound = ((h / metersToPixelsY - sBallDiameter) * 0.5f);
        }

        @Override
        protected void onDraw(Canvas canvas) {

	        // draw the background
            canvas.drawBitmap( woodBitmap, 0, 0, null);

            /*
             * compute the new position of our object, based on accelerometer
             * data and present time.
             */
            final long now = sensorTimeStamp + (System.nanoTime() - cpuTimeStamp );

	        particleSystem.update( sensorX, sensorY, now, horizontalBound, verticalBound );

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

            // and make sure to redraw asap
        }
    }
