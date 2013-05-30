package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

public class MMCategoryAdapter extends MMAdapter {
	private static final String TAG = "MMCategories: ";
	
	private static MMGetAsyncTask mmGetAsyncTask;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMCategoryAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param categoryId
	 * @param user
	 * @param auth
	 * @param partnerId
	 */
	public static void getCategories(MMCallback mmCallback) {		
		
		createUriBuilderInstance(MMSDKConstants.URI_PATH_CATEGORY);
		
		Log.d(TAG, TAG + "categoryURL: " + uriBuilder.toString());
		
		HttpGet httpGet = newHttpGetInstance();
		
		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(httpGet);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param user
	 * @param auth
	 * @param partnerId
	 */
	public static void getAllCategories(MMCallback mmCallback) {
		getCategories(mmCallback);
	}
	
	/**
	 * 
	 */
	public static void cancelGetAllCategories() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}
