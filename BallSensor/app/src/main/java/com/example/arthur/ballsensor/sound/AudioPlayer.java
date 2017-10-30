package com.example.arthur.ballsensor.sound;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioPlayer {
	private MediaPlayer player = null;
	private AssetManager assets;

	public AudioPlayer( AssetManager assets ) {
		this.assets = assets;
	}

	public void stopPlayer() {
		if (player != null) {
			player.release();
			player = null;
			//Log.d("sound","player released");
		}
	}

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
				//Log.d("sound","onCompletion called");
				stopPlayer();
			}
		} );
		//Log.d("sound","starting player");
		player.start();
	}

	/* public boolean isPlaying() {
		if(player!=null){
			return player.isPlaying();
		}
		return false;
	} */
}
