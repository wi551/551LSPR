/*
 * author :thomasloh
 * date: Feb 12
 * Description: Constants for this app
 * 
 * 
 */

package com.lspr.constants;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.SharedPreferences;

public class LSPRConstants {
	
	public static final int GO_TO_SETUP = 0;
	public static final int GO_TO_SETTINGS_FIRST_TIME = 1;
	public static final int GO_TO_SETTINGS = 2;
	public static final int ACTIVATE_DPM = 3;
	
	public static String PREF_PATH_NAME="file_path";
	public static String PREF_NAME = DeviceAdminReceiver.class.getName();
	public static String PREF_IS_FIRST_LAUNCH = "is_first_launch";
	public static String PREF_PASSWORD_QUALITY = "password_quality";
	public static String PREF_PASSWORD_LENGTH = "password_length";
	public static String PREF_MAX_FAILED_PW_FOR_WIPE = "max_failed_pw_for_wipe";
	public static String PREF_MAX_FAILED_PW_FOR_SERVICE = "max_failed_pw_for_service";
	public static String PREF_EMAIL = "email_address";
	public static String PREF_EMAIL_PASS = "email_password";
	public static String PREF_BACK_FROM_SETTING_THRU_BACK_BTN = "back_from_setting_thru_back_btn";
	
	
}
