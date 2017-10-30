package com.example.arthur.ballsensor.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.game.GameActivity;
import com.example.arthur.ballsensor.scoresList.ScoresActivity;
import com.example.arthur.ballsensor.sound.AudioPlayer;

public class MainActivity extends AppCompatActivity {
	private static AudioPlayer backgroundMusic ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_main );
		backgroundMusic = new AudioPlayer( this.getAssets() );
		backgroundMusic.startPlayer( "pacman_song.ogg", 1f, true);
	}

	// Methode pour afficher la liste des scores
	public void showScoresScreen( View view){
		Intent helpIntent = new Intent(this, ScoresActivity.class);
		startActivity(helpIntent);
	}

	// Methode pour lancer une partie
	public void showPlayScreen(View view) {
		Intent playIntent = new Intent(this, GameActivity.class);
		startActivity(playIntent);
	}

	// Méthode pour quitter l'application
	public void exitApp( View view){
		finish();
		System.exit(0);
	}

	public void onDestroy() {
		super.onDestroy();
		backgroundMusic.stopPlayer();
	}

	@Override
	public void onPause() {
		super.onPause();
		backgroundMusic.stopPlayer();
	}

	@Override
	public void onResume() {
		Log.i("info", "MainActivity onResume");
		super.onResume();
		backgroundMusic.startPlayer( "pacman_song.ogg", 1f, true);
	}

}