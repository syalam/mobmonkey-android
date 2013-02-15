package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

public class MMSearchLocationAdapter {
	
	private static final String TAG = "TAG";

	public static void searchTextWithLocation(MMCallback mmCallback, String searchText, String latitude, String longitude, String email, String password, String partnerId)
	{
		String searchLocationURL = "http://api.mobmonkey.com/rest/search/location/glob";
		
		JSONObject params = new JSONObject();
		
		try {
			params.put(MMAPIConstants.KEY_LATITUDE, latitude);
			params.put(MMAPIConstants.KEY_LONGITUDE, longitude);
			params.put(MMAPIConstants.KEY_RADIUS_IN_YARDS, 25);
			//params.put(MMAPIConstants.KEY_NAME, searchText);
			//params.put(MMAPIConstants.KEY_CATEGORY_IDS, "342");
			
			Log.d(TAG, TAG + "PARAMS: " + params);
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			HttpPost httpPost = new HttpPost(searchLocationURL);
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, email);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
			new MMAsyncTask(mmCallback).execute(httpPost);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
