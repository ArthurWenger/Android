package com.example.arthur.ballsensor.gameover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.database.DBManager;
import com.example.arthur.ballsensor.scoresList.ScoresActivity;

public class GameOverActivity extends AppCompatActivity {


	private TextView mTvScore;
	private int score;
	private DBManager mydb;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		mydb = new DBManager(this);
		Bundle extras = getIntent().getExtras();
		assert extras != null;
		score = extras.getInt( "score" );
		Double[] location = (Double[]) extras.get( "location" );
		if( location == null){
			Toast.makeText( this, "Impossible de localiser l'appareil,\n" +
					                      "votre score ne sera pas ajouté à la base",Toast.LENGTH_LONG ).show();
		} else {
			mydb.insertScore( score, location[ 0 ], location[ 1 ] );
		}

		setContentView( R.layout.activity_game_over );
		initView();
	}

	private void initView() {
		mTvScore = (TextView) findViewById( R.id.tvScore );
		mTvScore.setText( "Score: "+score );
	}

	public void play( View view ) {
		setResult( RESULT_OK );
		finish();
	}

	public void highscores( View view ) {
		Intent intent = new Intent( this, ScoresActivity.class );
		startActivity( intent );
	}
}
