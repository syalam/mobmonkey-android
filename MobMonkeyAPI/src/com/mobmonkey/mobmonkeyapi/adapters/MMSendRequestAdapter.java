package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;

/**
 * @author Dezapp, LLC
 *
 */
public class MMSendRequestAdapter extends MMAdapter {
	private static final String TAG = "MMSendRequestAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMSendRequestAdapter() {
		throw new AssertionError();
	}
	
	public static void sendRequest(MMCallback mmCallback,
								   String message,
								   String scheduleDate, 
								   String providerId, 
								   String locationId, 
								   int duration, 
								   int radiusInYards, 
								   String repeating, 
								   String mediaType, 
								   String partnerId,
								   String emailAddress,
								   String password) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_REQUESTMEDIA, mediaType);
		createParamsInstance();
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMAPIConstants.JSON_KEY_MESSAGE, message);
			params.put(MMAPIConstants.JSON_KEY_SCHEDULE_DATE, scheduleDate);
			params.put(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
			params.put(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId);
			params.put(MMAPIConstants.JSON_KEY_DURATION, duration);
			params.put(MMAPIConstants.JSON_KEY_RADIUS_IN_YARDS, radiusInYards);
			if(repeating.equals(MMAPIConstants.REQUEST_REPEAT_RATE_NONE)) {
				params.put(MMAPIConstants.JSON_KEY_RECURRING, 0);
			} else {
				params.put(MMAPIConstants.JSON_KEY_RECURRING, true);
				long freq = 86400000; //TODO: Change these to calculated values. This is miliseconds in a day for Daily repeat.
				if(repeating.equals("Weekly"))
					freq = 604800000;
				if(repeating.equals("Monthly"))
					freq = 2629740000L;
				params.put(MMAPIConstants.JSON_KEY_FREQUENCY_IN_MS, freq);
			}
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);

			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch(JSONException ex) {
			
		} catch(UnsupportedEncodingException ex) {
			
		}		
	}
}
