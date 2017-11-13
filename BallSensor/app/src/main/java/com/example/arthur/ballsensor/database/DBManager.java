package com.example.arthur.ballsensor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arthur.ballsensor.scores.Score;

import java.util.ArrayList;

/** Classe manipulant les informations de la base de données **/
public class DBManager extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "ScoresDB.db";
	public static final String SCORES_TABLE_NAME = "scores";
	public static final String SCORES_COLUMN_ID = "id";
	public static final String SCORES_COLUMN_VALUE = "value";
	public static final String SCORES_COLUMN_LATITUDE = "lat";
	public static final String SCORES_COLUMN_LONGITUDE = "lng";

	public DBManager( Context context) {
		super(context, DATABASE_NAME , null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {//Au lancement de l'activité, on récupère une base de données
		/** Si la base de données existe déjà, la requête suivante ne fait rien. Sinon, elle créé la table appropriée **/
		db.execSQL(
				"create table scores ("+
						SCORES_COLUMN_ID+" integer primary key autoincrement, " +
						SCORES_COLUMN_VALUE+" integer, " +
						SCORES_COLUMN_LATITUDE+" real, " +
						SCORES_COLUMN_LONGITUDE+" real)"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//Quand on change de version de base de données
		db.execSQL("DROP TABLE IF EXISTS "+SCORES_TABLE_NAME);//On détruit l'ancienne table
		onCreate(db);//Et on relance l'activité.
	}

	/** Insertion d'un score dans la base de données **/
	public long insertScore (int value, double latitude, double longitude) {
		SQLiteDatabase db = this.getWritableDatabase();//On récupère une base de données dans laquelle on peut écrire.
		ContentValues contentValues = new ContentValues();//On créé une variable conteneur,
		contentValues.put(SCORES_COLUMN_VALUE, value);//dans laquelle on range le score
		contentValues.put(SCORES_COLUMN_LATITUDE, latitude);//Puis la lattitude
		contentValues.put(SCORES_COLUMN_LONGITUDE, longitude);//Et la longitude.
        return db.insert(SCORES_TABLE_NAME, null, contentValues);//Enfin, on insere le conteneur dans la base de données.
        //Et on renvoie vrai pour dire que tout s'est bien passé.
	}

	/** Récupération de l'ensemble des scores de la base **/
	public ArrayList<Score> getAllScores() {
		ArrayList<Score> array_list = new ArrayList<Score>();//On créé tout d'abord un tableau de scores
		SQLiteDatabase db = this.getReadableDatabase();//Puis on récupère une base de données dans laquelle on peut écrire
		Cursor cursor =  db.rawQuery( "select * from "+SCORES_TABLE_NAME +//A laquelle on demande toutes les entrées
				                              " order by "+SCORES_COLUMN_VALUE+//Ordonnées par leur score
				                              " desc", null );//de manière décroissante
		// au cas ou la table changerait on recupere les index des attributs
		int valueIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_VALUE);
		int latIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_LATITUDE);
		int lngIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_LONGITUDE);

		cursor.moveToFirst();//Puis on met le curseur de la table envoyée par la base à la première valeur
		while( !cursor.isAfterLast() ){//Et tant que le curseur n'a pas dépassé la dernière valeur
			int rank = cursor.getPosition()+1;//on incrémente le rang de 1
			int value = cursor.getInt( valueIndex );//on récupère les index de la valeur,
			double lat = cursor.getDouble( latIndex );//de la lattitude,
			double lng = cursor.getDouble( lngIndex );//et de la longitude,
			Score row = new Score(rank, value, lat, lng);//et on range le tout dans un nouvel objet Score
			array_list.add(row);//Que l'on ajoute au tableau.
			cursor.moveToNext();//Et on pousse le curseur à la prochaine ligne.
		}//Une fois tous les scores enregistrés
		cursor.close();//On ferme la table envoyée par la base
		return array_list;//Et on renvoie le tableau.
	}

	// méthode utile uniquement dans l'activité GameOver
	// le parametre score n'est pas nécessaire mais permet d'accelerer la requete
	public int getRank(int id, int score){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "+SCORES_COLUMN_ID+
				                            " from "+SCORES_TABLE_NAME+
				                            " where "+SCORES_COLUMN_VALUE+" >= "+score+"" +
				                            " order by "+SCORES_COLUMN_VALUE+" desc;", null);
		ArrayList<Integer> array_list = new ArrayList<Integer>();
		int idIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_ID);
		cursor.moveToFirst();
		int rank = cursor.getInt( 0 );
		while( !cursor.isAfterLast() ){
			int rowId = cursor.getInt( idIndex );
			array_list.add(rowId);
			cursor.moveToNext();
		}
		cursor.close();
		return array_list.indexOf( id )+1;
	}
}