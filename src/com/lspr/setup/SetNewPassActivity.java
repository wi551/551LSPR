package com.lspr.setup;

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
import com.lspr.SettingActivity;

public class SetNewPassActivity extends DeviceAdminReceiver {

	static String PREF_PASSWORD_QUALITY = "password_quality";

	static SharedPreferences getSamplePreferences(Context context) {
		return context.getSharedPreferences(
				DeviceAdminReceiver.class.getName(), 0);
	}

	public static class Controller extends Activity {

		static final int SET_PASS = 1;

		DevicePolicyManager mDPM;
		ActivityManager mAM;
		ComponentName LSPRCN;

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.setnewpassword);

			// REQUIRED
			mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			mAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			LSPRCN = new ComponentName(Controller.this, SettingActivity.class);

			// Watch next button
			Button nextBtn = (Button) findViewById(R.id.snp_next_btn);
			nextBtn.setOnClickListener(nextBtnListener);

			// Set password quality
			setPasswordType(mDPM.PASSWORD_QUALITY_SOMETHING);
		}

		// set password type
		private void setPasswordType(int quality) {

			SharedPreferences prefs = getSamplePreferences(this);
			prefs.edit().putInt(PREF_PASSWORD_QUALITY, quality).commit();
			updatePolicies();
		}

		// update password policy
		void updatePolicies() {
			SharedPreferences prefs = getSamplePreferences(this);
			final int pwQuality = prefs.getInt(PREF_PASSWORD_QUALITY,
					DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);

			boolean active = mDPM.isAdminActive(LSPRCN);
			if (active) {
				mDPM.setPasswordQuality(LSPRCN, pwQuality);
			}
		}
		
		@Override
		public void onBackPressed() {
		// do something on back.
		return;
		}

		private void setNewPassOnFirstLaunch() {

			Intent intent = new Intent(
					DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
			startActivityForResult(intent, SET_PASS);
		}

		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {

			switch (requestCode) {
			case (SET_PASS): {
				setResult(RESULT_OK);
				finish();
				break;
			}
			}

//			super.onActivityResult(requestCode, resultCode, data);
		}

		@Override
		protected void onResume() {
			super.onResume();
		}

		private OnClickListener nextBtnListener = new OnClickListener() {
			public void onClick(View v) {
				setNewPassOnFirstLaunch();
			}
		};

	};

}