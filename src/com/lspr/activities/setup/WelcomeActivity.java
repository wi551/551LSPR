/*
 * author :thomasloh
 * date: Feb 12
 * Description: First screen of the app and the main setup page. 
 * 
 * 
 */

package com.lspr.activities.setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lspr.R;

public class WelcomeActivity extends Activity {

	static final private int SETUP_ENABLE_ADMIN = 0;
	static final private int SETUP_SET_NEW_PASSWORD = 1;
	static final private int SETUP_CONFIGURE = 2;
	static final private int SETUP_ENABLE_LOCATION = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Inflate UI
		setContentView(R.layout.welcome);

		// Watch next button
		Button nextBtn = (Button) findViewById(R.id.welcome_next_btn);
		nextBtn.setOnClickListener(nextBtnListener);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		// back from enable admin page, go set new password screen
		case SETUP_ENABLE_ADMIN: {
			goToEnableLocationSetup();

			break;

		}

		case SETUP_ENABLE_LOCATION: {
			goToSetNewPasswordSetup();
			break;

		}

		// back from set new password page, go configure message screen
		case SETUP_SET_NEW_PASSWORD: {
			goToConfigureSetup();
			break;

		}

		// back from configure message page, go back to main page
		case SETUP_CONFIGURE: {
			setResult(RESULT_OK);
			finish();
			break;
		}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// Just to disable the back button during setup
	@Override
	public void onBackPressed() {
		// do something on back.
		return;
	}

	// Go configure message page
	private void goToConfigureSetup() {
		Intent intent = new Intent(this, ConfigureActivity.class);
		startActivityForResult(intent, SETUP_CONFIGURE);
	}

	// Go set new password page
	private void goToSetNewPasswordSetup() {
		Intent intent = new Intent(this, SetNewPassActivity.class);
		startActivityForResult(intent, SETUP_SET_NEW_PASSWORD);
	}

	// Go enable admin page
	private void goToEnableAdminSetup() {
		Intent intent = new Intent(this, EnableAdminActivity.class);
		startActivityForResult(intent, SETUP_ENABLE_ADMIN);
	}

	// Go enable location page
	private void goToEnableLocationSetup() {
		Intent intent = new Intent(this, EnableLocationActivity.class);
		startActivityForResult(intent, SETUP_ENABLE_LOCATION);
	}

	private OnClickListener nextBtnListener = new OnClickListener() {
		public void onClick(View v) {

			// Go to setup page 2 (enable admin)
			goToEnableAdminSetup();

		}
	};

}