package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpDelete;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
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
		createUriBuilderInstance(MMSDKConstants.URI_PATH_REQUESTMEDIA);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_ISRECURRING, isRecurring);
		Log.d(TAG, uriBuilder.toString());
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		httpDelete.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpDelete.setHeader(MMSDKConstants.KEY_AUTH, password);
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
}
