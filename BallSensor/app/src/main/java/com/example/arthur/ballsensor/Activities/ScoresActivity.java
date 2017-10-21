package com.example.arthur.ballsensor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.Objects.Score;
import com.example.arthur.ballsensor.ScoresArrayAdapter;

import java.util.ArrayList;

public class ScoresActivity extends AppCompatActivity {
	private ListView mListView;
	private ScoresArrayAdapter adapter;
	private ArrayList<Score> tasks;
	private final int MAP_ACTIVITY = 2;

	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_scores );
		initView();
	}

	private void initView(){
		mListView = (ListView) findViewById( R.id.list );
		//registerForContextMenu( mListView );

		// a changer pour mettre en place la persistance
		tasks = new ArrayList<Score>();
		//Random rand = new Random(  );
		for ( int i = 1; i <= 5; i++ )
			tasks.add( new Score( i, "user " + i , 50000-10000*i) );

		adapter = new ScoresArrayAdapter( this, tasks );
		mListView.setAdapter( adapter );

		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> adapter, View view, int position, long id ) {
				Score score = (Score)adapter.getItemAtPosition(position);
				Intent intent = new Intent( getBaseContext(), MapsActivity.class );

				startActivityForResult( intent, MAP_ACTIVITY );
			}
		} );
	}
}
