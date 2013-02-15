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
	private static String signInURL;
	private static JSONObject userInfo;
	
	/**
	 * 
	 * @param mmCallback
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void signInUser(MMCallback mmCallback, String emailAddress, String password, String partnerId) {
		signInURL = MMAPIConstants.MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
				MMDeviceUUID.getDeviceUUID().toString() + "&useOAuth=false";
		
		Log.d(TAG, TAG + "signInURL: " + signInURL);
		
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
	public static void signInUserFacebook(MMCallback mmCallback, String oauthToken, String emailAddress, String partnerId) {
		Log.d(TAG, TAG + "providerUserName: " + emailAddress);
		Log.d(TAG, TAG + "access token: " + oauthToken);
		Log.d(TAG, TAG + "deviceId: " + MMDeviceUUID.getDeviceUUID().toString());
		signInURL = MMAPIConstants.MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
				MMDeviceUUID.getDeviceUUID().toString() + "&useOAuth=true&provider=" + MMAPIConstants.OAUTH_PROVIDER_FACEBOOK + 
				"&oauthToken=" + oauthToken + "&providerUserName=" + emailAddress;
//		userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
//		userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMGetDeviceUUID.getDeviceUUID().toString());
	
		HttpPost httpPost = new HttpPost(signInURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, emailAddress);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK);
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * 
	 */
	public static void signInUserTwitter(MMCallback mmCallback, String oauthToken, String providerUserName, String partnerId) {
		signInURL = MMAPIConstants.MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
				MMDeviceUUID.getDeviceUUID().toString() + "&useOAuth=true&provider=" + MMAPIConstants.OAUTH_PROVIDER_TWITTER + 
				"&oauthToken=" + oauthToken + "&providerUserName=" + providerUserName;
		
		Log.d(TAG, TAG + "signInURL: " + signInURL);
		
		HttpPost httpPost = new HttpPost(signInURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_TWITTER);
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
}
