package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.*;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMediaAdapter extends MMAdapter {
	private static final String TAG = "MMMediaAdapter: ";
	
	private static MMGetAsyncTask mmGetAsyncTask;
	
	private MMMediaAdapter() {
		throw new AssertionError();
	}
	
	public static void retrieveAllMediaForLocation(MMCallback mmCallback, String emailAddress, String password, String partnerId, String locationId, String providerId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId)
			.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);

		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, password);

		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(httpGet);
	}
	
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
	
	public static void cancelRetrieveAllMediaForLocation() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}
