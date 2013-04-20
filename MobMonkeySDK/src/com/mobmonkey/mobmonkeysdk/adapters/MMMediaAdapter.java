package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class MMMediaAdapter extends MMAdapter{
	private static final String TAG = "MMMediaAdapter: ";
	
	public static void acceptMedia(MMCallback mmCallback,
								   String partnerId,
								   String emailAddress,
								   String password,
								   String requestId,
								   String mediaId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA, MMSDKConstants.URI_PATH_REQUEST);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_MEDIA_ID, mediaId);
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpPost.setHeader(MMSDKConstants.KEY_AUTH, password);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	public static void rejectMedia(MMCallback mmCallback,
								   String partnerId,
								   String emailAddress,
								   String password,
								   String requestId,
								   String mediaId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA, MMSDKConstants.URI_PATH_REQUEST);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_MEDIA_ID, mediaId);
		
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		httpDelete.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpDelete.setHeader(MMSDKConstants.KEY_AUTH, password);
		
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
}
