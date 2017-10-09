package com.example.arthur.ballsensor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class ScoresActivity extends AppCompatActivity {
	private ListView mListView;
	private ScoresArrayAdapter adapter;
	private ArrayList<Score> tasks;

	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_scores );
		initView();
	}

	private void initView(){
		mListView = (ListView) findViewById( R.id.list );
		registerForContextMenu( mListView );

		// a changer pour mettre en place la persistance
		tasks = new ArrayList<Score>();
		//Random rand = new Random(  );
		for ( int i = 1; i <= 5; i++ )
			tasks.add( new Score( i, "user " + i , 50000-10000*i) );

		adapter = new ScoresArrayAdapter( this, tasks );
		mListView.setAdapter( adapter );
	}
}
