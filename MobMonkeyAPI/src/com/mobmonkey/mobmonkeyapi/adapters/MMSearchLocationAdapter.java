package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.*;

/**
 * Final adapter class that handles all the search location functionalities of MobMonkey Android
 * @author Dezapp, LLC
 * 
 */
public final class MMSearchLocationAdapter {
	private static final String TAG = "MMSearchLocationAdapter: ";
	
	private static String searchLocationURL;
	
	/**
	 * Function that searches locations with the specific input text
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param longitude Longitude value of user's current location
	 * @param latitude Latitude value of user's current location
	 * @param name The specific input text to be search
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void searchLocationWithText(MMCallback mmCallback, String longitude, String latitude, String name, String user, String auth, String partnerId) {
		searchLocationURL = MMAPIConstants.MOBMONKEY_URL + "search/location";
		
		Log.d(TAG, TAG + "searchLocationURL: " + searchLocationURL);
		
		try {
			JSONObject params = new JSONObject();
			params.put(MMAPIConstants.KEY_LONGITUDE, longitude);
			params.put(MMAPIConstants.KEY_LATITUDE, latitude);
			params.put(MMAPIConstants.KEY_RADIUS_IN_YARDS, 1000);
			params.put(MMAPIConstants.KEY_NAME, name);
			
			Log.d(TAG, TAG + "PARAMS: " + params);
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			HttpPost httpPost = new HttpPost(searchLocationURL);
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, user);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, auth);
			
			for(Header header : httpPost.getAllHeaders()) {
				Log.d(TAG, TAG + "name: " + header.getName() + " value: " + header.getValue());
			}
			
			new MMAsyncTask(mmCallback).execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function that searches all nearby location with the input text to be {@link MMAPIConstants#DEFAULT_STRING}
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param longitude Longitude value of user's current location
	 * @param latitude Latitude value of user's current location
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void searchAllNearby(MMCallback mmCallback, String longitude, String latitude, String user, String auth, String partnerId) {
		Log.d(TAG, TAG + "searchAllNearby");
		searchLocationWithText(mmCallback, longitude, latitude, MMAPIConstants.DEFAULT_STRING, user, auth, partnerId);
	}
}
