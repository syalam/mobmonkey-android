package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMDeviceUUID;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMPutAsyncTask;

public class MMAddLocationAdapter {
	private final static String TAG = "MMSignIn: ";
	private static String addLocationURL;
	private static JSONObject userInfo;
	
	public static void addLocation(MMCallback mmCallback, String emailAddress, String password, String partnerId, String address, String description, 
			String address_ext, String categoryIds, String countryCode, String latitude, String locality, String longitude, String name, String neighborhood, String phoneNumber,
			String postCode, String providerId, String region, String website) {
		addLocationURL = MMAPIConstants.MOBMONKEY_URL + "location";
		
		Log.d(TAG, TAG + "signInURL: " + addLocationURL);

		try{
			
			userInfo = new JSONObject();
			userInfo.put(MMAPIConstants.JSON_KEY_ADDRESS, address);
			//userInfo.put(MMAPIConstants.JSON_KEY_DESCRIPTION, description);
			//userInfo.put(MMAPIConstants.JSON_KEY_ADDRESS_EXT, address_ext);
			userInfo.put(MMAPIConstants.JSON_KEY_CATEGORY_IDS, categoryIds);
			userInfo.put(MMAPIConstants.JSON_KEY_COUNTRY_CODE, countryCode);
			userInfo.put(MMAPIConstants.JSON_KEY_LATITUDE, latitude);
			userInfo.put(MMAPIConstants.JSON_KEY_LOCALITY, locality);
			userInfo.put(MMAPIConstants.JSON_KEY_LONGITUDE, longitude);
			userInfo.put(MMAPIConstants.JSON_KEY_NAME, name);
			//userInfo.put(MMAPIConstants.JSON_KEY_PHONENUMBER, phoneNumber);
			userInfo.put(MMAPIConstants.JSON_KEY_POSTCODE, postCode);
			userInfo.put(MMAPIConstants.JSON_KEY_PROVIDER_ID, "e048acf0-9e61-4794-b901-6a4bb49c3181"); //TODO: Provider ID is hard coded, change in future
			userInfo.put(MMAPIConstants.JSON_KEY_REGION, region);
			//userInfo.put(MMAPIConstants.JSON_KEY_WEBSITE, website);
			
			Log.d(TAG, TAG + "userInfo: " + userInfo.toString());
			
			HttpPut httpPut = new HttpPut(addLocationURL);
			StringEntity stringEntity = new StringEntity(userInfo.toString());
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
