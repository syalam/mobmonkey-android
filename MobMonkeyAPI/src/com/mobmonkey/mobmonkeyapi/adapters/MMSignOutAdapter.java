package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMDeviceUUID;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMSignOutAdapter {
	private final static String TAG = "MMSignOutAdapter: ";
	private static String signOutURL;
	
	private MMSignOutAdapter() {
		throw new AssertionError();
	}
	
	public static void signOut(MMCallback mmCallback, String user, String auth, String partnerId) {
		signOutURL = MMAPIConstants.MOBMONKEY_URL + "signout/Android/" + MMDeviceUUID.getDeviceUUID().toString();
		Log.d(TAG, TAG + "signOutURL: " + signOutURL);
		
		HttpPost httpPost = new HttpPost(signOutURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_USER, user);
		httpPost.setHeader(MMAPIConstants.KEY_AUTH, auth);
		
		for(Header h : httpPost.getAllHeaders()) {
			Log.d(TAG, TAG + "name: " + h.getName() + " value: " + h.getValue());
		}
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
}
