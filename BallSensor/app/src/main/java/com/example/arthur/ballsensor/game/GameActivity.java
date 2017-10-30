package com.example.arthur.ballsensor.game;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.gameover.GameOverActivity;
import com.example.arthur.ballsensor.gameover.GameOverListener;
import com.example.arthur.ballsensor.location.LocationListener;
import com.example.arthur.ballsensor.location.SingleShotLocationProvider;

public class GameActivity extends AppCompatActivity implements GameOverListener, LocationListener {

	private SensorManager manager;
	private Sensor mAccelerometer;
	private boolean accelSupported;
	private MySensorListener sensorListener;
	private MazeGameView mazeView;
	Double[] location =null;

	// private final int SCORES_ACTIVITY = 1;
	private static final int GAMEOVER_ACTIVITY = 1;
	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_game );
		// on demande la position du joueur pour pouvoir inscrire son score dans la base de données par la suite
		// le premier this est pour le contexte et le deuxieme est pour le callback onNewLocationAvailable
		SingleShotLocationProvider.requestSingleUpdate(this, this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		manager = (SensorManager) getSystemService( Service.SENSOR_SERVICE );
		mAccelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorListener = new MySensorListener();

		initView();
	}

	private void initView() {
		mazeView = (MazeGameView) findViewById(R.id.mazeView );
		mazeView.setGameOverListener(this);
	}

	@Override
	public void notifyOfGameOver(int finalScore) {
		Intent gameOverIntent = new Intent(this, GameOverActivity.class);
		gameOverIntent.putExtra( "score",finalScore );
		gameOverIntent.putExtra( "location", location );
		startActivityForResult( gameOverIntent, GAMEOVER_ACTIVITY );
		overridePendingTransition(R.anim.enter, R.anim.exit);
	}

	@Override
	public void onResume() {
		super.onResume();
		/* Il n'est pas nécéssaire d'avoir un taux de rafraichissement très élevé pour l'accelerometre.
		 * En utilisant la sensibilité SENSOR_DELAY_UI on économise la batterie et les ressources du CPU */
		accelSupported = manager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
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
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				// on met à jour le jeu avec les données de l'accelerometre
				mazeView.updateAccel( -event.values[ 0 ], event.values[ 1 ] );
			}
		}

		@Override
		public void onAccuracyChanged( Sensor sensor, int accuracy ) {
		}
	}


	/* @Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch ( item.getItemId() ) {
			case R.id.action_scores:
				Intent intent = new Intent( this, ScoresActivity.class );
				startActivity( intent );
				return true;
			case R.id.action_quit:
				finish();
				System.exit( 0 );
				return true;
		}
		return super.onOptionsItemSelected( item );
	} */

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		switch ( requestCode ) {
			case GAMEOVER_ACTIVITY:
				switch ( resultCode ) {
					case RESULT_OK:
						mazeView.newGame();
						break;
					case RESULT_CANCELED:
						finish();
						break;
					default: break;
				}
				break;
			default: break;
		}
	}

	@Override
	public void onNewLocationAvailable( Double[] location ) {
		Log.d("Location", "my location is :" + location[0].toString()+" "+location[1].toString());
		this.location = location;
	}

	@Override
	public void onPermissionNeeded() {
		Log.d("Location", "requestNeeded callback reached");
		ActivityCompat.requestPermissions( this,
				new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
				MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		Log.d("Location", "onRequestPermissionResult callback reached");
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
				// Si la demande de permission est refusée,
				if (grantResults.length > 0
						    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					SingleShotLocationProvider.requestSingleUpdate(this, this);

				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		mazeView.stopTimer();
		super.onDestroy();
	}



}
