package com.example.arthur.ballsensor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arthur.ballsensor.Objects.DBManager;
import com.example.arthur.ballsensor.R;

public class GameOver extends AppCompatActivity {

	private ImageView mBPlay;
	private ImageView mBHighscore;
	private TextView mTvScore;
	private int score;
	private DBManager mydb;

	private double lat = 0;
	private double lng = 0;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		mydb = new DBManager(this);
		Intent intent = getIntent();
		score = intent.getExtras().getInt( "score" );
		mydb.insertScore( score, lat, lng );
		setContentView( R.layout.activity_game_over );
		initView();
	}

	private void initView() {
		mBPlay = (ImageView) findViewById( R.id.b_play );
		mBHighscore = (ImageView) findViewById( R.id.b_highscore );
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

	public void quit(View view){
		setResult( RESULT_CANCELED );
		finish();
	}
}
