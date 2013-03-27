package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;

public class MMCheckinAdapter {

	private static String TAG = "MMCheckinAdapter";
	private static String CheckinURL;
	private static JSONObject locationInfo;
	
	public static void checkIn(MMCallback mmCallback,
								// headers
							   String partnerId,
							   String emailAddress,
							   String password,
							   // body
							   double latitude,
							   double longitude) {
		CheckinURL = MMAPIConstants.TEST_MOBMONKEY_URL + "checkin";
		
		locationInfo = new JSONObject();
		try {
			locationInfo.put(MMAPIConstants.KEY_LATITUDE, latitude);
			locationInfo.put(MMAPIConstants.KEY_LONGITUDE, longitude);
			
			HttpPost httppost = new HttpPost(CheckinURL);
			// add header
			httppost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httppost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httppost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httppost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
			new MMPostAsyncTask(mmCallback).execute(httppost);
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
	}
}
