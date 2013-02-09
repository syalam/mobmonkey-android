package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMGetDeviceUUID;

/**
 * TODO: add desc of this singleton class
 * @author Dezapp, LLC
 *
 */
public final class MMSignUp {
	private final static String TAG = "MMSignUp: ";
	private static String signUpCall;
	private static JSONObject userInfo;
	
	/**
	 * Function that converts the user info from a {@link HashMap} to {@link JSONObject}, add it to the {@link HttpPost} and set the necessary headers before it is pushed to the server.
	 * @param mmCallback {@link MMCallback} to be sent back to the invoking Activity
	 * @param map {@link HashMap} of user info
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signUpNewUser(MMCallback mmCallback, HashMap<String,Object> map, String partnerId) {
		signUpCall = MMAPIConstants.URL + "signup/user";
		try {
			userInfo = new JSONObject(map);
			// TODO: remove hardcoded values
			userInfo.put(MMAPIConstants.KEY_CITY, "Tempe");
			userInfo.put(MMAPIConstants.KEY_STATE, "Arizona");
			userInfo.put(MMAPIConstants.KEY_ZIP, "85283");
			userInfo.put(MMAPIConstants.KEY_PHONE_NUMBER, "480-555-5555");
			// end TODO:
			userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
			userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMGetDeviceUUID.getDeviceUUID().toString());
			
			Log.d(TAG, TAG + "userInfo: " + userInfo.toString());
			
			HttpPost httpPost = new HttpPost(signUpCall);
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
	public static void signUpNewUserFacebook() {
		// TODO:
	}
	
	/**
	 * 
	 */
	public static void signUpNewUserTwitter() {
		// TODO:
	}
}
