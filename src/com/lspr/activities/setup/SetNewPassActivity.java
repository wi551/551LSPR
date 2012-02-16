/*
 * author :thomasloh
 * date: Feb 12
 * Description: Third screen of the app. Allow the user to set new password. 
 * 
 * 
 */

package com.lspr.activities.setup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lspr.R;
import com.lspr.constants.LSPRConstants;
import com.lspr.receivers.DeviceAdminAndUnlockMonitorReceiver;

public class SetNewPassActivity extends Activity {

	static final int SET_PASS = 1;

	// Backend stuffs
	DevicePolicyManager mDPM;
	ActivityManager mAM;
	ComponentName LSPRCN;
	
	static SharedPreferences getSamplePreferences(Context context) {
		return context.getSharedPreferences(
				DeviceAdminReceiver.class.getName(), 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setnewpassword);

		// REQUIRED
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		LSPRCN = new ComponentName(this,
				DeviceAdminAndUnlockMonitorReceiver.class);

		// Watch next button
		Button nextBtn = (Button) findViewById(R.id.snp_next_btn);
		nextBtn.setOnClickListener(nextBtnListener);

		// Set password quality
		setPasswordType(DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case (SET_PASS): {
			setResult(RESULT_OK);
			finish();
			break;
		}
		}

	}
	
	@Override
	public void onBackPressed() {
		// do something on back.
		return;
	}

	// set password type
	private void setPasswordType(int quality) {

		SharedPreferences prefs = getSamplePreferences(this);
		prefs.edit().putInt(LSPRConstants.PREF_PASSWORD_QUALITY, quality).commit();
		updatePasswordPolicies();
	}

	// update password policy
	void updatePasswordPolicies() {
		SharedPreferences prefs = getSamplePreferences(this);
		final int pwQuality = prefs.getInt(LSPRConstants.PREF_PASSWORD_QUALITY,
				DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);

		boolean active = mDPM.isAdminActive(LSPRCN);
		if (active) {
			mDPM.setPasswordQuality(LSPRCN, pwQuality);
		}
	}

	// set new password during setup
	private void setNewPassOnFirstLaunch() {

		Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
		startActivityForResult(intent, SET_PASS);
	}

	// Listener for for set new password is tapped
	private OnClickListener nextBtnListener = new OnClickListener() {
		public void onClick(View v) {
			setNewPassOnFirstLaunch();
		}
	};

}