package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
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
	
	/**
	 * 
	 * @param mmCallback
	 * @param latitude
	 * @param longitude
	 * @param searchRadius
	 * @param name
	 * @param categoryID
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	private static void searchLocations(MMCallback mmCallback,
										double latitude,
										double longitude,
										int searchRadius,
										String name,
										String categoryIds,
										String partnerId,
										String user,
										String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SEARCH, MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.KEY_LATITUDE, latitude);
			params.put(MMSDKConstants.KEY_LONGITUDE, longitude);
			params.put(MMSDKConstants.KEY_RADIUS_IN_YARDS, searchRadius);
			if(!name.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				params.put(MMSDKConstants.KEY_NAME, name);
			}
			if(!categoryIds.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				params.put(MMSDKConstants.KEY_CATEGORY_IDS, categoryIds);
			}
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMSDKConstants.KEY_USER, user);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that searches locations with the specific input text
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param latitude Latitude value of user's current location
	 * @param longitude Longitude value of user's current location
	 * @param searchRadius Search radius from user's current location
	 * @param name The specific input text to be search
	 * @param partnerId MobMonkey unique partner id
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 */
	public static void searchLocationsWithText(MMCallback mmCallback,
											   double latitude,
											   double longitude,
											   int searchRadius,
											   String name,
											   String partnerId,
											   String user,
											   String auth) {
		searchLocations(mmCallback, latitude, longitude, searchRadius, name, MMSDKConstants.DEFAULT_STRING_EMPTY, partnerId, user, auth);
	}

	/**
	 * 
	 * @param mmCallback
	 * @param streetAddress
	 * @param locality
	 * @param region
	 * @param postcode
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void searchLocationsByAddress(MMCallback mmCallback,
												String streetAddress,
												String locality,
												String region,
												String postcode,
												String partnerId,
												String user,
												String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SEARCH, MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.KEY_STREET_ADDRESS, streetAddress);
			params.put(MMSDKConstants.KEY_LOCALITY, locality);
			params.put(MMSDKConstants.KEY_REGION, region);
			params.put(MMSDKConstants.KEY_POST_CODE, postcode);
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMSDKConstants.KEY_USER, user);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
			
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
	public static void searchLocationsWithCategoryIds(MMCallback mmCallback,
													  double latitude,
													  double longitude,
													  int searchRadius,
													  String categoryIds,
													  String partnerId,
													  String user,
													  String auth) {
		searchLocations(mmCallback, latitude, longitude, searchRadius, MMSDKConstants.DEFAULT_STRING_EMPTY, categoryIds, partnerId, user, auth);
	}
	
	/**
	 * Function that searches all nearby location with the input text to be {@link MMSDKConstants#DEFAULT_STRING}
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param latitude Latitude value of user's current location
	 * @param longitude Longitude value of user's current location
	 * @param searchRadius Search radius from user's current location
  	 * @param partnerId MobMonkey unique partner id
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 */
	public static void searchAllNearbyLocations(MMCallback mmCallback,
												double latitude,
												double longitude,
												int searchRadius,
												String partnerId,
												String user,
												String auth) {
		searchLocations(mmCallback, latitude, longitude, searchRadius, MMSDKConstants.DEFAULT_STRING_EMPTY, MMSDKConstants.DEFAULT_STRING_EMPTY, partnerId, user, auth);
	}
}
