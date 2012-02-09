package com.lspr;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;


public class LSPRService extends Service
{
	
	String _user;
	String _pass;
	String _subject;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "The service has been ended", Toast.LENGTH_SHORT).show();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(getApplicationContext(), "The service has started", Toast.LENGTH_SHORT).show();
		return Service.START_STICKY;
	}
	
	
	//ready to send out email call this funtion
	//pass an Intent object with Extras that contain the gmail address and password
	private void Monitor(Intent intent)
	{
		
		Mail m = new Mail();
		String usertext = intent.getExtras().getString("email");
		String[] toArr = {usertext};
		m.setPass(intent.getExtras().getString("pass"));
		m.setUser(usertext);
		m.setTo(toArr); 
	    m.setFrom(usertext); 
	    m.setSubject("Capture"); 
	    m.setBody("GPS Coor.\nLat:\nLong:\n"); 
		try {
			
			File sd = new File(Environment.getExternalStorageDirectory(), "name of file here");
			m.addAttachment(sd.getAbsolutePath());

			//the following if statement may not be needed outside of testing would replace with just m.send()
			//or just comment out the Toast lines
	        if(m.send()) { 
	          Toast.makeText(LSPRService.this, "Email was sent successfully.", Toast.LENGTH_LONG).show(); 
	        } else { 
	          Toast.makeText(LSPRService.this, "Email was not sent.", Toast.LENGTH_LONG).show(); 
	        }
		}
		catch (Exception e) {
			Toast.makeText(LSPRService.this, "There was a problem sending the email.\nBody:" 
		+ m.getBody() + "\nSubject: " + m.getSubject() + "\nFrom: " + m.getFrom()
		+ "\nUser: " + m.getUser() + "\nPass: " + m.getPass(), Toast.LENGTH_LONG).show();
		
		}		
	}
}