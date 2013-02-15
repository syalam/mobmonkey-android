package com.mobmonkey.mobmonkeyapi.adapters;

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
	
	public static void signOut(MMCallback mmCallback, String partnerId) {
		signOutURL = MMAPIConstants.MOBMONKEY_URL + "signout/type/deviceid?type=" + MMAPIConstants.DEVICE_TYPE + "&deviceid=" + MMDeviceUUID.getDeviceUUID();
		
		Log.d(TAG, TAG + "signOutURL: " + signOutURL);
		
		HttpPost httpPost = new HttpPost(signOutURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_USER, "duds411@gmail.com");
		httpPost.setHeader(MMAPIConstants.KEY_AUTH, "helloworld123");
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
}
