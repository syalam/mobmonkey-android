package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
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
		setContentView(R.layout.termsofusescreen);
		TextView tvTOS = (TextView)findViewById(R.id.tvTOS);
		tvTOS.setMovementMethod(new ScrollingMovementMethod());
	}
}
