package com.mobmonkey.mobmonkeysdk.adapters;

import android.content.Context;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMDownloadVideoAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

public class MMDownloadVideoAdapter extends MMAdapter{
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMDownloadVideoAdapter() {
		throw new AssertionError();
	}
	
	public static void downloadVideo(MMCallback mmCallback, 
									 String mediaUrl,
									 int videoThumbnailCount) {
		new MMDownloadVideoAsyncTask(mmCallback).execute(mediaUrl, Integer.toString(videoThumbnailCount));
	}

}
