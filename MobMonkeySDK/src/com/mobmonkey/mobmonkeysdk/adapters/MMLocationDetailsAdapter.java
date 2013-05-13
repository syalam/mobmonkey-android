package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

/**
 * @author Dezapp, LLC
 *
 */
public class MMLocationDetailsAdapter extends MMAdapter {
	private static final String TAG = "MMMediaAdapter: ";
	
	private static MMGetAsyncTask mmGetAsyncTask;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMLocationDetailsAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param locationId
	 * @param providerId
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getLocationDetails(MMCallback mmCallback,
										  String locationId,
										  String providerId,
										  String partnerId,
										  String user,
										  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId)
		.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, user);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(httpGet);
	}
	
	/**
	 * 
	 * @param mmCallback
	 */
	public static void shareMediaForLocation(MMCallback mmCallback) {
		// TODO: implement for Share Media functionality
	}
	
	public static void cancelGetLcoationDetails() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}
