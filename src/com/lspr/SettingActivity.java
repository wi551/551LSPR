package com.lspr;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends DeviceAdminReceiver {

	private static final String TAG1 = "Pattern";
	private static final String TAG2 = "Password Quality";
	static final private int SET_PASS = 0;

	static SharedPreferences getSamplePreferences(Context context) {
		return context.getSharedPreferences(
				DeviceAdminReceiver.class.getName(), 0);
	}

	static String PREF_PASSWORD_QUALITY = "password_quality";
	static String PREF_PASSWORD_LENGTH = "password_length";
	static String PREF_MAX_FAILED_PW_FOR_WIPE = "max_failed_pw_for_wipe";
	static String PREF_MAX_FAILED_PW_FOR_SERVICE = "max_failed_pw_for_service";

	void showToast(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		showToast(context, "New password is set.");
	}

	// @Override
	// public void onPasswordFailed(Context context, Intent intent) {
	// showToast(context, "Sample Device Admin: pw failed");
	// }

	// @Override
	// public void onEnabled(Context context, Intent intent) {
	// showToast(context, "Now please set a new password.");
	// }

	public static class Controller extends Activity {

		static final int RESULT_ENABLE = 1;

		DevicePolicyManager mDPM;
		ActivityManager mAM;
		ComponentName LSPRCN;

		Button mSetPasswordButton;
		Button mActivateBtn;
		Button mEnableAdminButton;
		EditText mMaxFailedPw1;
		EditText mMaxFailedPw2;
		EditText email;
		EditText emailPass;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			mAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			LSPRCN = new ComponentName(Controller.this, SettingActivity.class);

			// Inflate UI
			setContentView(R.layout.setting);

			// Link buttons
			// mEnableAdminButton = (Button) findViewById(R.id.enableAdminBtn);
			mSetPasswordButton = (Button) findViewById(R.id.setPw);
			mSetPasswordButton.setOnClickListener(mSetPasswordListener);
			mMaxFailedPw1 = (EditText) findViewById(R.id.max_failed_pw1_input);
			mMaxFailedPw2 = (EditText) findViewById(R.id.max_failed_pw2_input);
			email = (EditText) findViewById(R.id.emailInput);
			emailPass = (EditText) findViewById(R.id.emailPassInput);
			mActivateBtn = (Button) findViewById(R.id.activateBtn);

			// Watch buttons
			//

			mMaxFailedPw1.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					int maxFailCount = Integer.parseInt(s.toString());
					// the one that triggers camera and GPS service
					setMaxFailedPwForService(maxFailCount);
				}
			});

			mMaxFailedPw2.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					try {
						int maxFailCount = Integer.parseInt(s.toString());
						if (maxFailCount > 0) {

							int maxFailed1 = Integer.parseInt(mMaxFailedPw1
									.getText().toString());
							if (maxFailCount <= maxFailed1) {
								Toast.makeText(
										Controller.this,
										"WARNING: This number must be strictly greater than one that triggers camera and GPS service above",
										Toast.LENGTH_SHORT).show();
								mMaxFailedPw2.setText("");
							}
						}
						setMaxFailedPwForWipe(maxFailCount);
					} catch (NumberFormatException e) {
					}
				}
			});

			// email.setOnClickListener(mMaxFailedPw1Listener);
			// emailPass.setOnClickListener(mMaxFailedPw1Listener);
			mActivateBtn.setOnClickListener(mActivateBtnListener);

			// enable admin on the device
			// enableAdmin();
		}

		// let the user to set new password upon first launch of the application
		private void setNewPassOnFirstLaunch() {

			Intent intent = new Intent(
					DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
			startActivityForResult(intent, SET_PASS);
		}

		@Override
		public void onBackPressed() {
			// do something on back.
			
			SharedPreferences settings = getSharedPreferences(LSPRActivity.PREFS_NAME, 0);
			boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);
			
			if(isFirstLaunch){
				return;
			}
		}

		// let the user set max number failed attempts
		void setMaxFailedPwForWipe(int length) {
			SharedPreferences prefs = getSamplePreferences(this);
			prefs.edit().putInt(PREF_MAX_FAILED_PW_FOR_WIPE, length).commit();
			updatePolicies();
		}

		void setMaxFailedPwForService(int length) {
			SharedPreferences prefs = getSamplePreferences(this);
			prefs.edit().putInt(PREF_MAX_FAILED_PW_FOR_SERVICE, length)
					.commit();
			updatePolicies();
		}

		// update password policy
		void updatePolicies() {
			SharedPreferences prefs = getSamplePreferences(this);
			final int pwQuality = prefs.getInt(PREF_PASSWORD_QUALITY,
					DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
			// final int pwLength = prefs.getInt(PREF_PASSWORD_LENGTH, 0);
			final int maxFailedPwForService = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
			final int maxFailedPwForWipe = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_WIPE, 0);

			boolean active = mDPM.isAdminActive(LSPRCN);
			if (active) {
				mDPM.setPasswordQuality(LSPRCN, pwQuality);
				// mDPM.setPasswordMinimumLength(LSPRCN, pwLength);
				mDPM.setMaximumFailedPasswordsForWipe(LSPRCN,
						maxFailedPwForWipe);
			}
		}

		@Override
		protected void onResume() {
			super.onResume();
		}

		// void updateButtonStates() {
		// boolean active = mDPM.isAdminActive(LSPRCN);
		// if (active) {
		// mSetPasswordButton.setEnabled(true);
		// mResetPasswordButton.setEnabled(true);
		// } else {
		// mSetPasswordButton.setEnabled(false);
		// mResetPasswordButton.setEnabled(false);
		// }
		// }

		// private void enableAdmin(){
		// // Launch the activity to have the user enable our admin.
		// Intent intent = new Intent(
		// DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		// intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
		// LSPRCN);
		// startActivityForResult(intent, RESULT_ENABLE);
		// };

		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {

			// if(requestCode == RESULT_ENABLE && resultCode == RESULT_OK){
			// setPasswordQuality(DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
			// setNewPassOnFirstLaunch();
			// }
			// else if(requestCode == SET_PASS && resultCode == RESULT_OK){
			// // new password has been set
			// setResult(RESULT_OK);
			// }

			super.onActivityResult(requestCode, resultCode, data);
		}

		private OnClickListener mSetPasswordListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Launch the activity to have the user set a new password.
				Intent intent = new Intent(
						DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
				startActivity(intent);
			}
		};

		void setPasswordQuality(int quality) {
			SharedPreferences prefs = getSamplePreferences(this);
			prefs.edit().putInt(PREF_PASSWORD_QUALITY, quality).commit();
			updatePolicies();
		}

		// private OnClickListener mEnableListener = new OnClickListener() {
		// public void onClick(View v) {
		// // Launch the activity to have the user enable our admin.
		// Intent intent = new
		// Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		// intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
		// LSPRCN);
		// startActivityForResult(intent, RESULT_ENABLE);
		// }
		// };

		private void goBackToMain() {
			setResult(RESULT_OK);
			finish();
		}

		private OnClickListener mActivateBtnListener = new OnClickListener() {

			public void onClick(View v) {

				boolean active = mDPM.isAdminActive(LSPRCN);
				if (active) {
					updatePolicies();
					Log.e(TAG1,
							"Here trigger the failedPasswordMonitor Service");
					goBackToMain();
					
				}
			}
		};

	}

}
