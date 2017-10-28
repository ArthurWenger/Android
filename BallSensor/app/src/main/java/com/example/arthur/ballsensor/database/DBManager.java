package com.example.arthur.ballsensor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arthur.ballsensor.scoresList.Score;

import java.util.ArrayList;

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
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(
				"create table scores ("+
						SCORES_COLUMN_ID+" integer primary key autoincrement, " +
						SCORES_COLUMN_VALUE+" integer, " +
						SCORES_COLUMN_LATITUDE+" real, " +
						SCORES_COLUMN_LONGITUDE+" real)"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+SCORES_TABLE_NAME);
		onCreate(db);
	}

	public boolean insertScore (int value, double latitude, double longitude) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(SCORES_COLUMN_VALUE, value);
		contentValues.put(SCORES_COLUMN_LATITUDE, latitude);
		contentValues.put(SCORES_COLUMN_LONGITUDE, longitude);
		db.insert(SCORES_TABLE_NAME, null, contentValues);
		return true;
	}

	public ArrayList<Score> getAllScores() {
		ArrayList<Score> array_list = new ArrayList<Score>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor =  db.rawQuery( "select * from "+SCORES_TABLE_NAME +
				                              " order by "+SCORES_COLUMN_VALUE+
				                              " desc", null );
		// au cas ou la table changerait on recupere les index des attributs
		//int idIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_ID);
		int valueIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_VALUE);
		int latIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_LATITUDE);
		int lngIndex = cursor.getColumnIndexOrThrow(SCORES_COLUMN_LONGITUDE);

		cursor.moveToFirst();
		while( !cursor.isAfterLast() ){
			//int id = cursor.getInt( idIndex );
			int rank = cursor.getPosition()+1;
			int value = cursor.getInt( valueIndex );
			double lat = cursor.getDouble( latIndex );
			double lng = cursor.getDouble( lngIndex );
			Score row = new Score(rank, value, lat, lng);
			array_list.add(row);
			cursor.moveToNext();
		}
		cursor.close();
		return array_list;
	}

	/* public Cursor getData( int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from "+ SCORES_TABLE_NAME +" where id="+id, null );
		return res;
	}

	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, SCORES_TABLE_NAME );
		return numRows;
	}

	public boolean updateScore (int id, int value, double lat, double lng) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(SCORES_COLUMN_ID, id);
		contentValues.put(SCORES_COLUMN_VALUE, value);
		contentValues.put(SCORES_COLUMN_LATITUDE, lat);
		contentValues.put(SCORES_COLUMN_LONGITUDE, lng);
		db.update(SCORES_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
		return true;
	}

	public Integer deleteScore (Integer id) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(SCORES_TABLE_NAME,
				"id = ? ",
				new String[] { Integer.toString(id) });
	} */
}