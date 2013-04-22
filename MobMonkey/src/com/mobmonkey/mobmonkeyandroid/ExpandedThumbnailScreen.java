package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * @author Dezapp, LLC
 *
 */
public class ExpandedThumbnailScreen extends Activity {
	private static final String TAG = "ExpandedThumbnailScreen";
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.expanded_thumbnail_screen);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ImageView ivExpandedThumbnail = (ImageView) findViewById(R.id.ivexpandedthumbnail);
		
		ivExpandedThumbnail.setImageBitmap((Bitmap) getIntent().getParcelableExtra(MMSDKConstants.KEY_INTENT_EXTRA_IMAGE_MEDIA));
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.ibcancel:
				finish();
				break;
		}
	}
}