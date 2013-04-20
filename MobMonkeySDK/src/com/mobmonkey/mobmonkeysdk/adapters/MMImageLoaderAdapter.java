package com.mobmonkey.mobmonkeysdk.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMGetVideoThumbnailTask;
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
		new MMImageLoaderTask(mmCallback).execute(imageUrl);
	}
	
	public static void loadVideoThumbnail(Context context, MMCallback mmCallback, Uri videoUri) {
		new MMGetVideoThumbnailTask(context, mmCallback).execute(videoUri);
	}
}
