package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * Android {@link Activity} displays the Term of Use agreement to the user
 * @author Dezapp, LLC
 *
 */
public class TermsofuseScreen extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.terms_of_use_screen);
		TextView tvToS = (TextView)findViewById(R.id.tvtos);
		tvToS.setMovementMethod(new ScrollingMovementMethod());
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}
}
