package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;

public class MMCheckinAdapter {
	private static String TAG = "MMCheckinAdapter: ";
	
	public static void checkIn(MMCallback mmCallback,
								// headers
							   String partnerId,
							   String emailAddress,
							   String password,
							   // body
							   double latitude,
							   double longitude) {
		
		Builder uriBuilder = Uri.parse(MMAPIConstants.MOBMONKEY_URL).buildUpon();
		uriBuilder.appendPath(MMAPIConstants.URI_PATH_CHECKIN);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		JSONObject locationInfo = new JSONObject();
		try {
			locationInfo.put(MMAPIConstants.KEY_LATITUDE, latitude);
			locationInfo.put(MMAPIConstants.KEY_LONGITUDE, longitude);
			
			HttpPost httppost = new HttpPost(uriBuilder.toString());
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
