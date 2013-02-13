package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.*;

/**
 * TODO: add desc of this class
 * @author Dezapp, LLC
 *
 */
public final class MMSignUpAdapter {
	private final static String TAG = "MMSignUp: ";
	private static String signUpURL;
	private static JSONObject userInfo;
	
	/**
	 * Function that converts the user info from a {@link HashMap} to {@link JSONObject}, add it to the {@link HttpPost} and set the necessary headers before it is pushed to the server.
	 * @param mmCallback {@link MMCallback} to be sent back to the invoking Activity
	 * @param map {@link HashMap} of user info
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signUpNewUser(MMCallback mmCallback, HashMap<String,Object> map, String partnerId) {
		signUpURL = MMAPIConstants.URL + "signup/user";
		try {
			userInfo = new JSONObject(map);
			// TODO: remove hardcoded values
			userInfo.put(MMAPIConstants.KEY_CITY, "Tempe");
			userInfo.put(MMAPIConstants.KEY_STATE, "Arizona");
			userInfo.put(MMAPIConstants.KEY_ZIP, "85283");
			userInfo.put(MMAPIConstants.KEY_PHONE_NUMBER, "480-555-5555");
			// end TODO:
			userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
			userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString());
			
			Log.d(TAG, TAG + "userInfo: " + userInfo.toString());
			
			HttpPost httpPost = new HttpPost(signUpURL);
			StringEntity stringEntity = new StringEntity(userInfo.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			
			new MMAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public static void signUpNewUserFacebook(MMCallback mmCallback, String accessToken, String emailAddress, String partnerId) {
		signUpURL = MMAPIConstants.URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + "&deviceId=" + 
					MMDeviceUUID.getDeviceUUID().toString() + "&useOAuth=true&provider=" + "facebook" + 
					"&oauthToken=" + accessToken + "&providerUserName=" + emailAddress;
//			userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
//			userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMGetDeviceUUID.getDeviceUUID().toString());
		
		HttpPost httpPost = new HttpPost(signUpURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, emailAddress);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, accessToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK);
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * 
	 */
	public static void signUpNewUserTwitter(MMCallback mmCallback, HashMap<String,Object> map, String partnerId) {
		signUpURL = MMAPIConstants.URL + "signin/registeremail?deviceType=" + MMAPIConstants.DEVICE_TYPE +
				"&deviceId=" + MMDeviceUUID.getDeviceUUID().toString() + "&oauthToken=" + (String) map.get(MMAPIConstants.KEY_OAUTH_TOKEN) + 
				"&providerUserName=" + (String) map.get(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME) + "&eMailAddress=" + (String) map.get(MMAPIConstants.KEY_EMAIL_ADDRESS) +
				"&provide=" + MMAPIConstants.OAUTH_PROVIDER_TWITTER;
		
		HttpPost httpPost = new HttpPost(signUpURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, (String) map.get(MMAPIConstants.KEY_EMAIL_ADDRESS));
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, (String) map.get(MMAPIConstants.KEY_OAUTH_TOKEN));
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_TWITTER);
		
		new MMAsyncTask(mmCallback).execute(httpPost);
	}
}
