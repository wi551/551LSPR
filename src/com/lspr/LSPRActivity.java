package com.lspr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lspr.setup.WelcomeActivity;

public class LSPRActivity extends Activity {

	static final private int GO_TO_SETUP = 0;
	static final private int GO_TO_SETTINGS_FIRST_TIME = 1;
	static final private int GO_TO_SETTINGS = 2;
	public static final String PREFS_NAME = "FIRSTTIMEPREF";
	private static final String TAG = "TAG";

	Button activateBtn;

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
		activateBtn = (Button) findViewById(R.id.activation_button);
		activateBtn.setOnClickListener(startService);
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
			reactivate();
			break;
		}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			goToSettings(GO_TO_SETTINGS);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void reactivate(){
		// HERE RESTARTS THE SERVICE
		activateBtn.setOnClickListener(stopService);
		activateBtn.performClick();
		activateBtn.setOnClickListener(startService);
		activateBtn.performClick();
	}
	
	private void activate(){
		
		activateBtn.setOnClickListener(startService);
		activateBtn.performClick();
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------

	// HERE TRIGGERS THE FAILED LOGIN MONITOR "SERVICE" (REPORT
	// CURRENT UNSUCCESSFUL LOGIN ATTEMTPS)

	// IN THAT SERVICE, ONCE TRIGGERED(REACHED MAX FAILED
	// ATTEMPTS), TRIGGERS THE CAMERA + GPS SERVICE(ANOTHER) AND
	// PASS EMAIL AND EMAILPASS TO IT

	// PASS EMAIL AND PASS HERE?

	// ----------------------------------------------------------------------------------------------------------------------------------------------------
	private OnClickListener startService = new OnClickListener() {

		public void onClick(View v) {
			
			// start service, make sure button is enabled.
			activateBtn.setEnabled(true);
			
			// starts service
		}
	};
	
	private OnClickListener stopService = new OnClickListener() {

		public void onClick(View v) {
			
			// stop service. make sure button is disabled.
			activateBtn.setEnabled(false);
			
			// stops service
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}
}