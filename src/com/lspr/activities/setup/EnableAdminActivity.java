/*
 * author :thomasloh
 * date: Feb 12
 * Description: Second screen of the setup. Allow the user to enable admin for this app 
 * 
 * 
 */

package com.lspr.activities.setup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lspr.R;
import com.lspr.receivers.DeviceAdminAndUnlockMonitorReceiver;

public class EnableAdminActivity extends Activity {

	static final int RESULT_ENABLE = 1;

	// Backend stuffs
	DevicePolicyManager mDPM;
	ActivityManager mAM;
	ComponentName LSPRCN;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.enableadmin);

		// REQUIRED
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		LSPRCN = new ComponentName(EnableAdminActivity.this,
				DeviceAdminAndUnlockMonitorReceiver.class);

		// Watch next button
		Button nextBtn = (Button) findViewById(R.id.eap_next_btn);
		nextBtn.setOnClickListener(nextBtnListener);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case (RESULT_ENABLE): {
			setResult(RESULT_OK);
			finish();
			break;
		}
		}

	}

	// Disable back button during setup
	@Override
	public void onBackPressed() {
		// do something on back.
		return;
	}

	// Enable device to be admin (prompt from user)
	private void enableAdmin() {
		// Launch the activity to have the user enable our admin.
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, LSPRCN);
		startActivityForResult(intent, RESULT_ENABLE);
	}

	// Callback for the enable admin button
	private OnClickListener nextBtnListener = new OnClickListener() {
		public void onClick(View v) {
			enableAdmin();
		}
	};

}