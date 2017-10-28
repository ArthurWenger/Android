package com.example.arthur.ballsensor.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class SingleShotLocationProvider {

	// calls back to calling thread, note this is for low grain: if you want higher precision, swap the
	// contents of the else and if. Also be sure to check gps permission/settings are allowed.
	// call usually takes <10ms
	public static void requestSingleUpdate( final Context context, final LocationCallback callback ) {
		final LocationManager locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
		assert locationManager != null;
		boolean isNetworkEnabled = locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
		if ( isNetworkEnabled ) {
			Log.d("Location", "network available detected");
			Criteria criteria = new Criteria();
			criteria.setAccuracy( Criteria.ACCURACY_COARSE );
			if ( ActivityCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
					     && ActivityCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
				callback.onPermissionNeeded();
				return;
			}
			locationManager.requestSingleUpdate( criteria, new MyLocationListener( callback ), null );
		} else {
			Log.d("Location", "network not available detected");
			boolean isGPSEnabled = locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
			if ( isGPSEnabled ) {
				Log.d("Location", "gps enabled detected");
				Criteria criteria = new Criteria();
				criteria.setAccuracy( Criteria.ACCURACY_FINE );
				locationManager.requestSingleUpdate( criteria, new MyLocationListener( callback ), null );
			}
		}
	}

	private static class MyLocationListener implements LocationListener {
		LocationCallback callback;
		private MyLocationListener(final LocationCallback callback){
			this.callback = callback;
		}
		@Override
		public void onLocationChanged( Location location ) {
			Log.d("Location", "onLocationChanged triggered");
			callback.onNewLocationAvailable( new Double[] {location.getLatitude(), location.getLongitude()} );
		}

		@Override
		public void onStatusChanged( String provider, int status, Bundle extras ) {
		}

		@Override
		public void onProviderEnabled( String provider ) {
		}

		@Override
		public void onProviderDisabled( String provider ) {
		}

	}
}
