package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

public class MMCategoryAdapter {
	private static final String TAG = "MMCategories: ";
	
	public static void getTopLevelCategories(MMCallback mmCallback, String user, String auth, String partnerId) {
		getCategories(mmCallback, "1", user, auth, partnerId);
	}
	
	public static void getCategories(MMCallback mmCallback, String categoryId, String user, String auth, String partnerId) {		
		Builder uriBuilder = Uri.parse(MMAPIConstants.MOBMONKEY_URL).buildUpon();
		uriBuilder.appendPath(MMAPIConstants.URI_PATH_CATEGORY);
		
		Log.d(TAG, TAG + "categoryURL: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, user);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, auth);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	public static void getAllCategories(MMCallback mmCallback, String user, String auth, String partnerId) {
		getCategories(mmCallback, MMAPIConstants.DEFAULT_STRING, user, auth, partnerId);
	}
}
