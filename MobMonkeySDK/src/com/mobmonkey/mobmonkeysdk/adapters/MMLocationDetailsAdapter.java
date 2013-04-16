package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMGetAsyncTask;

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
	
	public static void getLocationDetails(MMCallback mmCallback, String locationId, String providerId, String emailAddress, String password, String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_LOCATION);
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId)
		.appendQueryParameter(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);	
	}
	
	public static void retrieveAllMediaForLocation(MMCallback mmCallback, String emailAddress, String password, String partnerId, String locationId, String providerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_MEDIA);
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId)
			.appendQueryParameter(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(httpGet);
	}
	
	public static void shareMediaForLocation(MMCallback mmCallback) {
		// TODO: implement for Share Media functionality

	}
	
	public static void cancelRetrieveAllMediaForLocation() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}
