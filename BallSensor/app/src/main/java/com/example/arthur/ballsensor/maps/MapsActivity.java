package com.example.arthur.ballsensor.maps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.scores.Score;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private ArrayList<Score> scores_array;
	private Score centerScore;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {//Au lancement de l'activité
		super.onCreate( savedInstanceState );//On utilise la méthode usuelle
		setContentView( R.layout.activity_maps );//On récupère les éléments de l'affichage
		Bundle extras = this.getIntent().getExtras();//On récupère les paramètres de lancement
		assert extras != null;//On vérifie que ceux-ci ne soient pas vides
		centerScore = (Score) extras.get( "centerScore" );//On les enregistrent
		scores_array = (ArrayList<Score>) extras.get( "scores_array" );//dans des variables locales

		// On récupère la carte quand elle est prète.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				                                                      .findFragmentById( R.id.map );
		mapFragment.getMapAsync( this );
	}


	/**Méthode éxécutée quand la carte est prète*/
	@Override
	public void onMapReady( GoogleMap googleMap ) {
		try {
			mMap = googleMap;
			mMap.getUiSettings().setZoomControlsEnabled( true );
			mMap.getUiSettings().setZoomGesturesEnabled( true );

			int centerScoreRank = centerScore.getRank();
			for(Score score: scores_array){
					mMap.addMarker( createMarker( score, score.getRank()==centerScoreRank ) );
			}

			LatLng centerLatLng = new LatLng( centerScore.getLatitude(), centerScore.getLongitude() );
			mMap.addMarker(new MarkerOptions()
					               .position(centerLatLng)
					               .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( centerLatLng, 11 ) );

		} catch(SecurityException e){

		}
	}

	/**Méthode pour créer des indicateurs de position*/
	private MarkerOptions createMarker(Score score, boolean center){
		double sLat = score.getLatitude();
		double sLng = score.getLongitude();
		LatLng latLng = new LatLng( sLat, sLng );
		MarkerOptions res = new MarkerOptions()
				                .position( latLng )
				                .title( "score: " + score.getValue() )
				                .snippet("lat: "+sLat+" lng: "+sLng);
		if(center){
			res.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		}
		return res;
	}
}
