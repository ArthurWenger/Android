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
	protected void onCreate( Bundle savedInstanceState ) {//Au lancement de l'activité
		super.onCreate( savedInstanceState );//On utilise la méthode usuelle
		mydb = new DBManager(this);//On récupère la base de données des scores
		Bundle extras = getIntent().getExtras();//On récupère les paramètres donnés dans l'intent (à la demande de création de l'activité)
		assert extras != null;//On vérifie leur existance
		score = extras.getInt( "score" );//On récupère le nouveau score
		Double[] location = (Double[]) extras.get( "location" );//Puis la nouvelle position
		if( location == null){//Si on n'a pas la nouvelle position
			Toast.makeText( this, "Impossible de localiser l'appareil,\n" +
					                      "votre score ne sera pas ajouté à la base",Toast.LENGTH_LONG ).show();//On affiche le Toast ci-contre
		} else {//Sinon,
			mydb.insertScore( score, location[ 0 ], location[ 1 ] );//On ajoute le nouveau score et la nouvelle position à la base de données.
		}

		setContentView( R.layout.activity_game_over );//On met en place les éléments de l'affichage
		initView();//et on lance la méthode ci-contre
	}

	private void initView() {
		mTvScore = (TextView) findViewById( R.id.tvScore );//On récupère l'élément tvScore dans l'affichage
		mTvScore.setText( "Score: "+score );//Et on fixe son texte à la valeur du score
	}

	public void play( View view ) {
		setResult( RESULT_OK );//On met le code résultat à RESULT_OK
		finish();//Et on termine l'activité
	}

	public void highscores( View view ) {
		Intent intent = new Intent( this, ScoresActivity.class );//On créé une nouvelle activité pour afficher les scores
		startActivity( intent );//Et on la lance.
	}
}
