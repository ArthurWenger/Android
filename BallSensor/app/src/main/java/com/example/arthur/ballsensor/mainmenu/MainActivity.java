package com.example.arthur.ballsensor.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.game.GameActivity;
import com.example.arthur.ballsensor.scores.ScoresActivity;
import com.example.arthur.ballsensor.sound.AudioPlayer;

public class MainActivity extends AppCompatActivity {
	private static AudioPlayer backgroundMusic ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {//Au démarrage de l'application
		super.onCreate(savedInstanceState);//On applique la méthode usuelle
		setContentView( R.layout.activity_main );//On récupère la disposition des vues
		backgroundMusic = new AudioPlayer( this.getAssets() );
		backgroundMusic.startPlayer( "sounds/pacman_song.ogg", 1f, true);//On lance la musique de fond
	}

	// Methode pour afficher la liste des scores
	public void showScoresScreen( View view){
		Intent helpIntent = new Intent(this, ScoresActivity.class);//On créé une activité d'affichage des scores
		startActivity(helpIntent);//On lance cette activité
	}

	// Methode pour lancer une partie
	public void showPlayScreen(View view) {
		Intent playIntent = new Intent(this, GameActivity.class);//On créé une activité de jeu
		startActivity(playIntent);//On lance cette activité
	}

	// Méthode pour quitter l'application
	public void exitApp( View view){
		finish();//On arrête cette activité
		System.exit(0);//Et on quitte
	}

	public void onDestroy() {//Quand on arrête l'activité
		super.onDestroy();//On utilise la procédure habituelle
		backgroundMusic.stopPlayer();//Et on arrête la musique.
	}

	@Override
	public void onPause() {//Quand on met l'activité en arrière-plan
		super.onPause();//On utilise la procédure habituelle
		backgroundMusic.stopPlayer();//Et on arrête la musique.
	}

	@Override
	public void onResume() {//Quand on met l'activité en premier plan
		super.onResume();//On utilise la procédure habituelle
		backgroundMusic.startPlayer( "sounds/pacman_song.ogg", 1f, true);//Et on relance la musique.
	}

}