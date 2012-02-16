package com.lspr.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.lspr.activities.SettingActivity;
import com.lspr.constants.LSPRConstants;
import com.lspr.modules.mail.Mail;

public class CameraGPSTriggerService extends Service {

	private static final String TAG = "Service";
	private SharedPreferences prefs;

	public CameraGPSTriggerService() {
		super();
		prefs = null;
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			// 1 as the parameter is for the front camera, 0 is for back camera
			c = Camera.open(1); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"RecoveryPicturesNew");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private Camera mCamera;
	// picture call back start
	private PictureCallback mPicture = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {

			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				// Log.d(TAG,
				// "Error creating media file, check storage permissions: " +
				// e.getMessage());
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				// Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				// Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		releaseCamera();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");

		// Camera take picture saves to SD
		// Create an instance of Camera
		mCamera = getCameraInstance();

		SurfaceView view = new SurfaceView(this);
		try {
			mCamera.setPreviewDisplay(view.getHolder());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}
		mCamera.startPreview();
		mCamera.takePicture(null, null, mPicture);

		// GPS take location saves to SD
		// Toast.makeText(this, "Recorded your GPS location!",
		// Toast.LENGTH_SHORT).show();

		// Email module grabs from SD, attach and sends email
		prefs = SettingActivity.getPreferences(getApplicationContext());
		String emailText = prefs.getString(LSPRConstants.PREF_EMAIL,
				"email@domain.com");
		String emailPassword = prefs.getString(LSPRConstants.PREF_EMAIL_PASS,
				"email_password");

		sendMail(emailText, emailPassword);

		stopSelf();

	}

	private void sendMail(String email, String pass) {

		Mail m = new Mail();
		// String usertext = intent.getExtras().getString("email");
		String usertext = email;
		String emailPass = pass;
		String[] toArr = { usertext };
		// m.setPass(intent.getExtras().getString("pass"));
		m.setPass(emailPass);
		m.setUser(usertext);
		m.setTo(toArr);
		m.setFrom(usertext);
		m.setSubject("Capture");
		m.setBody("GPS Coor.\nLat:\nLong:\n");
		try {

			File sd = new File(Environment.getExternalStorageDirectory(),
					prefs.getString(LSPRConstants.PREF_FILE_NAME, "file_name"));
			m.addAttachment(sd.getAbsolutePath());

			// the following if statement may not be needed outside of testing
			// would replace with just m.send()
			// or just comment out the Toast lines
			if (m.send()) {
				Toast.makeText(this, "Email was sent successfully.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(
					this,
					"There was a problem sending the email.\nBody:"
							+ m.getBody() + "\nSubject: " + m.getSubject()
							+ "\nFrom: " + m.getFrom() + "\nUser: "
							+ m.getUser(), 
//							+ "\nPass: " + m.getPass(),
					Toast.LENGTH_LONG).show();

		}
	}
}
