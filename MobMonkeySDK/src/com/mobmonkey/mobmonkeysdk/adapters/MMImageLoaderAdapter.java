package com.mobmonkey.mobmonkeysdk.adapters;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMImageLoaderTask;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMImageLoaderAdapter {
	private static final String TAG = "MMImageLoaderAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMImageLoaderAdapter() {
		throw new AssertionError();
	}
	
	public static void loadImage(MMCallback mmCallback, String imageUrl) {
		Log.d(TAG, TAG + "loadImage");
		new MMImageLoaderTask(mmCallback).execute(imageUrl);
	}
}
