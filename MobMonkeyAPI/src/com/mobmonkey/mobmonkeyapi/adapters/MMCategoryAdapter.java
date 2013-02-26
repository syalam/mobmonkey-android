package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

public class MMCategoryAdapter {
	private static final String TAG = "MMCategories: ";
	private static String categoryURL;
	
	public static void getTopLevelCategories(MMCallback mmCallback, String user, String auth, String partnerId) {
		getCategories(mmCallback, "1", user, auth, partnerId);
	}
	
	public static void getCategories(MMCallback mmCallback, String categoryId, String user, String auth, String partnerId) {
		categoryURL = MMAPIConstants.MOBMONKEY_URL + "category?categoryId=" + categoryId;
		
		Log.d(TAG, TAG + "categoryURL: " + categoryURL);
		
		HttpGet httpGet = new HttpGet(categoryURL);
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, user);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, auth);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	public static void getAllCategories(MMCallback mmCallback, String user, String auth, String partnerId)
	{
		categoryURL = MMAPIConstants.MOBMONKEY_URL + "category/all";
		
		Log.d(TAG, TAG + "AllCategoriesURL: " + categoryURL);

		HttpGet httpGet = new HttpGet(categoryURL);
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, user);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, auth);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
}
