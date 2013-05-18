package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPutAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class MMHotSpotAdapter extends MMAdapter {
	private static final String TAG = "MMHotSpotAdapter: ";
	
	/**
	 * 
	 */
	private MMHotSpotAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param name
	 * @param description
	 * @param range
	 * @param locationId
	 * @param latitude
	 * @param longitude
	 * @param categoryIds
	 * @param countryCode
	 * @param locality
	 * @param phoneNumber
	 * @param providerId
	 * @param region
	 * @param webSite
	 * @param parentLocationId
	 * @param parentProviderId
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void createSubLocationWithLocationInfo(MMCallback mmCallback,
														 String name,
														 String description,
														 String range,
														 String locationId,
														 double latitude,
														 double longitude,
														 String categoryIds,
														 String countryCode,
														 String locality,
														 String phoneNumber,
														 String providerId,
														 String region,
														 String webSite,
														 String parentLocationId,
														 String parentProviderId,
														 String partnerId,
														 String user,
														 String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.JSON_KEY_LATITUDE, latitude);
			params.put(MMSDKConstants.JSON_KEY_LONGITUDE, longitude);
			params.put(MMSDKConstants.JSON_KEY_CATEGORY_IDS, categoryIds);
			params.put(MMSDKConstants.JSON_KEY_COUNTRY_CODE, countryCode);
			params.put(MMSDKConstants.JSON_KEY_LOCALITY, locality);
			params.put(MMSDKConstants.JSON_KEY_PHONE_NUMBER, phoneNumber);
			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
			params.put(MMSDKConstants.JSON_KEY_REGION, region);
			params.put(MMSDKConstants.JSON_KEY_WEBSITE, webSite);
			params.put(MMSDKConstants.JSON_KEY_NAME, name);
			params.put(MMSDKConstants.JSON_KEY_PARENT_LOCATION_ID, parentLocationId);
			params.put(MMSDKConstants.JSON_KEY_PARENT_PROVIDER_ID, parentProviderId);
			
			HttpPut httpPut = new HttpPut(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPut.setEntity(stringEntity);
			httpPut.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPut.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPut.setHeader(MMSDKConstants.KEY_USER, user);
			httpPut.setHeader(MMSDKConstants.KEY_AUTH, auth);

			new MMPutAsyncTask(mmCallback).execute(httpPut);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateLocation(MMCallback mmCallback,
									  String name,
									  String description,
									  String range,
									  String locationId,
									  double latitude,
									  double longitude,
									  String categoryIds,
									  String countryCode,
									  String locality,
									  String phoneNumber,
									  String providerId,
									  String region,
									  String webSite,
									  String parentLocationId,
									  String parentProviderId,
									  String partnerId,
									  String user,
									  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.JSON_KEY_LATITUDE, latitude);
			params.put(MMSDKConstants.JSON_KEY_LONGITUDE, longitude);
			params.put(MMSDKConstants.JSON_KEY_CATEGORY_IDS, categoryIds);
			params.put(MMSDKConstants.JSON_KEY_COUNTRY_CODE, countryCode);
			params.put(MMSDKConstants.JSON_KEY_LOCALITY, locality);
			params.put(MMSDKConstants.JSON_KEY_PHONE_NUMBER, phoneNumber);
			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
			params.put(MMSDKConstants.JSON_KEY_REGION, region);
			params.put(MMSDKConstants.JSON_KEY_WEBSITE, webSite);
			params.put(MMSDKConstants.JSON_KEY_NAME, name);
			params.put(MMSDKConstants.JSON_KEY_PARENT_LOCATION_ID, parentLocationId);
			params.put(MMSDKConstants.JSON_KEY_PARENT_PROVIDER_ID, parentProviderId);
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMSDKConstants.KEY_USER, user);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);

			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void deletLocation(MMCallback mmCallback,
									 String locationId,
								     String providerId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		HttpDelete httpDelet = new HttpDelete(uriBuilder.toString());
		
		new MMDeleteAsyncTask(mmCallback).execute(httpDelet);
	}
}
