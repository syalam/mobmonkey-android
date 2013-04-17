package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMPutAsyncTask;

public class MMAddLocationAdapter extends MMAdapter {
	private final static String TAG = "MMAddLocation: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMAddLocationAdapter() {
		throw new AssertionError();
	}
	
	public static void addLocation(MMCallback mmCallback, String emailAddress, String password, String partnerId, String address, String description, 
			String address_ext, String categoryIds, String countryCode, String latitude, String locality, String longitude, String name, String neighborhood, String phoneNumber,
			String postCode, String providerId, String region, String website) {
		
		createUriBuilderInstance(MMAPIConstants.URI_PATH_LOCATION);
		createParamsInstance();
		Log.d(TAG, TAG + "signInURL: " + uriBuilder.toString());

		try {
			params.put(MMAPIConstants.JSON_KEY_ADDRESS, address);
			//userInfo.put(MMAPIConstants.JSON_KEY_DESCRIPTION, description);
			//userInfo.put(MMAPIConstants.JSON_KEY_ADDRESS_EXT, address_ext);
			params.put(MMAPIConstants.JSON_KEY_CATEGORY_IDS, categoryIds);
			params.put(MMAPIConstants.JSON_KEY_COUNTRY_CODE, countryCode);
			params.put(MMAPIConstants.JSON_KEY_LATITUDE, latitude);
			params.put(MMAPIConstants.JSON_KEY_LOCALITY, locality);
			params.put(MMAPIConstants.JSON_KEY_LONGITUDE, longitude);
			params.put(MMAPIConstants.JSON_KEY_NAME, name);
			//userInfo.put(MMAPIConstants.JSON_KEY_PHONENUMBER, phoneNumber);
			params.put(MMAPIConstants.JSON_KEY_POSTCODE, postCode);
			params.put(MMAPIConstants.JSON_KEY_PROVIDER_ID, "e048acf0-9e61-4794-b901-6a4bb49c3181"); //TODO: Provider ID is hard coded, change in future
			params.put(MMAPIConstants.JSON_KEY_REGION, region);
			//userInfo.put(MMAPIConstants.JSON_KEY_WEBSITE, website);
			
			Log.d(TAG, TAG + "userInfo: " + params.toString());
			
			HttpPut httpPut = new HttpPut(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPut.setEntity(stringEntity);
			httpPut.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPut.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPut.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httpPut.setHeader(MMAPIConstants.KEY_AUTH, password);

			new MMPutAsyncTask(mmCallback).execute(httpPut);			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}