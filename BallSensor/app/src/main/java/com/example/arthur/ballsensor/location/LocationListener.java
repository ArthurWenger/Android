package com.example.arthur.ballsensor.location;

public interface LocationListener {
	void onNewLocationAvailable( Double[] location );

	void onPermissionNeeded();
}
