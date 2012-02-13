package com.lspr;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.lspr.service.CameraGPSTriggerService;
import com.lspr.service.RestartsService;
import com.lspr.setup.WelcomeActivity;

public class LSPRActivity extends Activity {

	static final private int GO_TO_SETUP = 0;
	static final private int GO_TO_SETTINGS_FIRST_TIME = 1;
	static final private int GO_TO_SETTINGS = 2;
	public static final String PREFS_NAME = "FIRSTTIMEPREF";
	private static final String TAG = "TAG";

	ToggleButton activateBtn;

	static SharedPreferences getSamplePreferences(Context context) {
		return context.getSharedPreferences(
				DeviceAdminReceiver.class.getName(), 0);
	}

	public boolean isFirstLaunch() {
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);

		return isFirstLaunch;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (isFirstLaunch()) {

			// go to setup pages
			goToSetup();

		}

		// set activate button watcher
		activateBtn = (ToggleButton) findViewById(R.id.activation_button);
		// activateBtn.setOnClickListener(startService);
	}

	private void goToSetup() {
		Intent intent = new Intent(this, WelcomeActivity.class);
		startActivityForResult(intent, GO_TO_SETUP);
	}

	private void goToSettings(int type) {
		Intent intent = new Intent(this, SettingActivity.Controller.class);
		startActivityForResult(intent, type);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case (GO_TO_SETUP): {
			goToSettings(GO_TO_SETTINGS_FIRST_TIME);
			break;
		}
		case (GO_TO_SETTINGS_FIRST_TIME): {

			// Save the preferences, isFirstLaunch will now be false
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("isFirstLaunch", false);
			editor.commit();

			// ACTIVATE AT MAIN
			activate();
			break;
		}
		case (GO_TO_SETTINGS): {
			SharedPreferences prefs = getSamplePreferences(this);

			boolean backThruBackBtn = prefs
					.getBoolean(
							SettingActivity.PREF_BACK_FROM_SETTING_THRU_BACK_BTN,
							false);

			if (activateBtn.isChecked() || !backThruBackBtn) {
				activate();
			}
			break;
		}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			goToSettings(GO_TO_SETTINGS);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void activate() {

		// make sure light is on
		if (!activateBtn.isChecked()) {
			activateBtn.toggle();
		}
		
		callsRestartsService();
		
	}
	
	private void callsRestartsService(){
		Intent intent = new Intent(this, RestartsService.class);
		startService(intent);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------

	// HERE TRIGGERS THE FAILED LOGIN MONITOR "SERVICE" (REPORT
	// CURRENT UNSUCCESSFUL LOGIN ATTEMTPS)

	// IN THAT SERVICE, ONCE TRIGGERED(REACHED MAX FAILED
	// ATTEMPTS), TRIGGERS THE CAMERA + GPS SERVICE(ANOTHER) AND
	// PASS EMAIL AND EMAILPASS TO IT

	// PASS EMAIL AND PASS HERE?

	// ----------------------------------------------------------------------------------------------------------------------------------------------------
//	private OnClickListener startService = new OnClickListener() {
//
//		public void onClick(View v) {
//
//			// start service, make sure button is enabled.
//
//			// starts service
//		}
//	};
//
//	private OnClickListener stopService = new OnClickListener() {
//
//		public void onClick(View v) {
//
//			// stop service. make sure button is disabled.
//
//			// stops service
//		}
//	};

	@Override
	protected void onResume() {
		super.onResume();
	}
}