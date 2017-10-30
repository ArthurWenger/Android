package com.example.arthur.ballsensor.location;

/** Interface permettant d'écouter la classe "SingleShotLocationListener" pour récupérer la position
 *  de l'appareil ou savoir si une demande de permission est recquise **/
public interface LocationListener {
	void onNewLocationAvailable( Double[] location );

	void onPermissionNeeded();
}
