package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

public class MMCheckinAdapter extends MMAdapter {
	private static String TAG = "MMCheckinAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMCheckinAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param latitude
	 * @param longitude
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void checkInUser(MMCallback mmCallback) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_CHECKIN);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		try {
			params.put(MMSDKConstants.KEY_LATITUDE, MMLocationManager.getLocationLatitude());
			params.put(MMSDKConstants.KEY_LONGITUDE, MMLocationManager.getLocationLongitude());
			
			HttpPost httpPost = newHttpPostInstance();
			httpPost.setEntity(new StringEntity(params.toString()));
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
