package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPutAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class MMLocationAdapter extends MMAdapter {
	private static final String TAG = "MMLocationAdapter: ";
	
	private static MMGetAsyncTask getLocationInfoTask;
	
	private MMLocationAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param address
	 * @param description
	 * @param address_ext
	 * @param categoryIds
	 * @param countryCode
	 * @param latitude
	 * @param locality
	 * @param longitude
	 * @param name
	 * @param neighborhood
	 * @param phoneNumber
	 * @param postCode
	 * @param region
	 * @param providerId
	 * @param website
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void addLocation(MMCallback mmCallback,
								   String address,
								   String description,
								   String address_ext,
								   String categoryIds,
								   String countryCode,
								   double latitude,
								   String locality,
								   double longitude,
								   String name,
								   String neighborhood,
								   String phoneNumber,
								   String postCode,
								   String region,
								   String providerId,
								   String website,
								   String partnerId,
								   String user,
								   String auth) {
		
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		Log.d(TAG, TAG + "addLocation: " + uriBuilder.toString());

		try {			
			params.put(MMSDKConstants.JSON_KEY_ADDRESS, address);
			params.put(MMSDKConstants.JSON_KEY_DESCRIPTION, description);
			params.put(MMSDKConstants.JSON_KEY_ADDRESS_EXT, address_ext);
			params.put(MMSDKConstants.JSON_KEY_CATEGORY_IDS, categoryIds);
			params.put(MMSDKConstants.JSON_KEY_COUNTRY_CODE, countryCode);
			params.put(MMSDKConstants.JSON_KEY_LATITUDE, latitude);
			params.put(MMSDKConstants.JSON_KEY_LOCALITY, locality);
			params.put(MMSDKConstants.JSON_KEY_LONGITUDE, longitude);
			params.put(MMSDKConstants.JSON_KEY_NAME, name);
			params.put(MMSDKConstants.JSON_KEY_NEIGHBORHOOD, neighborhood);
			params.put(MMSDKConstants.JSON_KEY_PHONE_NUMBER, phoneNumber);
			params.put(MMSDKConstants.JSON_KEY_POSTCODE, postCode);
			params.put(MMSDKConstants.JSON_KEY_REGION, region);
			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
			params.put(MMSDKConstants.JSON_KEY_WEBSITE, website);
			
			Log.d(TAG, TAG + "userInfo: " + params.toString());
			
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
									  String locationId,
									  String providerId,
									  String address,
									  String address_ext,
									  String categoryIds,
									  String countryCode,
									  double latitude,
									  String locality,
									  double longitude,
									  String name,
									  String neighborhood,
									  String phoneNumber,
									  String postCode,
//									  String providerId,
									  String region,
									  String webSite,
									  String partnerId,
									  String user,
									  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		
		try {
			params.put(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId);
			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
			params.put(MMSDKConstants.JSON_KEY_ADDRESS, address);
			params.put(MMSDKConstants.JSON_KEY_ADDRESS_EXT, address_ext);
			params.put(MMSDKConstants.JSON_KEY_CATEGORY_IDS, categoryIds);
			params.put(MMSDKConstants.JSON_KEY_COUNTRY_CODE, countryCode);
			params.put(MMSDKConstants.JSON_KEY_LATITUDE, latitude);
			params.put(MMSDKConstants.JSON_KEY_LOCALITY, locality);
			params.put(MMSDKConstants.JSON_KEY_LONGITUDE, longitude);
			params.put(MMSDKConstants.JSON_KEY_NAME, name);
			params.put(MMSDKConstants.JSON_KEY_NEIGHBORHOOD, neighborhood);
			params.put(MMSDKConstants.JSON_KEY_PHONE_NUMBER, phoneNumber);
			params.put(MMSDKConstants.JSON_KEY_POSTCODE, postCode);
//			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
			params.put(MMSDKConstants.JSON_KEY_REGION, region);
			params.put(MMSDKConstants.JSON_KEY_WEBSITE, webSite);
			
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
	
	/**
	 * 
	 * @param mmCallback
	 * @param locationId
	 * @param providerId
	 */
	public static void deleteLocation(MMCallback mmCallback,
									  String locationId,
								      String providerId,
									  String partnerId,
									  String user,
									  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder);
		
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		httpDelete.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMSDKConstants.KEY_USER, user);
		httpDelete.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
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
	public static void createHotSpot(MMCallback mmCallback,
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
	
	public static void updateHotSpot(MMCallback mmCallback,
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
	
	/**
	 * 
	 * @param mmCallback
	 * @param locationId
	 * @param providerId
	 */
	public static void deleteHotSpot(MMCallback mmCallback,
									 String locationId,
									 String providerId,
									 String partnerId,
									 String user,
									 String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		createParamsInstance();
		
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		httpDelete.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMSDKConstants.KEY_USER, user);
		httpDelete.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param locationId
	 * @param providerId
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getLocationInfo(MMCallback mmCallback,
									   String locationId,
									   String providerId,
									   String partnerId,
									   String user,
									   String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_LOCATION);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, user);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		getLocationInfoTask = new MMGetAsyncTask(mmCallback);
		getLocationInfoTask.execute(httpGet);
	}
	
	public static void cancelGetLocationInfo() {
		if(getLocationInfoTask != null) {
			if(!getLocationInfoTask.isCancelled()) {
				getLocationInfoTask.cancel(true);
			}
		}
	}
}