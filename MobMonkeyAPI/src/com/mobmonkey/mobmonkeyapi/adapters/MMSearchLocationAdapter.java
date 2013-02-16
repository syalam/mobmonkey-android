package com.mobmonkey.mobmonkeyapi.adapters;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

/**
 * @author Dezapp, LLC
 * 
 */
public class MMSearchLocationAdapter {
	
	private static final String TAG = "MMSearchLocationAdapter: ";
	
	private static String searchLocationURL;

	/**
	 * 
	 * @param mmCallback
	 * @param hashMap
	 */
	public static void searchTextWithLocation(MMCallback mmCallback, HashMap<String, Object> hashMap) {
		searchLocationURL = MMAPIConstants.MOBMONKEY_URL + "search/location";
		
		Log.d(TAG, TAG + "searchLocationURL: " + searchLocationURL);
		
		try {
			JSONObject params = new JSONObject();
			params.put(MMAPIConstants.KEY_LONGITUDE, (String) hashMap.get(MMAPIConstants.KEY_LONGITUDE));
			params.put(MMAPIConstants.KEY_LATITUDE, (String) hashMap.get(MMAPIConstants.KEY_LATITUDE));
			params.put(MMAPIConstants.KEY_RADIUS_IN_YARDS, 1000);
			params.put(MMAPIConstants.KEY_NAME, (String) hashMap.get(MMAPIConstants.KEY_NAME));
			
			Log.d(TAG, TAG + "PARAMS: " + params);
			
			StringEntity stringEntity = new StringEntity(params.toString());
			
			HttpPost httpPost = new HttpPost(searchLocationURL);
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, (String) hashMap.get(MMAPIConstants.KEY_CONTENT_TYPE));
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, (String) hashMap.get(MMAPIConstants.KEY_PARTNER_ID));
			httpPost.setHeader(MMAPIConstants.KEY_USER, (String) hashMap.get(MMAPIConstants.KEY_USER));
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, (String) hashMap.get(MMAPIConstants.KEY_AUTH));
			
			for(Header header : httpPost.getAllHeaders()) {
				Log.d(TAG, TAG + "name: " + header.getName() + " value: " + header.getValue());
			}
			
			new MMAsyncTask(mmCallback).execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param hashMap
	 */
	public static void searchAllNearby(MMCallback mmCallback, HashMap<String, Object> hashMap) {
		Log.d(TAG, TAG + "searchAllNearby");
		hashMap.put(MMAPIConstants.KEY_NAME, MMAPIConstants.DEFAULT_STRING);
		searchTextWithLocation(mmCallback, hashMap);
	}
}
