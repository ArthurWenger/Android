package com.example.arthur.ballsensor.location;

/** Interface permettant d'écouter la classe "SingleShotLocationListener" pour récupérer la position
 *  de l'appareil ou notifier une demande de permission nécessaire **/
public interface LocationListener {
	void onNewLocationAvailable( Double[] location );

	void onPermissionNeeded();
}
