package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMediaAdapter extends MMAdapter {
	private static final String TAG = "MMMediaAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMMediaAdapter() {
		throw new AssertionError();
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
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);	
	}
}
