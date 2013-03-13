package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMPutAsyncTask;

/**
 * @author Dezapp, LLC
 *
 */
public class MMSendRequestAdapter {
	private static final String TAG = "MMSendRequestAdapter: ";
	private static String sendRequestUrl;
	private static JSONObject requestInfo;
	
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
		
		sendRequestUrl = MMAPIConstants.MOBMONKEY_URL + "requestmedia/" + mediaType;
		
		try {
			HttpPost httpPut;
			requestInfo = new JSONObject();
			requestInfo.put(MMAPIConstants.JSON_KEY_MESSAGE, message);
			requestInfo.put(MMAPIConstants.JSON_KEY_SCHEDULEDATE, scheduleDate);
			requestInfo.put(MMAPIConstants.JSON_KEY_PROVIDER_ID, "e048acf0-9e61-4794-b901-6a4bb49c3181"); //TODO: Provider ID is hard coded, change in future
			requestInfo.put(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId);
			requestInfo.put(MMAPIConstants.JSON_KEY_DURATION, duration);
			requestInfo.put(MMAPIConstants.JSON_KEY_RADIUS_IN_YARDS, radiusInYards);
			if(repeating.equals("none"))
			{
				requestInfo.put(MMAPIConstants.JSON_KEY_RECURRING, false);
			}
			else
			{
				requestInfo.put(MMAPIConstants.JSON_KEY_RECURRING, true);
				long freq = 86400000; //TODO: Change these to calculated values. This is miliseconds in a day for Daily repeat.
				if(repeating.equals("Weekly"))
					freq = 604800000;
				if(repeating.equals("Monthly"))
					freq = 2629740000L;
				requestInfo.put(MMAPIConstants.JSON_KEY_FREQUENCY_IN_MS, freq);
			}
			httpPut = new HttpPost(sendRequestUrl);
			StringEntity stringEntity = new StringEntity(requestInfo.toString());
			httpPut.setEntity(stringEntity);
			httpPut.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPut.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPut.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httpPut.setHeader(MMAPIConstants.KEY_AUTH, password);

			new MMPostAsyncTask(mmCallback).execute(httpPut);
		} catch(JSONException ex) {
			
		} catch(UnsupportedEncodingException ex) {
			
		}
		
		
	}
}
