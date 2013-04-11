package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

public class MMCategoryAdapter extends MMAdapter {
	private static final String TAG = "MMCategories: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMCategoryAdapter() {
		throw new AssertionError();
	}
	
	public static void getTopLevelCategories(MMCallback mmCallback, String user, String auth, String partnerId) {
		getCategories(mmCallback, "1", user, auth, partnerId);
	}
	
	public static void getCategories(MMCallback mmCallback, String categoryId, String user, String auth, String partnerId) {		
		createUriBuilderInstance(MMAPIConstants.URI_PATH_CATEGORY);
		
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
