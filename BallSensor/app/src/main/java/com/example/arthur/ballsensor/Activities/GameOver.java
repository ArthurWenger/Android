package com.example.arthur.ballsensor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arthur.ballsensor.Objects.DBManager;
import com.example.arthur.ballsensor.Objects.SingleShotLocationProvider;
import com.example.arthur.ballsensor.R;

public class GameOver extends AppCompatActivity implements SingleShotLocationProvider.LocationCallback {

	private ImageView mBPlay;
	private ImageView mBHighscore;
	private TextView mTvScore;
	private int score;
	private DBManager mydb;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		// le premier this est pour le contexte et le deuxieme est pour le callback onNewLocationAvailable
		SingleShotLocationProvider.requestSingleUpdate(this, this);
		mydb = new DBManager(this);
		Intent intent = getIntent();
		score = intent.getExtras().getInt( "score" );
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

	@Override
	public void onNewLocationAvailable( Double[] location ) {
		//Log.d("Location", "my location is :" + location[0].toString()+" "+location[1].toString());
		Log.d("Location", "my location is :" + location[0].toString()+" "+location[1].toString());
		mydb.insertScore( score, location[0], location[1] );
	}
}
