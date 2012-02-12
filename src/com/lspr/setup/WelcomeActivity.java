package com.lspr.setup;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		// Watch next button
		Button nextBtn = (Button) findViewById(R.id.welcome_next_btn);
		nextBtn.setOnClickListener(nextBtnListener);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		case SETUP_ENABLE_ADMIN: {
			goToSetNewPasswordSetup();
			break;

		}
		case SETUP_SET_NEW_PASSWORD: {
			goToConfigureSetup();
			break;

		}
		case SETUP_CONFIGURE: {
			setResult(RESULT_OK); // go back to main page
			finish();
			break;
		}

		}
		
//		super.onActivityResult(requestCode, resultCode, data);

		// When Enable Admin is done, go to setup page 3 (set new password)
		// if (requestCode == SETUP_ENABLE_ADMIN && resultCode== RESULT_OK) {
		// goToSetNewPasswordSetup();
		// }
		// else if (requestCode == SETUP_SET_NEW_PASSWORD && resultCode==
		// RESULT_OK) {
		// goToSetNewPasswordSetup();
		// }
		// else if (requestCode == SETUP_CONFIGURE && resultCode== RESULT_OK) {
		// goToConfigureSetup();
		// }
		// else if (requestCode == SETUP_CONFIGURE && resultCode== RESULT_OK) {
		// goToConfigureSetup();
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
	// do something on back.
	return;
	}

	private void goToConfigureSetup() {
		Intent intent = new Intent(this, ConfigureActivity.class);
		startActivityForResult(intent, SETUP_CONFIGURE);
	}

	private void goToSetNewPasswordSetup() {
		Intent intent = new Intent(this, SetNewPassActivity.Controller.class);
		startActivityForResult(intent, SETUP_SET_NEW_PASSWORD);
	}

	private void goToEnableAdminSetup() {
		Intent intent = new Intent(this, EnableAdminActivity.Controller.class);
		startActivityForResult(intent, SETUP_ENABLE_ADMIN);
	}

	private OnClickListener nextBtnListener = new OnClickListener() {
		public void onClick(View v) {

			// Go to setup page 2 (enable admin)
			goToEnableAdminSetup();

		}
	};

}