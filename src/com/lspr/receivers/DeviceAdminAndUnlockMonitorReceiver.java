/*
 * author :thomasloh
 * date: Feb 12
 * Description: Device admin that deals with passwords stuffs and monitors unlock attempts.
 * 
 */

package com.lspr.receivers;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.lspr.constants.LSPRConstants;
import com.lspr.services.CameraGPSTriggerService;

public class DeviceAdminAndUnlockMonitorReceiver extends DeviceAdminReceiver {

	private static final String TAG = "UnlockMonitor";
	static DevicePolicyManager mDPM;

	static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(LSPRConstants.PREF_NAME, 0);
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		
		SharedPreferences prefs = getPreferences(context);
		final int maxFailedPwForService = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, 0);

		int attempt = mDPM.getCurrentFailedPasswordAttempts();

		if (attempt == maxFailedPwForService) {
//			Log.e(TAG, "Start Camera+GPS Service!");
//			Intent serviceIntent = new Intent(context,
//					CameraGPSTriggerService.class);
//			context.startService(serviceIntent);
		}
	}

	public static class Controller extends Activity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			setResult(RESULT_OK);
			finish();
		}

	}

}
