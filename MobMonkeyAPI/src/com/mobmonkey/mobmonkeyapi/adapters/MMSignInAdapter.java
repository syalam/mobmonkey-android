package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.*;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMSignInAdapter {
	private final static String TAG = "MMSignIn: ";
	private static String signInURL = MMAPIConstants.URL + "signin";
	private static JSONObject userInfo;
	
	/**
	 * 
	 * @param mmCallback
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void signInUser(MMCallback mmCallback, String emailAddress, String password, String partnerId) {
		signInURL = signInURL + "?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
				MMGetDeviceUUID.getDeviceUUID().toString() + "&useOAuth=false";
		
		HttpPost httpPost = new HttpPost(signInURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		// TODO: encrypt password
		httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * 
	 */
	public static void signInUserFacebook(MMCallback mmCallback, String accessToken, String emailAddress, String partnerId) {
		Log.d(TAG, TAG + "providerUserName: " + emailAddress);
		Log.d(TAG, TAG + "access token: " + accessToken);
		Log.d(TAG, TAG + "deviceId: " + MMGetDeviceUUID.getDeviceUUID().toString());
		signInURL = signInURL + "?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
				MMGetDeviceUUID.getDeviceUUID().toString() + "&useOAuth=true&provider=" + "facebook" + 
				"&oauthToken=" + accessToken + "&providerUserName=" + emailAddress;
//		userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
//		userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMGetDeviceUUID.getDeviceUUID().toString());
	
		HttpPost httpPost = new HttpPost(signInURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader("OauthProviderUserName", emailAddress);
		httpPost.setHeader("OauthToken", accessToken);
		httpPost.setHeader("OatuthProvider", "facebook");
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * 
	 */
	public static void signInUserTwitter() {
		
	}
}
