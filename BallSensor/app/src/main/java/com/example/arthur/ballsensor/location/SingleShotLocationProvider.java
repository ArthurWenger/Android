package com.example.arthur.ballsensor.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class SingleShotLocationProvider {

	/** Classe permettant de récupérer une seule fois la position de l'appareil
	 *  L'interface "LocationListener" permet de notifier les écouteurs que la recuperation
	 *  de la position est terminée ou qu'une demande de permission est recquise **/
	public static void requestSingleUpdate( final Context context, final LocationListener callback ) {
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

	private static class MyLocationListener implements android.location.LocationListener {
		LocationListener callback;
		private MyLocationListener(final LocationListener callback){
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
