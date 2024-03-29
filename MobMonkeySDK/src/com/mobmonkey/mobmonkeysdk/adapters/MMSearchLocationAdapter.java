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
	 * @param searchRadius
	 * @param name
	 * @param categoryIds
	 */
	private static void searchLocations(MMCallback mmCallback,
										int searchRadius,
										String name,
										String categoryIds) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SEARCH, MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.KEY_LATITUDE, MMLocationManager.getLocationLatitude());
			params.put(MMSDKConstants.KEY_LONGITUDE, MMLocationManager.getLocationLongitude());
			params.put(MMSDKConstants.KEY_RADIUS_IN_YARDS, searchRadius);
			if(!name.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				params.put(MMSDKConstants.KEY_NAME, name);
			}
			if(!categoryIds.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				params.put(MMSDKConstants.KEY_CATEGORY_IDS, categoryIds);
			}
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPost httpPost = newHttpPostInstance();
			httpPost.setEntity(stringEntity);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that searches locations with the specific input text
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param searchRadius Search radius from user's current location
	 * @param name The specific input text to be search
	 */
	public static void searchLocationsWithText(MMCallback mmCallback,
											   int searchRadius,
											   String name) {
		searchLocations(mmCallback, searchRadius, name, MMSDKConstants.DEFAULT_STRING_EMPTY);
	}

	/**
	 * 
	 * @param mmCallback
	 * @param streetAddress
	 * @param locality
	 * @param region
	 * @param postcode
	 */
	public static void searchLocationsByAddress(MMCallback mmCallback,
												String streetAddress,
												String locality,
												String region,
												String postcode) {
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
			
			HttpPost httpPost = newHttpPostInstance();
			httpPost.setEntity(stringEntity);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param searchRadius
	 * @param categoryIds
	 */
	public static void searchLocationsWithCategoryIds(MMCallback mmCallback,
													  int searchRadius,
													  String categoryIds) {
		searchLocations(mmCallback, searchRadius, MMSDKConstants.DEFAULT_STRING_EMPTY, categoryIds);
	}
	
	/**
	 * Function that searches all nearby location with the input text to be {@link MMSDKConstants#DEFAULT_STRING}
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the search location url
	 * @param searchRadius Search radius from user's current location
	 */
	public static void searchAllNearbyLocations(MMCallback mmCallback,
												int searchRadius) {
		searchLocations(mmCallback, searchRadius, MMSDKConstants.DEFAULT_STRING_EMPTY, MMSDKConstants.DEFAULT_STRING_EMPTY);
	}
}
