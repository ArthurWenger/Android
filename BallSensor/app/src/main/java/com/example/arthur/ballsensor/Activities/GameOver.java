package com.example.arthur.ballsensor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arthur.ballsensor.R;

public class GameOver extends AppCompatActivity implements View.OnClickListener {

	private ImageView mBPlay;
	private ImageView mBHighscore;
	private TextView mTvScore;
	private int score;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Intent intent = getIntent();
		score = intent.getExtras().getInt( "score" );
		setContentView( R.layout.activity_game_over );
		initView();
	}

	private void initView() {
		mBPlay = (ImageView) findViewById( R.id.b_play );
		mBPlay.setOnClickListener( this );
		mBHighscore = (ImageView) findViewById( R.id.b_highscore );
		mBHighscore.setOnClickListener( this );
		mTvScore = (TextView) findViewById( R.id.tvScore );
		mTvScore.setText( "Score: "+score );
	}

	@Override
	public void onClick( View v ) {
		switch ( v.getId() ) {
			case R.id.b_play:
				break;
			case R.id.b_highscore:
				break;
		}
	}
}
