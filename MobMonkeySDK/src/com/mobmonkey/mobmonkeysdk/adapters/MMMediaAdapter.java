package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.*;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMediaAdapter extends MMAdapter {
	private static final String TAG = "MMMediaAdapter: ";
	
	private static MMGetAsyncTask mmGetAsyncTask;
	
	/**
	 * 
	 */
	private MMMediaAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param locationId
	 * @param providerId
	 */
	public static void retrieveAllMediaForLocation(MMCallback mmCallback,												   
												   String locationId,
												   String providerId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId)
			.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);

		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		HttpGet httpGet = newHttpGetInstance();

		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(httpGet);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param requestId
	 * @param mediaId
	 */
	public static void acceptMedia(MMCallback mmCallback,
								   String requestId,
								   String mediaId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA, MMSDKConstants.URI_PATH_REQUEST);

		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_MEDIA_ID, mediaId);

		HttpPost httpPost = newHttpPostInstance();

		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param requestId
	 * @param mediaId
	 */
	public static void rejectMedia(MMCallback mmCallback,
								   String requestId,
								   String mediaId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA, MMSDKConstants.URI_PATH_REQUEST);

		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_MEDIA_ID, mediaId);

		HttpDelete httpDelete = newHttpDeleteInstance();

		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
	
	/**
	 * 
	 */
	public static void cancelRetrieveAllMediaForLocation() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}
