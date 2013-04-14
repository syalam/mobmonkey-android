package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
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
	
	public static void checkIn(MMCallback mmCallback,
								// headers
							   String partnerId,
							   String emailAddress,
							   String password,
							   // body
							   double latitude,
							   double longitude) {
		
		createUriBuilderInstance(MMAPIConstants.URI_PATH_CHECKIN);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());

		try {
			params.put(MMAPIConstants.KEY_LATITUDE, latitude);
			params.put(MMAPIConstants.KEY_LONGITUDE, longitude);
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			// add header
			httpPost.setEntity(new StringEntity(params.toString()));
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
}
