package com.mobmonkey.mobmonkeyapi.servercalls;

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
 * @author Dezapp, LLC
 *
 */
public final class MMSignUp {
	private final static String TAG = "MMSignUp: ";
	private static String signUpCall;
	private static JSONObject userInfo;
	
	public static void signUpNewUser(MMCallback mmCallback, HashMap<String,Object> map, String partnerId) {
		signUpCall = MMAPIConstants.URL + "signup/user";
		try {
			// TODO: transform map to json
//			userInfo = new JSONObject("{ \"firstName\":\"Wilson\", \"lastName\":\"Xie\", \"gender\":1, \"password\":\"trustme123\", \"eMailAddress\":\"wilsonxie23@gmail.com\", \"phoneNumber\":\"480-555-5555\", \"birthday\":1346291313438, \"city\":\"Phoenix\", \"state\":\"AZ\", \"zip\":90210, \"acceptedtos\":true, \"deviceId\":\"" + deviceUUID.toString() + "\", \"deviceType\":\"Android\" }");
			// TODO: 
			userInfo = new JSONObject(map);
			userInfo.put(MMAPIConstants.KEY_CITY, "Tempe");
			userInfo.put(MMAPIConstants.KEY_STATE, "Arizona");
			userInfo.put(MMAPIConstants.KEY_ZIP, "85283");
			userInfo.put(MMAPIConstants.KEY_PHONE_NUMBER, "480-555-5555");
			userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
			userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMGetDeviceUUID.getDeviceUUID().toString());
			Log.d(TAG, TAG + "userInfo: " + userInfo.toString());
			HttpPost httpPost = new HttpPost(signUpCall);
			StringEntity stringEntity = new StringEntity(userInfo.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE,"application/json");
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			
			new MMAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
