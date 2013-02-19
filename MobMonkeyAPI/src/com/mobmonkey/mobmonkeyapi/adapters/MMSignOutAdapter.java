package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMDeviceUUID;

/**
 * Final adapter class that handles all the sign out functionalities of MobMonkey Android
 * @author Dezapp, LLC
 *
 */
public final class MMSignOutAdapter {
	private final static String TAG = "MMSignOutAdapter: ";
	private static String signOutURL;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMSignOutAdapter() {
		throw new AssertionError();
	}
	
	/**
 	 * Function that signs out user from MobMonkey server
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign out url
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signOut(MMCallback mmCallback, String user, String auth, String partnerId) {
		signOutURL = MMAPIConstants.MOBMONKEY_URL + "signout/" + MMAPIConstants.DEVICE_TYPE + "/" + MMDeviceUUID.getDeviceUUID().toString();
		Log.d(TAG, TAG + "signOutURL: " + signOutURL);
		
		HttpPost httpPost = new HttpPost(signOutURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_USER, user);
		httpPost.setHeader(MMAPIConstants.KEY_AUTH, auth);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, auth);
		
		for(Header header : httpPost.getAllHeaders()) {
			Log.d(TAG, TAG + "header name: " + header.getName());
			Log.d(TAG, TAG + "header value: " + header.getValue());
		}
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
}
