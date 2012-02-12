package com.lspr.setup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lspr.R;
import com.lspr.SettingActivity;

public class EnableAdminActivity extends DeviceAdminReceiver {

	public static class Controller extends Activity {

		static final int RESULT_ENABLE = 1;

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
			LSPRCN = new ComponentName(Controller.this, SettingActivity.class);

			// Watch next button
			Button nextBtn = (Button) findViewById(R.id.eap_next_btn);
			nextBtn.setOnClickListener(nextBtnListener);

		}
		
		@Override
		public void onBackPressed() {
		// do something on back.
		return;
		}

		private void enableAdmin() {
			// Launch the activity to have the user enable our admin.
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, LSPRCN);
			startActivityForResult(intent, RESULT_ENABLE);
		}

		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {

			switch (requestCode) {
			case (RESULT_ENABLE): {
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
				enableAdmin();
			}
		};

	};

}