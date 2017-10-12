package com.example.arthur.ballsensor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	private float xAcc, yAcc;
	DrawableView ballView;
	private final int SCORES_ACTIVITY = 1;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		manager = (SensorManager) getSystemService( Service.SENSOR_SERVICE );
		mAccelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorListener = new MySensorListener();
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
		accelSupported = manager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	}
	@Override
	public void onPause() {
		if (accelSupported)
			manager.unregisterListener(sensorListener, mAccelerometer);
		super.onPause();
	}

	private class MySensorListener implements SensorEventListener {
		@Override
		public void onSensorChanged( SensorEvent event ) {
			switch(event.sensor.getType()){
				case Sensor.TYPE_ACCELEROMETER:
					xAcc = event.values[0];
					yAcc = event.values[1];
					ballView.setAcceleration(xAcc,yAcc);
					break;
				case Sensor.TYPE_GYROSCOPE:
					//mTvRes.setText( "x="+ event.values[0]+"\ny="+event.values[1]+"\nz="+event.values[2]);
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					//mTvRes.setText( "x="+ event.values[0]+"\ny="+event.values[1]+"\nz="+event.values[2]);
					break;
				case Sensor.TYPE_PROXIMITY:
					//mTvRes.setText("dist="+ event.values[0]);
					break;
				default:
					break;
			}
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
