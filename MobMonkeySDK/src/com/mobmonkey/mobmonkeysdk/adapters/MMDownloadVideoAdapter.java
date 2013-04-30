package com.mobmonkey.mobmonkeysdk.adapters;

import android.content.Context;

import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMVideoDownloaderTask;

public class MMDownloadVideoAdapter extends MMAdapter{
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMDownloadVideoAdapter() {
		throw new AssertionError();
	}
	
	public static void downloadVideo(MMCallback mmCallback, 
									String mediaUrl) {
		new MMVideoDownloaderTask(mmCallback).execute(mediaUrl);
	}

}
