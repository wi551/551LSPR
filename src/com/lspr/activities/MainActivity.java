/*
 * author :thomasloh
 * date: Feb 12
 * Description: This is the main activity of the application. It shows an on/off button that activates/deactivates the unlock monitor
 * 
 */

package com.lspr.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.lspr.R;
import com.lspr.activities.setup.WelcomeActivity;
import com.lspr.constants.LSPRConstants;
import com.lspr.receivers.DeviceAdminAndUnlockMonitorReceiver;

public class MainActivity extends Activity {

	private ToggleButton activateBtn;
	private static SharedPreferences prefs;
	ComponentName component;

	public MainActivity() {
		super();
		prefs = null;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get preference for this app
		this.prefs = getApplicationContext().getSharedPreferences(
				LSPRConstants.PREF_NAME, 0);

		// If this is the first launch of the app
		if (isFirstLaunch()) {

			// go to setup pages
			goToSetup();

		}

		// set activate button watcher
		activateBtn = (ToggleButton) findViewById(R.id.activation_button);
		activateBtn.setOnClickListener(toggleService);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// Override callback for activities that returned to main page
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case (LSPRConstants.GO_TO_SETUP): {
			// When back from setup page, go to settings page
			goToSettings(LSPRConstants.GO_TO_SETTINGS_FIRST_TIME);
			break;
		}
		case (LSPRConstants.GO_TO_SETTINGS_FIRST_TIME): {

			// Save the preferences, isFirstLaunch will now be false
			prefs.edit().putBoolean(LSPRConstants.PREF_IS_FIRST_LAUNCH, false)
					.commit();

			// ACTIVATE AT MAIN
			activateBtn();

			break;
		}
		case (LSPRConstants.ACTIVATE_DPM): {
			break;
		}
		case (LSPRConstants.GO_TO_SETTINGS): {
			// SharedPreferences prefs = getSamplePreferences(this);

			boolean backThruBackBtn = prefs.getBoolean(
					LSPRConstants.PREF_BACK_FROM_SETTING_THRU_BACK_BTN, false);

			// if PREF_BACK_FROM_SETTING_THRU_BACK_BTN is true, means the user
			// hit the back button
			// else if false, means the user hit the activate button

			// the user hit activate
			if (!backThruBackBtn) {
				activateBtn();
				setUnlockMonitorTo(getUnlockMonitor(),
						PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			}
			break;
		}
		}

	}

	// Turn activate button on
	private void activateBtn() {

		// make sure light is on
		if (!activateBtn.isChecked()) {
			activateBtn.toggle();
		}

	}

	// check if this is the first launch of the application
	public boolean isFirstLaunch() {
		boolean isFirstLaunch = prefs.getBoolean(
				LSPRConstants.PREF_IS_FIRST_LAUNCH, true);

		return isFirstLaunch;
	}

	// Initiate setup upon first launch
	private void goToSetup() {
		Intent intent = new Intent(this, WelcomeActivity.class);
		startActivityForResult(intent, LSPRConstants.GO_TO_SETUP);
	}

	// Go to settings page. type is either first time or otherwise
	private void goToSettings(int type) {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivityForResult(intent, type);
	}

	// Override callback for key down events
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			goToSettings(LSPRConstants.GO_TO_SETTINGS); // If hit menu, go to
														// Settings page
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true); // If hit back, go to phone Home
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// Enable/Disable Unlock Monitor Receiver
	private void setUnlockMonitorTo(ComponentName receiver, int status) {
		getPackageManager().setComponentEnabledSetting(receiver, status,
				PackageManager.DONT_KILL_APP);
	}

	// Grab the unlock monitor receiver
	private ComponentName getUnlockMonitor() {

		ComponentName receiver = new ComponentName(MainActivity.this,
				DeviceAdminAndUnlockMonitorReceiver.class);

		return receiver;
	}

	// Listener for the activate button. ON = Turn on unlock monitor. OFF = Turn
	// off unlock monitor
	private OnClickListener toggleService = new OnClickListener() {

		public void onClick(View v) {

			// If intention is to activate, turn on the receiver
			if (activateBtn.isChecked()) {

				// get app preference and grab max failed password for wipe
				prefs = SettingActivity.getPreferences(getApplicationContext());
				final int maxFailedPwForWipe = prefs.getInt(
						LSPRConstants.PREF_MAX_FAILED_PW_FOR_WIPE, 0);

				// Restore max failed password for wipe from app preference
				SettingActivity.mDPM.setMaximumFailedPasswordsForWipe(
						SettingActivity.LSPRCN, maxFailedPwForWipe);

				// Enable unlock monitor
				setUnlockMonitorTo(getUnlockMonitor(),
						PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

				// Intent serviceIntent = new Intent(getApplicationContext(),
				// CameraGPSTriggerService.class);
				// getApplicationContext().startService(serviceIntent);

			} else {// If intention is to deactivate, turn off the receiver
				// Update max failed password for wipe to 0
				SettingActivity.mDPM.setMaximumFailedPasswordsForWipe(
						SettingActivity.LSPRCN, 0);

				// Disable unlock monitor
				setUnlockMonitorTo(getUnlockMonitor(),
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
			}

		}
	};

}