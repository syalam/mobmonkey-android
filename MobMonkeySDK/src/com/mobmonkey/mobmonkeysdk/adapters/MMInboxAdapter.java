package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMGetAsyncTask;

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
	 * Get all open requests that have been fulfilled or waiting to be fulfilled.
	 * @param {@link MMCallback} 
	 * @param partnerId
	 * @param Email address
	 * @param Password
	 */
	public static void getOpenRequests(MMCallback mmCallback,
									   String partnerId,
									   String emailAddress,
									   String password) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_OPENREQUESTS);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	/**
	 * Get all answered requests that have been fulfilled or waiting to be fulfilled.
	 * @param {@link MMCallback} 
	 * @param partnerId
	 * @param Email address
	 * @param Password
	 */
	public static void getAnsweredRequests(MMCallback mmCallback,
									   String partnerId,
									   String emailAddress,
									   String password) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_ANSWEREDREQUESTS);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	/**
	 * Get all requests that have been assigned to you from the checkin API.
	 * @param {@link MMCallback} 
	 * @param partnerId
	 * @param Email address
	 * @param Password
	 */
	public static void getAssignedRequests(MMCallback mmCallback,
										   String partnerId,
										   String emailAddress,
										   String password) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_ASSIGNEDREQUESTS);
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	public static void getCounts(MMCallback mmCallback,
								 String partnerId,
								 String emailAddress,
								 String password) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_COUNTS);
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
}
