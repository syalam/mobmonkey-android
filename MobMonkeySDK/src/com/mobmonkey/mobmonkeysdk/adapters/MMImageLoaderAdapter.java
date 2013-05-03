package com.mobmonkey.mobmonkeysdk.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMLoadImageAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

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
	
	/**
	 * 
	 * @param mmCallback
	 * @param imageUrl
	 */
	public static void loadImage(MMCallback mmCallback,
								 String imageUrl) {
		new MMLoadImageAsyncTask(mmCallback).execute(imageUrl);
	}
	
//	/**
//	 * 
//	 * @param context
//	 * @param mmCallback
//	 * @param videoUri
//	 */
//	public static void loadVideoThumbnail(Context context,
//										  MMCallback mmCallback,
//										  Uri videoUri) {
//		new MMGetVideoThumbnailTask(context, mmCallback).execute(videoUri);
//	}
}
