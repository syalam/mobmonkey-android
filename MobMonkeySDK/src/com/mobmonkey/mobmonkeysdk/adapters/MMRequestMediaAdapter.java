package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpDelete;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDeleteAsyncTask;

public class MMRequestMediaAdapter extends MMAdapter{
	
	private static String TAG = "MMRequestMediaAdapter";
	
	private MMRequestMediaAdapter() {
		throw new AssertionError();
	}
	
	public static void deleteMedia(MMCallback mmCallback,
							  	   String requestId,
							  	   String isRecurring) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_REQUESTMEDIA);
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_ISRECURRING, isRecurring);
		Log.d(TAG, uriBuilder.toString());
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
}
