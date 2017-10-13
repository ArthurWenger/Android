package com.example.arthur.ballsensor;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
	private FusedLocationProviderClient mFusedLocationClient;
	private LocationRequest mLocationRequest;
	private LocationCallback mLocationCallback;
	Location mLastLocation;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_maps );

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				                                                      .findFragmentById( R.id.map );
		mapFragment.getMapAsync( this );

		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		createLocationRequest();
		mLocationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				// Update UI with the most recent location.
				updateUI(locationResult.getLastLocation());
			}
		};
		// regarder pour ne le faire qu'une seule fois! + modifier onmapready pour l'animation de depart


	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(5000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if ( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
				    PackageManager.PERMISSION_GRANTED) {
			// We request the permission.
			ActivityCompat.requestPermissions(this,
					new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
					MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
		} else
			startLocationUpdates();
	}

	private void startLocationUpdates() {
		try {
			// Initialize UI with the last known location.
			mFusedLocationClient.getLastLocation()
					.addOnSuccessListener(this, new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							// Got last known location. In some rare situations this can be null.
							updateUI(location);
						}
					});

			mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
		} catch (SecurityException e) {
			Toast.makeText(this, getString(R.string.security_error), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocationUpdates();
	}

	private void stopLocationUpdates() {
		mFusedLocationClient.removeLocationUpdates(mLocationCallback);
	}

	private void updateUI(Location location) {
		if (location != null) {
			mLastLocation = location;
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       String permissions[],
	                                       int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					startLocationUpdates();

				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
				}
			}
		}
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
			mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );
			mMap.setMyLocationEnabled( true );
			mMap.getUiSettings().setZoomControlsEnabled( true );
			mMap.getUiSettings().setZoomGesturesEnabled( true );

			// Add a marker in Sydney and move the camera
			//LatLng sydney = new LatLng( -34, 151 );
			//mMap.addMarker( new MarkerOptions().position( sydney ).title( "Marker in Sydney" ) );
			//mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );
			if(mLastLocation!=null) {
				LatLng latLng = new LatLng( mLastLocation.getLatitude(), mLastLocation.getLongitude() );
				/*MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title("Position Actuelle");
				markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
				mCurrLocationMarker = mGoogleMap.addMarker(markerOptions); */

				//move map camera
				mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 11 ) );
			}

		} catch(SecurityException e){

		}
	}
}
