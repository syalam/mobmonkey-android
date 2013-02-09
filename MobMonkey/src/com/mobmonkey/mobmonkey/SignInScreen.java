package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author Dezapp, LLC
 *
 */
public class SignInScreen extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signinscreen);
		
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignin:
				break;
			case R.id.btnsigninfacebook:
				break;
			case R.id.btnsignintwitter:
				break;
			case R.id.btnsignup:
				startActivity(new Intent(SignInScreen.this, SignUpScreen.class));
				break;
		}
	}
}
