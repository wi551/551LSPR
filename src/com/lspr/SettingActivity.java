package com.lspr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lspr.service.CameraGPSTriggerService;

public class SettingActivity extends DeviceAdminReceiver {

	static final private int SET_PASS = 0;

	static SharedPreferences getSamplePreferences(Context context) {
		return context.getSharedPreferences(
				DeviceAdminReceiver.class.getName(), 0);
	}

	static String PREF_PASSWORD_QUALITY = "password_quality";
	static String PREF_PASSWORD_LENGTH = "password_length";
	static String PREF_MAX_FAILED_PW_FOR_WIPE = "max_failed_pw_for_wipe";
	static String PREF_MAX_FAILED_PW_FOR_SERVICE = "max_failed_pw_for_service";
	static String PREF_EMAIL = "email_address";
	static String PREF_EMAIL_PASS = "email_password";
	static String PREF_BACK_FROM_SETTING_THRU_BACK_BTN = "back_from_setting_thru_back_btn";

	void showToast(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		showToast(context, "New password is set.");
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {

		SharedPreferences prefs = this.getSamplePreferences(context);
		final int maxFailedPwForService = prefs.getInt(
				PREF_MAX_FAILED_PW_FOR_SERVICE, 0);

		int attempt = Controller.mDPM.getCurrentFailedPasswordAttempts();
		// showToast(context, "attempt: " + attempt);

		if (attempt == maxFailedPwForService) {
			Intent serviceIntent = new Intent(context,
					CameraGPSTriggerService.class);
			context.startService(serviceIntent);
		}
	}

	public static class Controller extends Activity {

		static final int RESULT_ENABLE = 1;

		static DevicePolicyManager mDPM;
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
					try {
						int maxFailCount = Integer.parseInt(s.toString());
						setMaxFailedPwForService(maxFailCount);
					} catch (NumberFormatException e) {

					}

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
										"WARNING: This number must be strictly greater than number above",
										Toast.LENGTH_SHORT).show();
							}
						}
						setMaxFailedPwForWipe(maxFailCount);
					} catch (NumberFormatException e) {
					}
				}
			});

			// email.setOnClickListener(mMaxFailedPw1Listener);
			// emailPass.setOnClickListener(mMaxFailedPw1Listener);
			mMaxFailedPw1.clearFocus();
			mMaxFailedPw2.clearFocus();
			email.clearFocus();
			emailPass.clearFocus();
			mActivateBtn.setOnClickListener(mActivateBtnListener);

		}

		// let the user to set new password upon first launch of the application
		// private void setNewPassOnFirstLaunch() {
		//
		// Intent intent = new Intent(
		// DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
		// startActivityForResult(intent, SET_PASS);
		// }

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_BACK) {
				SharedPreferences prefs = getSamplePreferences(this);
				SharedPreferences settings = getSharedPreferences(
						LSPRActivity.PREFS_NAME, 0);
				boolean isFirstLaunch = settings.getBoolean("isFirstLaunch",
						true);

				if (isFirstLaunch) {
					return false;
				}
				prefs.edit()
						.putBoolean(PREF_BACK_FROM_SETTING_THRU_BACK_BTN, true)
						.commit();
			}
			return super.onKeyDown(keyCode, event);
		}

		// let the user set max number failed attempts
		void setMaxFailedPwForWipe(int length) {
			SharedPreferences prefs = getSamplePreferences(this);
			prefs.edit().putInt(PREF_MAX_FAILED_PW_FOR_WIPE, length).commit();
			updatePasswordPolicies();
		}

		void setMaxFailedPwForService(int length) {
			SharedPreferences prefs = getSamplePreferences(this);
			prefs.edit().putInt(PREF_MAX_FAILED_PW_FOR_SERVICE, length)
					.commit();
			updatePasswordPolicies();
		}

		void updatePasswordPolicies() {
			SharedPreferences prefs = getSamplePreferences(this);
			final int pwQuality = prefs.getInt(PREF_PASSWORD_QUALITY,
					DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
			// final int pwLength = prefs.getInt(PREF_PASSWORD_LENGTH, 0);
			final int maxFailedPwForService = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
			final int maxFailedPwForWipe = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_WIPE, 0);

			// save max password failed attempts
			boolean active = mDPM.isAdminActive(LSPRCN);
			if (active) {
				mDPM.setPasswordQuality(LSPRCN, pwQuality);
				// mDPM.setPasswordMinimumLength(LSPRCN, pwLength);
				mDPM.setMaximumFailedPasswordsForWipe(LSPRCN,
						maxFailedPwForWipe);
			}
		}

		boolean isValidEmailAddress(String aEmailAddress) {
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			// Match the given string with the pattern
			Matcher m = p.matcher(aEmailAddress);
			// check whether match is found
			boolean matchFound = m.matches();
			if (matchFound)
				return true;
			else {
				Toast.makeText(SettingActivity.Controller.this,
						R.string.email_invalid, Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		boolean isValidMaxFailedPasswordAttemptsValue() {

			SharedPreferences prefs = getSamplePreferences(this);

			int maxFailedPwForService = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
			int maxFailedPwForWipe = prefs.getInt(PREF_MAX_FAILED_PW_FOR_WIPE,
					0);

			if (maxFailedPwForService < maxFailedPwForWipe) {
				return true;
			} else {
				Toast.makeText(SettingActivity.Controller.this,
						R.string.invalid_attempts_value, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		}

		private boolean settingFieldsEmpty() {

			SharedPreferences prefs = getSamplePreferences(this);

			String emailText = email.getText().toString();
			String emailPassword = emailPass.getText().toString();
			int maxFailedPwForService = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
			int maxFailedPwForWipe = prefs.getInt(PREF_MAX_FAILED_PW_FOR_WIPE,
					0);

			if (emailText.isEmpty() || emailPassword.isEmpty()
					|| maxFailedPwForService == 0 || maxFailedPwForWipe == 0)
				return true;
			else
				return false;
		}

		// update password policy
		boolean saveSettings() {
			SharedPreferences prefs = getSamplePreferences(this);

			// validate and save email and password

			String emailText = email.getText().toString();
			String emailPassword = emailPass.getText().toString();

			if (isValidEmailAddress(emailText)
					&& isValidMaxFailedPasswordAttemptsValue()) {
				prefs.edit().putString(PREF_EMAIL, emailText).commit();
				prefs.edit().putString(PREF_EMAIL_PASS, emailPassword).commit();
				// save password policy
				updatePasswordPolicies();
				return true;
			} else {

				return false;
			}

		}

		private void populateFields() {

			SharedPreferences prefs = getSamplePreferences(this);
			final int pwQuality = prefs.getInt(PREF_PASSWORD_QUALITY,
					DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
			// final int pwLength = prefs.getInt(PREF_PASSWORD_LENGTH, 0);
			final int maxFailedPwForService = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
			final int maxFailedPwForWipe = prefs.getInt(
					PREF_MAX_FAILED_PW_FOR_WIPE, 0);
			String emailText = prefs.getString(PREF_EMAIL, "email@domain.com");
			String emailPassword = prefs.getString(PREF_EMAIL_PASS,
					"email_password");

			// populate fields
			mMaxFailedPw1.setText(Integer.toString(maxFailedPwForService));
			mMaxFailedPw2.setText(Integer.toString(maxFailedPwForWipe));
			email.setText(emailText);
			emailPass.setText(emailPassword);

		}

		@Override
		protected void onResume() {

			SharedPreferences settings = getSharedPreferences(
					LSPRActivity.PREFS_NAME, 0);
			boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);

			if (!isFirstLaunch) {
				populateFields();
			}

			mMaxFailedPw1.clearFocus();
			mMaxFailedPw2.clearFocus();
			email.clearFocus();
			emailPass.clearFocus();

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
			updatePasswordPolicies();
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
			SharedPreferences prefs = getSamplePreferences(this);
			prefs.edit()
					.putBoolean(PREF_BACK_FROM_SETTING_THRU_BACK_BTN, false)
					.commit();
			setResult(RESULT_OK);
			finish();
		}

		private OnClickListener mActivateBtnListener = new OnClickListener() {

			public void onClick(View v) {

				// if any of the fields is empty
				if (settingFieldsEmpty()) {
					Toast.makeText(SettingActivity.Controller.this,
							R.string.field_empty_warning, Toast.LENGTH_SHORT)
							.show();
				} else {
					boolean active = mDPM.isAdminActive(LSPRCN);
					if (active) {
						if (saveSettings()) {
							goBackToMain();
						}
					}
				}
			}
		};

	}

}
