package com.example.arthur.ballsensor;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapActivityOld extends AppCompatActivity {

	// Request code to use when requesting location permission
	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

	private FusedLocationProviderClient mFusedLocationClient;
	private LocationRequest mLocationRequest;
	private LocationCallback mLocationCallback;
	Location mLastLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		createLocationRequest();
		mLocationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				// Update UI with the most recent location.
				updateUI(locationResult.getLastLocation());
			}
		};
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
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
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
}
