/*
 * author :thomasloh
 * date: Feb 12
 * Description: This is the setting activity of the app. It shows the fields for setting max failed unlock attempts and delivery parameters. 
 * 
 * 
 */

package com.lspr.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
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

import com.lspr.R;
import com.lspr.constants.LSPRConstants;
import com.lspr.receivers.DeviceAdminAndUnlockMonitorReceiver;

public class SettingActivity extends Activity {

	// Backend stuffs
	static DevicePolicyManager mDPM;
	static final int RESULT_ENABLE = 1;
	static ActivityManager mAM;
	static ComponentName LSPRCN;
	private static SharedPreferences prefs;

	// UI stuffs
	Button mSetPasswordButton;
	Button mActivateBtn;
	Button mEnableAdminButton;
	EditText mMaxFailedPw1;
	EditText mMaxFailedPw2;
	EditText email;
	EditText emailPass;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Declare backend stuffs
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		LSPRCN = new ComponentName(this,
				DeviceAdminAndUnlockMonitorReceiver.class);

		// Inflate UI
		setContentView(R.layout.setting);

		// Get app preference
		this.prefs = getApplicationContext().getSharedPreferences(
				LSPRConstants.PREF_NAME, 0);

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
		mMaxFailedPw1.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					int maxFailCount = Integer.parseInt(s.toString());
					if (maxFailCount > 0) {

						int maxFailed1 = Integer.parseInt(mMaxFailedPw1
								.getText().toString());
						if (maxFailCount <= maxFailed1) {
							Toast.makeText(
									getApplicationContext(),
									"WARNING: This number must be strictly greater than number above",
									Toast.LENGTH_SHORT).show();
						}
					}
					setMaxFailedPwForWipe(maxFailCount);
				} catch (NumberFormatException e) {
				}
			}
		});

		// UI adjustments
		mMaxFailedPw1.clearFocus();
		mMaxFailedPw2.clearFocus();
		email.clearFocus();
		emailPass.clearFocus();
		mActivateBtn.setOnClickListener(mActivateBtnListener);

	}

	// Override callback for key down events
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// if the user hit back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			boolean isFirstLaunch = prefs.getBoolean(
					LSPRConstants.PREF_IS_FIRST_LAUNCH, true);

			// check if is app first launch
			if (isFirstLaunch) {
				return false;
			}
			prefs.edit()
					.putBoolean(
							LSPRConstants.PREF_BACK_FROM_SETTING_THRU_BACK_BTN,
							true).commit();
		}
		return super.onKeyDown(keyCode, event);
	}

	// Override callback for onResume event
	@Override
	protected void onResume() {

		boolean isFirstLaunch = prefs.getBoolean(
				LSPRConstants.PREF_IS_FIRST_LAUNCH, true);

		if (!isFirstLaunch) {
			populateFields();
		}

		mMaxFailedPw1.clearFocus();
		mMaxFailedPw2.clearFocus();
		email.clearFocus();
		emailPass.clearFocus();

		super.onResume();
	}

	// Override callback for for when activities (if any) return to this
	// @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Set max number of unlock attempts to trigger data wipe
	// let the user set max number failed attempts
	private void setMaxFailedPwForWipe(int length) {
		prefs.edit().putInt(LSPRConstants.PREF_MAX_FAILED_PW_FOR_WIPE, length)
				.commit();
		updatePasswordPolicies();
	}

	// Function to get app preference
	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(LSPRConstants.PREF_NAME, 0);
	}

	// Show a notification on screen
	void showToast(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	// Set max number of unlock attempts to trigger camera and gps service
	// Set max number of unlock attempts to trigger camera and gps service
	private void setMaxFailedPwForService(int length) {
		prefs.edit()
				.putInt(LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, length)
				.commit();
		updatePasswordPolicies();
	}

	// Update password policies

	// Update password policies
	private void updatePasswordPolicies() {
		final int pwQuality = prefs.getInt(LSPRConstants.PREF_PASSWORD_QUALITY,
				DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
		final int maxFailedPwForService = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
		final int maxFailedPwForWipe = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_WIPE, 0);

		// save max password failed attempts
		boolean active = mDPM.isAdminActive(LSPRCN);
		if (active) {
			mDPM.setPasswordQuality(LSPRCN, pwQuality);
			// mDPM.setPasswordMinimumLength(LSPRCN, pwLength);
			mDPM.setMaximumFailedPasswordsForWipe(LSPRCN, maxFailedPwForWipe);
		}
	}

	// Email validation

	// Email validation
	boolean isValidEmailAddress(String aEmailAddress) {
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		// Match the given string with the pattern
		Matcher m = p.matcher(aEmailAddress);
		// check whether match is found
		boolean matchFound = m.matches();
		if (matchFound)
			return true;
		else {
			Toast.makeText(SettingActivity.this, R.string.email_invalid,
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	// max number of unlock attempts fields validation

	// Max password attempts field validation
	boolean isValidMaxFailedPasswordAttemptsValue() {

		int maxFailedPwForService = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
		int maxFailedPwForWipe = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_WIPE, 0);

		if (maxFailedPwForService < maxFailedPwForWipe) {
			return true;
		} else {
			Toast.makeText(SettingActivity.this,
					R.string.invalid_attempts_value, Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	// configuration fields validation

	// Settings page configuration fields validation
	private boolean settingFieldsEmpty() {

		String emailText = email.getText().toString();
		String emailPassword = emailPass.getText().toString();
		int maxFailedPwForService = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
		int maxFailedPwForWipe = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_WIPE, 0);

		if (emailText.isEmpty() || emailPassword.isEmpty()
				|| maxFailedPwForService == 0 || maxFailedPwForWipe == 0)
			return true;
		else
			return false;
	}

	// Save settings

	// Save configurations
	private boolean saveSettings() {
		// validate and save email and password
		String emailText = email.getText().toString();
		String emailPassword = emailPass.getText().toString();

		if (isValidEmailAddress(emailText)
				&& isValidMaxFailedPasswordAttemptsValue()) {
			prefs.edit().putString(LSPRConstants.PREF_EMAIL, emailText)
					.commit();
			prefs.edit()
					.putString(LSPRConstants.PREF_EMAIL_PASS, emailPassword)
					.commit();
			// save password policy
			updatePasswordPolicies();
			return true;
		} else {

			return false;
		}

	}

	// Populate configuration field upon back to settings page
	private void populateFields() {

		final int maxFailedPwForService = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
		final int maxFailedPwForWipe = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_WIPE, 0);
		String emailText = prefs.getString(LSPRConstants.PREF_EMAIL,
				"email@domain.com");
		String emailPassword = prefs.getString(LSPRConstants.PREF_EMAIL_PASS,
				"email_password");

		// populate fields
		mMaxFailedPw1.setText(Integer.toString(maxFailedPwForService));
		mMaxFailedPw2.setText(Integer.toString(maxFailedPwForWipe));
		email.setText(emailText);
		emailPass.setText(emailPassword);

	}

	// Listener for when reset password button is pressed
	private OnClickListener mSetPasswordListener = new OnClickListener() {
		public void onClick(View v) {
			// Launch the activity to have the user set a new password.
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
			startActivity(intent);
		}
	};

	// Go back to main page

	// Destroy this activity and go back to main page
	private void goBackToMain() {
		prefs.edit()
				.putBoolean(LSPRConstants.PREF_BACK_FROM_SETTING_THRU_BACK_BTN,
						false).commit();
		setResult(RESULT_OK);
		finish();
	}

	// Listener for activate button

	// i
	private OnClickListener mActivateBtnListener = new OnClickListener() {

		public void onClick(View v) {

			// if any of the fields is empty
			if (settingFieldsEmpty()) {
				Toast.makeText(SettingActivity.this,
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
