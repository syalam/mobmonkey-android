package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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
		
		setContentView(R.layout.mm_expanded_thumbnail_screen);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ImageView ivExpandedThumbnail = (ImageView) findViewById(R.id.ivexpandedthumbnail);
		
		ivExpandedThumbnail.setImageBitmap((Bitmap) getIntent().getParcelableExtra(MMAPIConstants.KEY_INTENT_EXTRA_IMAGE_MEDIA));
	}

}
