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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lspr.R;
import com.lspr.activities.setup.ConfigureActivity;
import com.lspr.constants.LSPRConstants;
import com.lspr.modules.Mail;
import com.lspr.receivers.DeviceAdminAndUnlockMonitorReceiver;

public class SettingActivity extends Activity {

	// Backend stuffs
	static DevicePolicyManager mDPM;
	static final int RESULT_ENABLE = 1;
	static ActivityManager mAM;
	static ComponentName LSPRCN;
	private static SharedPreferences prefs;
	private int duration = 900000;
	private static final int DIALOG_VERIFY_EMAIL = 0;
	private static final int DIALOG2_ACTIVATING = 1;

	// UI stuffs
	Button mSetPasswordButton;
	Button mActivateBtn;
	Button mEnableAdminButton;
	EditText mMaxFailedPw1;
	Spinner mSpinner;
	EditText email;
	EditText emailPass;
	EditText emailPassV;
	ProgressDialog dialog1;
	ProgressDialog dialog2;

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
		mSetPasswordButton = (Button) findViewById(R.id.setPw);
		mSetPasswordButton.setOnClickListener(mSetPasswordListener);
		mMaxFailedPw1 = (EditText) findViewById(R.id.max_failed_pw1_input);
		mSpinner = (Spinner) findViewById(R.id.often_spinner);
		email = (EditText) findViewById(R.id.emailInput);
		emailPass = (EditText) findViewById(R.id.emailPassInput);
		// emailPassV = (EditText) findViewById(R.id.emailPassInputV);
		mActivateBtn = (Button) findViewById(R.id.activateBtn);

		// Set adapter to spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.durations, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				String selected = parent.getItemAtPosition(position).toString();
				if (selected == "15 minutes") {
					duration = 900000;
				} else if (selected == "30 minutes") {
					duration = 1800000;
				} else if (selected == "45 minutes") {
					duration = 2700000;
				} else if (selected == "1 hour") {
					duration = 3600000;
				}

				prefs.edit()
						.putInt(LSPRConstants.PREF_SEND_EMAIL_DURATION,
								duration).commit();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

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

