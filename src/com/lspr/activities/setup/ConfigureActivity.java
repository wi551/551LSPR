/*
 * author :thomasloh
 * date: Feb 12
 * Description: Fifth screen of the app. Shows a configure message. 
 * 
 * 
 */

package com.lspr.activities.setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.lspr.R;

public class ConfigureActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);
        
        // Watch next button
        Button nextBtn = (Button) findViewById(R.id.cp_next_btn);
        nextBtn.setOnClickListener(nextBtnListener);
        
    }
    
    @Override
	protected void onStart(){
		Animation pushleftin = AnimationUtils.loadAnimation(ConfigureActivity.this,
				R.anim.push_left_in);
		
		findViewById(R.id.cp_title).startAnimation(pushleftin);
		findViewById(R.id.cp_msg).startAnimation(pushleftin);
		findViewById(R.id.cp_next_btn).startAnimation(pushleftin);
		
		super.onStart();
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

	// Go back to setup's main screen
	private void goBackToWelcome(){
		setResult(RESULT_OK);
		finish();
	}
	
	// Listener for configure button
	private OnClickListener nextBtnListener = new OnClickListener() {
      public void onClick(View v) {
    	  
    	  goBackToWelcome();
          
      }
  };
  
}