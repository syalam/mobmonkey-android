package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.*;

/**
 * Final adapter class that handles all the search location functionalities of MobMonkey Android
 * @author Dezapp, LLC
 * 
 */
public final class MMSearchLocationAdapter extends MMAdapter {
	private static final String TAG = "MMSearchLocationAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMSearchLocationAdapter() {
		throw new AssertionError();
	}
	
	private static void searchLocation(MMCallback mmCallback, String longitude, String latitude, int searchRadius, String name, String categoryID, String user, String auth, String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_SEARCH, MMAPIConstants.URI_PATH_LOCATION);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMAPIConstants.KEY_LONGITUDE, longitude);
			params.put(MMAPIConstants.KEY_LATITUDE, latitude);
			params.put(MMAPIConstants.KEY_RADIUS_IN_YARDS, searchRadius);
			if(!name.equals(MMAPIConstants.DEFAULT_STRING_EMPTY)) {
				params.put(MMAPIConstants.KEY_NAME, name);
			}
			params.put(MMAPIConstants.KEY_NAME, name);
			if(!categoryID.equals(MMAPIConstants.DEFAULT_STRING_EMPTY)) {
				params.put(MMAPIConstants.KEY_CATEGORY_IDS, categoryID);
			}
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, user);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, auth);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that searches locations with the specific input text
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param longitude Longitude value of user's current location
	 * @param latitude Latitude value of user's current location
	 * @param searchRadius Search radius from user's current location
	 * @param name The specific input text to be search
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void searchLocationWithText(MMCallback mmCallback, String longitude, String latitude, int searchRadius, String name, String user, String auth, String partnerId) {
		searchLocation(mmCallback, longitude, latitude, searchRadius, name, MMAPIConstants.DEFAULT_STRING_EMPTY, user, auth, partnerId);
	}

	public static void searchLocationByAddress(MMCallback mmCallback, String streetAddress, String locality, String region, String postcode, String user, String auth, String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_SEARCH, MMAPIConstants.URI_PATH_LOCATION);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMAPIConstants.KEY_STREET_ADDRESS, streetAddress);
			params.put(MMAPIConstants.KEY_LOCALITY, locality);
			params.put(MMAPIConstants.KEY_REGION, region);
			params.put(MMAPIConstants.KEY_POST_CODE, postcode);
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, user);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, auth);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param longitude
	 * @param latitude
	 * @param searchRadius
	 * @param categoryID
	 * @param user
	 * @param auth
	 * @param partnerId
	 */
	public static void searchLocationWithCategoryId(MMCallback mmCallback, String longitude, String latitude, int searchRadius, String categoryID, String user, String auth, String partnerId) {
		searchLocation(mmCallback, longitude, latitude, searchRadius, MMAPIConstants.DEFAULT_STRING_EMPTY, categoryID, user, auth, partnerId);
	}
	
	/**
	 * Function that searches all nearby location with the input text to be {@link MMAPIConstants#DEFAULT_STRING}
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param longitude Longitude value of user's current location
	 * @param latitude Latitude value of user's current location
	 * @param searchRadius Search radius from user's current location
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void searchAllNearby(MMCallback mmCallback, String longitude, String latitude, int searchRadius, String user, String auth, String partnerId) {
		searchLocation(mmCallback, longitude, latitude, searchRadius, MMAPIConstants.DEFAULT_STRING_EMPTY, MMAPIConstants.DEFAULT_STRING_EMPTY, user, auth, partnerId);
	}
}
