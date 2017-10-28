package com.example.arthur.ballsensor.maps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.arthur.ballsensor.R;
import com.example.arthur.ballsensor.scoresList.Score;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private double scoreLatitude;
	private double scoreLongitude;
	private ArrayList<Score> scores_array;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_maps );
		Bundle extras = this.getIntent().getExtras();
		assert extras != null;
		scoreLatitude = extras.getDouble( "latitude", 0 );
		scoreLongitude = extras.getDouble( "longitude", 0 );
		scores_array = (ArrayList<Score>) extras.get( "scores_array" );
		Log.i("lat", ""+scoreLatitude);
		Log.i("long", ""+scoreLongitude);


		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				                                                      .findFragmentById( R.id.map );
		mapFragment.getMapAsync( this );
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady( GoogleMap googleMap ) {
		try {
			mMap = googleMap;
			//mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );
			mMap.getUiSettings().setZoomControlsEnabled( true );
			mMap.getUiSettings().setZoomGesturesEnabled( true );

			//LatLng latLng = new LatLng( scoreLatitude, scoreLongitude );
			for(Score score: scores_array){
				LatLng latLng = new LatLng( score.getLatitude(), score.getLongitude() );
				mMap.addMarker( new MarkerOptions().position( latLng ).title( "score: "+score.getValue() ) );
			}
			LatLng centerLatLng = new LatLng( scoreLatitude, scoreLongitude );
			mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( centerLatLng, 11 ) );

		} catch(SecurityException e){

		}
	}
}
