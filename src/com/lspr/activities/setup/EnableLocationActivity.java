/*
 * author :thomasloh
 * date: Feb 12
 * Description: Third screen of the setup. Allow the user to enable location services for this app 
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.lspr.R;
import com.lspr.receivers.DeviceAdminAndUnlockMonitorReceiver;

public class EnableLocationActivity extends Activity {

	static final int RESULT_ENABLE_LOCATION = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.enablelocation);

		// Watch next button
		Button nextBtn = (Button) findViewById(R.id.elp_next_btn);
		nextBtn.setOnClickListener(nextBtnListener);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStart(){
		Animation pushleftin = AnimationUtils.loadAnimation(EnableLocationActivity.this,
				R.anim.push_left_in);
		
		findViewById(R.id.elp_title).startAnimation(pushleftin);
		findViewById(R.id.elp_msg).startAnimation(pushleftin);
		findViewById(R.id.elp_next_btn).startAnimation(pushleftin);
		
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case (RESULT_ENABLE_LOCATION): {
			setResult(RESULT_OK);
			finish();
			break;
		}
		}

	}

	// Disable back button during setup
	@Override
	public void onBackPressed() {
		return;
	}

	private void enableLocation() {
		// Launch the activity to have the user enable location services
		Toast.makeText(getApplicationContext(), "Please check all. To continue, please tap the back button on your device.", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(intent, RESULT_ENABLE_LOCATION);
	}

	// Callback for the enable location button
	private OnClickListener nextBtnListener = new OnClickListener() {
		public void onClick(View v) {
			enableLocation();
		}
	};

}