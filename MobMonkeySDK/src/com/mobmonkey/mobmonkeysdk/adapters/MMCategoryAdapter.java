package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMGetAsyncTask;

public class MMCategoryAdapter extends MMAdapter {
	private static final String TAG = "MMCategories";
	
	private static MMGetAsyncTask mmGetAsyncTask;
	
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
		
		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(httpGet);
	}
	
	public static void getAllCategories(MMCallback mmCallback, String user, String auth, String partnerId) {
		getCategories(mmCallback, MMAPIConstants.DEFAULT_STRING_EMPTY, user, auth, partnerId);
	}
	
	public static void cancelGetAllCategories() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}