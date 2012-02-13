package com.lspr.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class RestartsService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Restarts Service created...", Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Restarts Service starting...", Toast.LENGTH_SHORT)
				.show();

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Restarts Service done", Toast.LENGTH_SHORT)
				.show();
	}

}
