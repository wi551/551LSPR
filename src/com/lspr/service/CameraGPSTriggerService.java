package com.lspr.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class CameraGPSTriggerService extends Service{

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Camera+ GPS Service created...", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "Camera + GPS Service starting...", Toast.LENGTH_SHORT).show();
	      
	      // Camera take picture saves to SD
	      Toast.makeText(this, "Took your picture!", Toast.LENGTH_SHORT).show();
	      
	      // GPS take location saves to SD
	      Toast.makeText(this, "Recorded your GPS location!", Toast.LENGTH_SHORT).show();
	      
	      // Email module grabs from SD, attach and sends email
	      Toast.makeText(this, "I emailed your picture and GPS location to the owner! You're doomed! =)", Toast.LENGTH_SHORT).show();
	      
	      return START_STICKY;
	  }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
	
}
