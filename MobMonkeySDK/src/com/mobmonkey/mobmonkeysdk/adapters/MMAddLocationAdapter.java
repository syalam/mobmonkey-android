package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMPutAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

public class MMAddLocationAdapter extends MMAdapter {
	private final static String TAG = "MMAddLocation: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMAddLocationAdapter() {
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
		Log.d(TAG, TAG + "signInURL: " + uriBuilder.toString());

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
}
