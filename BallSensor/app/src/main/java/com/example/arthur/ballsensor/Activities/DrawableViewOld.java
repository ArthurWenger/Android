package com.example.arthur.ballsensor.Activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawableViewOld extends View
{
	//private Bitmap mWood;
	//private Bitmap mBitmap;
	private float xPosition = 100;
	private float yPosition = 100;
	private float width;
	private float height;
	private final int radius = 50;
	//private final int speedMultiplier = 2;
	public float xAcceleration, xVelocity, yAcceleration, yVelocity = 0.0f;
	public float frameTime = 0.666f;

	private Paint paint= new Paint();

	public DrawableViewOld( Context context)
	{
		super(context);
		init();
	}
	public DrawableViewOld( Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init(){
		paint.setAntiAlias( true );
		paint.setColor(android.graphics.Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5);
	}

	protected void onDraw(Canvas canvas){
		drawCircle(canvas);
		drawObstacle(canvas);
	}



	private void drawObstacle(Canvas canvas){
		canvas.drawRect( 100,100,100,100,paint );
	}
	private void drawCircle(Canvas canvas){
		canvas.drawCircle( xPosition, yPosition, radius, paint );
	}

	/* public void moveBall( float x, float y){
		float newX = xPos - speedMultiplier*x;
		if(newX < width-radius && newX>radius)
			xPos = newX;

		float newY = yPos + speedMultiplier*y;
		if(newY < height-radius && newY>radius)
			yPos = newY;
		invalidate();
	} */

	public void setAcceleration(float xA, float yA){
		xAcceleration = -xA;
		yAcceleration = -yA;
		updatePosition();
	}

	private void updatePosition(){
		float dTdT = frameTime * frameTime;

		float newX = xPosition + xAcceleration * dTdT;
		float newY = yPosition + xAcceleration * dTdT;
		//Log.i("X",""+newX);
		//Log.i("Y",""+newY);

		if(newX < width-radius && newX>radius)
			xPosition = newX;

		if(newY < height-radius && newY>radius)
			yPosition = newY;

		//Log.i("width",""+width);
		//Log.i("height",""+height);
		invalidate();
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		width = xNew;
		height = yNew;
	}
}