package com.example.arthur.ballsensor.location;

public interface LocationCallback {
	void onNewLocationAvailable( Double[] location );

	void onPermissionNeeded();
}