		// UI adjustments
		dialog1 = new ProgressDialog(this);
		dialog2 = new ProgressDialog(this);
		mMaxFailedPw1.clearFocus();
		mSpinner.clearFocus();
		email.clearFocus();
		emailPass.clearFocus();
		mActivateBtn.setOnClickListener(mActivateBtnListener);

	}

	@Override
	protected void onStart() {
//		Animation pushupin = AnimationUtils.loadAnimation(
//				SettingActivity.this, R.anim.push_up_in);
//		Animation pushrightin= AnimationUtils.loadAnimation(
//				SettingActivity.this, R.anim.push_right_in);
//		Animation pushleftin = AnimationUtils.loadAnimation(
//				SettingActivity.this, R.anim.push_left_in);
//
//		findViewById(R.id.setting_title).startAnimation(pushrightin);
//		findViewById(R.id.max_failed_pw1_txt).startAnimation(pushrightin);
//		findViewById(R.id.max_failed_pw1_input).startAnimation(pushleftin);
//		findViewById(R.id.often_txt).startAnimation(pushrightin);
//		findViewById(R.id.often_spinner).startAnimation(pushleftin);
//		findViewById(R.id.dm_txt).startAnimation(pushrightin);
//		findViewById(R.id.emailContainer).startAnimation(pushleftin);
//		findViewById(R.id.passwordContainer).startAnimation(pushleftin);
//		mSetPasswordButton.startAnimation(pushrightin);
//		mActivateBtn.startAnimation(pushleftin);

		super.onStart();
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
		mSpinner.clearFocus();
		email.clearFocus();
		emailPass.clearFocus();

		super.onResume();
	}

	// Override callback for for when activities (if any) return to this
	// @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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

		// save max password failed attempts
		boolean active = mDPM.isAdminActive(LSPRCN);
		if (active) {
			mDPM.setPasswordQuality(LSPRCN, pwQuality);
		}
	}

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

	// configuration fields validation
	// Settings page configuration fields validation
	private boolean settingFieldsEmpty() {

		String emailText = email.getText().toString();
		String emailPassword = emailPass.getText().toString();
		int maxFailedPwForService = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, 0);

		if (emailText.isEmpty() || emailPassword.isEmpty()
				|| maxFailedPwForService == 0)
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
		// String emailPasswordV = emailPassV.getText().toString();
		String selected = mSpinner.getSelectedItem().toString();
		if (selected.equals("15 minutes")) {
			duration = 900000;
		} else if (selected.equals("30 minutes")) {
			duration = 1800000;
		} else if (selected.equals("45 minutes")) {
			duration = 2700000;
		} else if (selected.equals("1 hour")) {
			duration = 3600000;
		}

		if (isValidEmailAddress(emailText)) {
			prefs.edit().putString(LSPRConstants.PREF_EMAIL, emailText)
					.commit();
			prefs.edit()
					.putString(LSPRConstants.PREF_EMAIL_PASS, emailPassword)
					.commit();
			// save password policy
			updatePasswordPolicies();
			// save send email duration
			prefs.edit()
					.putInt(LSPRConstants.PREF_SEND_EMAIL_DURATION, duration)
					.commit();
			return true;
		} else {
			return false;
		}

	}

	// Populate configuration field upon back to settings page
	private void populateFields() {

		final int maxFailedPwForService = prefs.getInt(
				LSPRConstants.PREF_MAX_FAILED_PW_FOR_SERVICE, 0);
		String emailText = prefs.getString(LSPRConstants.PREF_EMAIL,
				"email@domain.com");
		int key = prefs.getInt(LSPRConstants.PREF_SEND_EMAIL_DURATION, 900000);
		int pos = 0;

		if (key == 900000) {
			pos = 0;

		} else if (key == 1800000) {
			pos = 1;
		} else if (key == 2700000) {
			pos = 2;
		} else if (key == 3600000) {
			pos = 3;
		}

		// populate fields
		mMaxFailedPw1.setText(Integer.toString(maxFailedPwForService));
		email.setText(emailText);
		mSpinner.setSelection(pos);
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

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_VERIFY_EMAIL: {
			dialog1.setMessage("Verifying email...");
			dialog1.setIndeterminate(true);
			dialog1.setCancelable(false);
			return dialog1;
		}
		case DIALOG2_ACTIVATING: {

			dialog2.setMessage("Activating...");
			dialog2.setIndeterminate(true);
			dialog2.setCancelable(false);
			return dialog2;
		}
		}
		return null;

	}

	// Listener for activate button
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
						new Thread(new Runnable() {
							public void run() {

								SettingActivity.this
										.runOnUiThread(new Runnable() {
											public void run() {
												showDialog(DIALOG_VERIFY_EMAIL);
											}
										});
								// test for valid email and pass by sending a
								// email
								// pass go to main
								if (testMail()) {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									SettingActivity.this
											.runOnUiThread(new Runnable() {
												public void run() {
													dialog2.cancel();
												}
											});
									goBackToMain();
								} else {
									// fail test display error message
									SettingActivity.this
											.runOnUiThread(new Runnable() {
												public void run() {
													dialog1.cancel();
													Animation shake = AnimationUtils
															.loadAnimation(
																	SettingActivity.this,
																	R.anim.shake);
													findViewById(
															R.id.deliveryContainer)
															.startAnimation(
																	shake);
													showToast(
															SettingActivity.this,
															"Invalid email or password.");
												}
											});
								}
							}

							private boolean testMail() {
								boolean result = false;
								final Mail m = new Mail();
								String usertext = prefs.getString(
										LSPRConstants.PREF_EMAIL,
										"domain@email.com");
								String emailPass = prefs.getString(
										LSPRConstants.PREF_EMAIL_PASS,
										"password");
								String[] toArr = { usertext };
								m.setPass(emailPass);
								m.setUser(usertext);
								m.setTo(toArr);
								m.setFrom(usertext);
								m.setSubject("LSPR App Email Test");
								m.setBody("Congratulations, email and password are valid."
										+ "\nAll future emails will be sent to this address "
										+ usertext
										+ "\nThank you for using the Lost, Stolen Phone Recovery App.");
								try {
									if (m.send()) {
										SettingActivity.this
												.runOnUiThread(new Runnable() {
													public void run() {
														dialog1.cancel();
														showDialog(DIALOG2_ACTIVATING);
													}
												});
										result = true;
									} else
										result = false;
								} catch (Exception e) {
									e.printStackTrace();
								}

								return result;
							}
						}).start();
					}
				}
			}
		}
	};
}
