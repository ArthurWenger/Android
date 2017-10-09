package com.example.arthur.ballsensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomDrawableView extends View
{
	//private Bitmap mWood;
	//private Bitmap mBitmap;
	private float xPos = 100;
	private float yPos = 100;
	private float width;
	private float height;
	private final int radius = 50;
	private Paint paint= new Paint();

	public CustomDrawableView(Context context)
	{
		super(context);
		init();
	}
	public CustomDrawableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init(){
		paint.setColor(android.graphics.Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5);
	}

	protected void onDraw(Canvas canvas)
	{
		canvas.drawCircle( xPos, yPos, radius, paint );
	}

	public void moveBall( float x, float y){
		float newX = xPos+x;
		if(newX < width-radius && newX>radius)
			xPos = xPos+x;

		float newY = yPos+y;
		if(newY < height-radius && newY>radius)
			yPos = yPos + y;
		invalidate();
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		width = xNew;
		height = yNew;
	}
}