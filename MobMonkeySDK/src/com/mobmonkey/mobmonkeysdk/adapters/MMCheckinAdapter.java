package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMPostAsyncTask;

public class MMCheckinAdapter extends MMAdapter {
	private static String TAG = "MMCheckinAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMCheckinAdapter() {
		throw new AssertionError();
	}
	
	public static void checkInUser(MMCallback mmCallback,
							   double latitude, 
							   double longitude, 
							   String emailAddress, 
							   String password,
							   String partnerId) {
		
		createUriBuilderInstance(MMSDKConstants.URI_PATH_CHECKIN);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		try {
			params.put(MMSDKConstants.KEY_LATITUDE, latitude);
			params.put(MMSDKConstants.KEY_LONGITUDE, longitude);
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			// add header
			httpPost.setEntity(new StringEntity(params.toString()));
			httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMSDKConstants.KEY_USER, emailAddress);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, password);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
}
