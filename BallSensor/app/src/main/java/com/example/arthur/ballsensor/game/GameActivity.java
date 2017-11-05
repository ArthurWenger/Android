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
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.gameover.GameOverActivity;
import com.example.arthur.ballsensor.gameover.GameOverListener;
import com.example.arthur.ballsensor.location.LocationListener;
import com.example.arthur.ballsensor.location.SingleShotLocationProvider;
import com.example.arthur.ballsensor.scores.ScoresActivity;

public class GameActivity extends AppCompatActivity implements GameOverListener, LocationListener {

	private SensorManager manager;
	private Sensor mAccelerometer;
	private boolean accelSupported;
	private MySensorListener sensorListener;
	private GameView mazeView;
	Double[] location =null;

	private static final int GAMEOVER_ACTIVITY = 1;
	private static final int SCORES_ACTIVITY = 2;
	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_game );
		// on demande la position du joueur pour pouvoir inscrire son score dans la base de données par la suite
		// le premier this est pour le contexte et le deuxieme est pour le callback onNewLocationAvailable
		SingleShotLocationProvider.requestSingleUpdate(this, this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//On met l'application en plein écran
		manager = (SensorManager) getSystemService( Service.SENSOR_SERVICE );//On récupère les capteurs,
		mAccelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//Et en particulier l'accéléromètre
		sensorListener = new MySensorListener();//Et on pose un écouteur dessus.

		initView();//Enfin on lance l'affichage.
	}

	private void initView() {
		mazeView = (GameView) findViewById(R.id.mazeView );//On récupère le labyrinthe
		mazeView.setGameOverListener(this);//Et on pose un écouteur dessus.
	}

	@Override
	public void notifyOfGameOver(int finalScore) {
		Intent gameOverIntent = new Intent(this, GameOverActivity.class);//On récupère l'activité correspondant au game over.
		gameOverIntent.putExtra( "score",finalScore );//Et on lui donne comme paramètre le score final
		gameOverIntent.putExtra( "location", location );//et la position
		startActivityForResult( gameOverIntent, GAMEOVER_ACTIVITY );//On lance la nouvelle activité
		overridePendingTransition(R.anim.enter, R.anim.exit);//En passant par une transition définie dans les fichiers enter.xml et exit.xml dans le dossier res
	}

	@Override
	public void onResume() {//Quand on va sur l'application
		super.onResume();//On applique la procédure habituelle
		accelSupported = manager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);//On vérifie que les capteurs soient disponibles
		/* Il n'est pas nécéssaire d'avoir un taux de rafraichissement très élevé pour l'accelerometre.
		 * En utilisant la sensibilité SENSOR_DELAY_UI on économise la batterie et les ressources du CPU */
		accelSupported = manager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onPause() {//Quand on met l'application en arrière plan
		if (accelSupported)//Si on a accès aux capteurs
			manager.unregisterListener(sensorListener, mAccelerometer);//On les libère.
		super.onPause();//En tout cas, on applique la procèdure habituelle.
	}

	private class MySensorListener implements SensorEventListener {//On définit ici l'écouteur sur l'acceléromètre.
		@Override
		public void onSensorChanged( SensorEvent event ) {//Quand un capteur perçoit un changement de l'environnement
			if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)//Si ce changement n'est pas mesuré par l'accéléromètre
				return;//On ne fais rien.
			else//Sinon,
				mazeView.updateAccel( -event.values[0], event.values[1]);//On demande au labyrinthe de se mettre à jour avec ces nouvelles données.
		}

		@Override
		public void onAccuracyChanged( Sensor sensor, int accuracy ) {//Méthode obligatoire pour implémenter l'écouteur.

		}
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {//Quand une activité se termine
		switch ( requestCode ) {//Selon la valeur du code requête (Quelle activité s'est terminée)
			case GAMEOVER_ACTIVITY://S'il vaut la constante GAMEOVER_ACTIVITY (dans le cas d'un game over, et donc du lancement de l'activité GameOver)
				switch ( resultCode ) {//Selon la valeur du code résultat
					case RESULT_OK://S'il vaut la constante RESULT_OK
						mazeView.newGame();//On lance un nouveau jeu.
						break;
					case RESULT_CANCELED://S'il vaut la constante RESULT_CANCELED
						finish();//On arrête l'activité.
						break;
					default: break;//Sinon, On ne fais rien
				}
				break;
			case SCORES_ACTIVITY:
				mazeView.startTimer();
				break;
			default: break;//Si le code requête vaut tout autre valeur, on ne fais rien.
		}
	}

	@Override
	public void onNewLocationAvailable( Double[] location ) {//Quand on appelle cette méthode
        Log.d("Location", "my location is :" + location[0].toString()+" "+location[1].toString());
		this.location = location;//On enregistre la position.
	}

	@Override
	public void onPermissionNeeded() {//Quand on appelle cette méthode (Quand on a besoin de permission pour accéder à la position)
		Log.d("Location", "onPermissionNeeded callback reached");//On enregistre dans le log le tag Location et on affiche en console qu'on a besoin d'une permission
		ActivityCompat.requestPermissions( this,//On demande la permission,
				new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },//d'accéder à la position fine
				MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);//avec le code ci-contre.
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {//Quand on reçoit (ou pas) des permissions
		Log.d("Location", "onRequestPermissionResult callback reached");// On enregistre dans le log le tag Location et on affiche en console qu'on a reçu une réponse
		switch (requestCode) {//Selon le code requête
			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {//Si il vaut la valeur ci-contre.
				// Si la requête est refusée, le résultat est vide, donc
				if (grantResults.length > 0//si le résultat n'est pas vide
						    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//et que la permission a été donnée
					SingleShotLocationProvider.requestSingleUpdate(this, this);//On demande la position courante.
				} else {//sinon,
					// La permission a été refusée, donc
					Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();//On affiche un toast disant que la permission a été refusée.
				}
			}
		}
	}

	@Override
	public void onDestroy() {//En quittant l'activité
		mazeView.stopTimer();//On arrête le timer du jeu
		super.onDestroy();//Et on quiteselon la procédure habituelle.
	}


	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.game, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch ( item.getItemId() ) {
			case R.id.action_scores:
				mazeView.stopTimer();
				Intent scoresIntent = new Intent( this, ScoresActivity.class );
				startActivityForResult( scoresIntent, SCORES_ACTIVITY );
				return true;
			case R.id.action_new_game:
				mazeView.newGame();
				return true;
			case R.id.action_menu:
				finish();
				return true;
		}
		return super.onOptionsItemSelected( item );
	}
}
