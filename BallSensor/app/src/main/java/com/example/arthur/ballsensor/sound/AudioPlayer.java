package com.example.arthur.ballsensor.sound;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioPlayer {
	private MediaPlayer player = null;
	private AssetManager assets;

	/**Constructeur**/
	public AudioPlayer( AssetManager assets ) {
		this.assets = assets;
	}

	/**Méthode permettant de libérer la ressource lecteur de son**/
	public void stopPlayer() {
		if (player != null) {
			player.release();
			player = null;
		}
	}

	/**Méthode permettant de lancer un nouveau lecteur de son.**/
	public void startPlayer(String filename, float volume, boolean loop) {
		stopPlayer();

		AssetFileDescriptor afd;
		try {
			afd = assets.openFd(filename);
			player = new MediaPlayer();
			player.setVolume(volume, volume);
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player.setLooping(loop);
			player.prepare();
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		player.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion( MediaPlayer mp ) {
				stopPlayer();
			}
		} );
		player.start();
	}

}
