package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

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
		try {
			userInfo = new JSONObject();
			userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
			userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMGetDeviceUUID.getDeviceUUID().toString());
			
			HttpPost httpPost = new HttpPost(signInURL);
			StringEntity stringEntity = new StringEntity(userInfo.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			// TODO: encrypt password
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
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
	public static void signInUserFacebook() {
		
	}
	
	/**
	 * 
	 */
	public static void signInUserTwitter() {
		
	}
}
