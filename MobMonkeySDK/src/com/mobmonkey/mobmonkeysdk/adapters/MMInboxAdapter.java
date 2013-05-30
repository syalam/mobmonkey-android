package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

/**
 * @author Dezapp, LLC
 * The adapter for pulling information of Inbox from the server 
 */
public class MMInboxAdapter extends MMAdapter {
	private static String TAG = "MMInboxAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMInboxAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param partnerId
	 * @param emailAddress
	 * @param auth
	 */
	public static void getCounts(MMCallback mmCallback) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_COUNTS);
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = newHttpGetInstance();
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
}
