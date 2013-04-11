package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_INBOX, MMAPIConstants.URI_PATH_OPENREQUESTS);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_INBOX, MMAPIConstants.URI_PATH_OPENREQUESTS);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_INBOX, MMAPIConstants.URI_PATH_ASSIGNEDREQUESTS);
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
}
