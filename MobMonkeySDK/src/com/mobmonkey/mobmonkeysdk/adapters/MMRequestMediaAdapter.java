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
								   String partnerId,
								   String emailAddress,
								   String password,
							  	   String requestId,
							  	   String isRecurring) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_REQUESTMEDIA);
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_ISRECURRING, isRecurring);
		Log.d(TAG, uriBuilder.toString());
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		httpDelete.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpDelete.setHeader(MMAPIConstants.KEY_AUTH, password);
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
}
