package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.net.Uri;
import android.net.Uri.Builder;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMediaAdapter {
	private static final String TAG = "MMMediaAdapter: ";
	private static String mediaURL = MMAPIConstants.MOBMONKEY_URL + "media";
	
	public static void retrieveAllMediaForLocation(MMCallback mmCallback, String emailAddress, String password, String partnerId, String locationId, String providerId) {
		Builder uriBuilder = Uri.parse(mediaURL).buildUpon();
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId)
			.appendQueryParameter(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);	
	}
}
