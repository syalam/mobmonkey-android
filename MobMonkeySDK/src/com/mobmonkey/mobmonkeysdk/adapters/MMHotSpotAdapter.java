package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

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
	
	private MMHotSpotAdapter() {
		throw new AssertionError();
	}
	
	public static void createSubLocationWithLocationInfo(MMCallback mmCallback,
														 String name,
														 String description,
														 String range,
														 String locationId,
														 String latitude,
														 String longitude,
														 String categoryIds,
														 String countryCode,
														 String locality,
														 String phoneNumber,
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
			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, "e048acf0-9e91-4794-b901-6a4bb49c3181");
			params.put(MMSDKConstants.JSON_KEY_REGION, region);
			params.put(MMSDKConstants.JSON_KEY_WEBSITE, webSite);
			params.put(MMSDKConstants.JSON_KEY_NAME, name);
			params.put(MMSDKConstants.JSON_KEY_PARENT_LOCATION_ID, parentLocationId);
			params.put(MMSDKConstants.JSON_KEY_PARENT_PROVIDER_ID, parentProviderId);
			
			Log.d(TAG, TAG + "params size: " + params.names().length());
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPut httpPut = new HttpPut(uriBuilder.toString());
			Log.d(TAG, TAG + "uri: " + httpPut.getURI().toString());
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
