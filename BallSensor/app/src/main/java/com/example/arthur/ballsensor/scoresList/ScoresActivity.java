package com.example.arthur.ballsensor.scoresList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.maps.MapsActivity;
import com.example.arthur.ballsensor.database.DBManager;

import java.util.ArrayList;

public class ScoresActivity extends AppCompatActivity {
	private ListView mListView;
	private TextView mTvNothing;
	private ScoresArrayAdapter adapter;
	DBManager mydb;

	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_scores );
		mydb = new DBManager(this);
		// pour supprimer la base de données:
		// this.deleteDatabase("ScoresDB.db");
		initView();
	}

	private void initView(){
		mTvNothing = (TextView) findViewById( R.id.tvNothing );
		mListView = (ListView) findViewById( R.id.list );

		final ArrayList<Score> scores_array = mydb.getAllScores();
		if(scores_array.size()==0){
			mTvNothing.setVisibility(TextView.VISIBLE);
		} else {
			adapter = new ScoresArrayAdapter( this, scores_array );
			mListView.setAdapter( adapter );
			mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapter, View view, int position, long id ) {
					Score score = (Score) adapter.getItemAtPosition( position );
					Intent intent = new Intent( getBaseContext(), MapsActivity.class );
					intent.putExtra( "centerScore", score );
					intent.putExtra( "scores_array", scores_array );
					startActivity( intent );
				}
			} );
		}
	}
}
