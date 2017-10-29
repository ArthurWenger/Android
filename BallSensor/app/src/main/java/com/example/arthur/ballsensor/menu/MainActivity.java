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
		/* player = MediaPlayer.create(this, R.raw.pacman_song);
		player.setVolume(100, 100);
		player.setLooping(true);
		player.start(); */
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



	//TODO: ajouter la musique au projet
	/*private static final String TAG = "MainActivity";
	public void toggleMusic(View view){
		if(player.isPlaying()){ player.stop(); }
		else{
			try {
				player.prepare();
			}
			catch(IOException ex){
				Log.d(TAG,"Prepare failed");
			}
			finally {
				player.start();
			}
		}
	} */

	public void onDestroy() {
		super.onDestroy();
		backgroundMusic.stopPlayer();
	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

	/*public static MediaPlayer getPlayer() {
		return player;
	}*/

	@Override
	public void onPause() {
		super.onPause();
		//player.pause();
		backgroundMusic.stopPlayer();
	}

	@Override
	public void onResume() {
		Log.i("info", "MainActivity onResume");
		super.onResume();
		//player.start();
		backgroundMusic.startPlayer( "pacman_song.ogg", 1f, true);
	}

}