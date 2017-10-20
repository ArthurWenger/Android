package com.example.arthur.ballsensor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private ToggleButton mTOn;
	private Button mBPlus;
	private Button mBMoins;
	private SensorManager manager;
	private Sensor mAccelerometer;
	private boolean accelSupported;
	private MySensorListener sensorListener;
	private DrawableView ballView;
	private final int SCORES_ACTIVITY = 1;

	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private Display mDisplay;
	//private WakeLock mWakeLock;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		manager = (SensorManager) getSystemService( Service.SENSOR_SERVICE );
		mAccelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorListener = new MySensorListener();
		// Get an instance of the PowerManager
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mDisplay = mWindowManager.getDefaultDisplay();

		// Create a bright wake lock
		//mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
		//		                                                                            .getName());

		initView();
	}

	private void initView() {
		mTOn = (ToggleButton) findViewById( R.id.tOn );
		mBPlus = (Button) findViewById( R.id.bPlus );
		mBPlus.setOnClickListener( this );
		mBMoins = (Button) findViewById( R.id.bMoins );
		mBMoins.setOnClickListener( this );
		ballView = (DrawableView) findViewById(R.id.ballsView);
	}

	@Override
	public void onClick( View v ) {
		switch ( v.getId() ) {
			case R.id.bPlus:
				break;
			case R.id.bMoins:
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
         * It is not necessary to get accelerometer events at a very high
         * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
         * automatic low-pass filter, which "extracts" the gravity component
         * of the acceleration. As an added benefit, we use less power and
         * CPU resources.
         */
		accelSupported = manager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
		ballView.resume();
	}
	@Override
	public void onPause() {
		if (accelSupported)
			manager.unregisterListener(sensorListener, mAccelerometer);
		ballView.pause();
		super.onPause();
	}

	private class MySensorListener implements SensorEventListener {
		@Override
		public void onSensorChanged( SensorEvent event ) {
			if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
				return;
            /*
             * record the accelerometer data, the event's timestamp as well as
             * the current time. The latter is needed so we can calculate the
             * "present" time during rendering. In this application, we need to
             * take into account how the screen is rotated with respect to the
             * sensors (which always return data in a coordinate space aligned
             * to with the screen in its native orientation).
             */
            float sX, sY;

			switch (mDisplay.getRotation()) {
				case Surface.ROTATION_0:
					sX = event.values[0];
					sY = event.values[1];
					break;
				case Surface.ROTATION_90:
					sX = -event.values[1];
					sY = event.values[0];
					break;
				case Surface.ROTATION_180:
					sX = -event.values[0];
					sY = -event.values[1];
					break;
				case Surface.ROTATION_270:
					sX = event.values[1];
					sY = -event.values[0];
					break;
				default:
					sX = 0;
					sY = 0;
					break;
			}
			ballView.updateSensor( sX, sY);
		}

		@Override
		public void onAccuracyChanged( Sensor sensor, int accuracy ) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch ( item.getItemId() ) {
			case R.id.action_scores:
				Intent intent = new Intent( this, ScoresActivity.class );
				startActivityForResult( intent, SCORES_ACTIVITY );
				return true;
			case R.id.action_quit:
				finish();
				System.exit( 0 );
				return true;
		}
		return super.onOptionsItemSelected( item );
	}

	/* @Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		switch ( requestCode ) {
			case SCORES_ACTIVITY:
				switch ( resultCode ) {
					case RESULT_OK:
						break;
					case RESULT_CANCELED: break;
					default: break;
				}
				break;
			default: break;
		}
	} */



}
