package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.net.Uri;
import android.net.Uri.Builder;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

/**
 * @author Dezapp, LLC
 * The adapter for pulling information of Inbox from the server 
 */
public class MMInboxAdapter {
	private static String TAG = "MMInboxAdapter";
	private static String inboxURL;
	
	/**
	 * Get all open requests that have been fulfilled or waiting to be fulfilled.
	 * @param {@link MMCallback} 
	 * @param partnerId
	 * @param Email address
	 * @param Password
	 */
	public static void getOpenRequests(MMCallback mmCallback,
									   // headers
									   String partnerId,
									   String emailAddress,
									   String password) {
		inboxURL = MMAPIConstants.TEST_MOBMONKEY_URL + "inbox/openrequests";
		
		
		HttpGet httpget = new HttpGet(inboxURL);
		// add header
		httpget.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpget.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpget.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpget.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpget);
	}
	
	/**
	 * Get all requests that have been assigned to you from the checkin API.
	 * @param {@link MMCallback} 
	 * @param partnerId
	 * @param Email address
	 * @param Password
	 */
	public static void getAssignedRequests(MMCallback mmCallback,
										   // headers
										   String partnerId,
										   String emailAddress,
										   String password) {
		inboxURL = MMAPIConstants.TEST_MOBMONKEY_URL + "inbox/assignedrequests";
		
		
		HttpGet httpget = new HttpGet(inboxURL);
		// add header
		httpget.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpget.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpget.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpget.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpget);
	}
}
