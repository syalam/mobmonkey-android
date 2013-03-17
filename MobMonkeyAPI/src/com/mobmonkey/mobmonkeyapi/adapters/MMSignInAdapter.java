package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpPost;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.*;

/**
 * Final adapter class that handles all the sign in functionalities of MobMonkey Android
 * @author Dezapp, LLC
 *
 */
public final class MMSignInAdapter {
	private final static String TAG = "MMSignIn: ";
	private static String signInURL;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMSignInAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * Function that signs user in to MobMonkey with normal email and password
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign in url
	 * @param emailAddress The email address of the user
	 * @param password The password that is associated with the email address of the user
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signInUser(MMCallback mmCallback, String emailAddress, String password, String partnerId) {
//		signInURL = MMAPIConstants.MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
//				MMDeviceUUID.getDeviceUUID().toString() + "&useOAuth=false";
		signInURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
				MMDeviceUUID.getDeviceUUID().toString() + "&useOAuth=false";
		
		Log.d(TAG, TAG + "signInURL: " + signInURL);
		
		HttpPost httpPost = new HttpPost(signInURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		// TODO: encrypt password??
		httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * Function that signs user in to MobMonkey with Facebook credentials
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign in url
	 * @param oauthToken Facebook OAuth access token
	 * @param providerUserName Facebook provider username
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signInUserFacebook(MMCallback mmCallback, String oauthToken, String providerUserName, String partnerId) {
		signInURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + 
				"&deviceId=" + MMDeviceUUID.getDeviceUUID().toString() + 
				"&useOAuth=true&provider=" + MMAPIConstants.OAUTH_PROVIDER_FACEBOOK + 
				"&oauthToken=" + oauthToken + 
				"&providerUserName=" + providerUserName;
	
		HttpPost httpPost = new HttpPost(signInURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * Function that signs user in to MobMonkey with Twitter credentials
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign in url
	 * @param oauthToken Twitter OAuth access token
	 * @param providerUserName Twitter provider username
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signInUserTwitter(MMCallback mmCallback, String oauthToken, String providerUserName, String partnerId) {
		signInURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + 
				"&deviceId=" + MMDeviceUUID.getDeviceUUID().toString() + 
				"&useOAuth=true&provider=" + MMAPIConstants.OAUTH_PROVIDER_TWITTER + 
				"&oauthToken=" + oauthToken + 
				"&providerUserName=" + providerUserName;
		
		Log.d(TAG, TAG + "signInURL: " + signInURL);
		
		HttpPost httpPost = new HttpPost(signInURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_TWITTER);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
}
