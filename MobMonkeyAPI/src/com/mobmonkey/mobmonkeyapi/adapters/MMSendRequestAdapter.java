package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
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
								   //double latitude,
								   //double longitude,
								   String scheduleDate, 
								   String providerId, 
								   String locationId, 
								   String duration, 
								   //int radiusInYards, 
								   boolean repeating, 
								   String mediaType, 
								   String partnerId,
								   String emailAddress,
								   String password) {
		
		sendRequestUrl = MMAPIConstants.TEST_MOBMONKEY_URL + "requestmedia/" + mediaType;
		
		try {
			requestInfo = new JSONObject();
			requestInfo.put(MMAPIConstants.JSON_KEY_MESSAGE, message);
			requestInfo.put(MMAPIConstants.JSON_KEY_DURATION, 250);
			requestInfo.put(MMAPIConstants.JSON_KEY_PROVIDER_ID, "e048acf0-9e61-4794-b901-6a4bb49c3181"); //TODO: Provider ID is hard coded, change in future
			requestInfo.put(MMAPIConstants.JSON_KEY_RECURRING, repeating);
			//requestInfo.put(MMAPIConstants.JSON_KEY_LATITUDE, latitude);
			//requestInfo.put(MMAPIConstants.JSON_KEY_LONGITUDE, longitude);
			//requestInfo.put(MMAPIConstants.JSON_KEY_RADIUS_IN_YARDS, radiusInYards);
			
			HttpPut httpPut = new HttpPut(sendRequestUrl);
			StringEntity stringEntity = new StringEntity(requestInfo.toString());
			httpPut.setEntity(stringEntity);
			httpPut.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPut.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPut.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httpPut.setHeader(MMAPIConstants.KEY_AUTH, password);

			new MMPutAsyncTask(mmCallback).execute(httpPut);
		} catch(JSONException ex) {
			
		} catch(UnsupportedEncodingException ex) {
			
		}
		
		
	}
}
