package com.example.arthur.ballsensor.scores;

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
	private Integer highlight = null;

	/**Au lancement de l'acivité**/
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );//On utilise la méthode habituelle.
		Bundle extras = getIntent().getExtras();//On récupère les arguments de l'intent
		if(extras!=null && extras.containsKey( "highlight" ) ) {//Si parmis ces argument, il y a une clef "highlight"
				highlight = extras.getInt( "highlight" );//On récupère sa valeur
		}
		setContentView( R.layout.activity_scores );//On met en place l'affichage
		mydb = new DBManager(this);//On récupère la base de données
		// pour supprimer la base de données:
		// this.deleteDatabase("ScoresDB.db");
		initView();//Et on lance l'affichage.
	}

	private void initView(){
		mTvNothing = (TextView) findViewById( R.id.tvNothing );//On récupère la zone de texte
		mListView = (ListView) findViewById( R.id.list );//Et la liste

		final ArrayList<Score> scores_array = mydb.getAllScores();//On récupère la liste des scores
		if(scores_array.size()==0){//Si la liste est vide
			mTvNothing.setVisibility(TextView.VISIBLE);//On affiche la zone de texte telle quelle.
		} else {//Sinon
			adapter = new ScoresArrayAdapter( this, scores_array );//On créé un adaptateur de liste
			adapter.setSelected( highlight );//on sélectionne le score donné en paramètre d'intent
			mListView.setAdapter( adapter );//On ajoute l'adapteur à la liste dans les vues
			mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {//On créé un écouteur sur la liste
				@Override
				public void onItemClick( AdapterView<?> adapter, View view, int position, long id ) {
					Score score = (Score) adapter.getItemAtPosition( position );//On récupère l'objet score sur lequel on a cliqué
					Intent intent = new Intent( getBaseContext(), MapsActivity.class );//On créé un nouvel intent pour la carte
					intent.putExtra( "centerScore", score );//on y ajoute le score sélectionné
					intent.putExtra( "scores_array", scores_array );//et la liste des scores
					startActivity( intent );//Et on lance l'activité
				}
			} );
		}
	}
}
