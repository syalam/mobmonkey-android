package com.mobmonkey.mobmonkeyapi.servercalls;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class MMSignUp {
	private final static String TAG = "MMSignUp: ";
	private static String signUpCall;
	private static JSONObject userInfo;
	
	public static void signUpNewUser(MMCallback mmCallback, UUID deviceUUID) {
		signUpCall = MMConstants.URL + "signup/user";
		try {
			userInfo = new JSONObject("{ \"firstName\":\"Wilson\", \"lastName\":\"Xie\", \"gender\":1, \"password\":\"trustme123\", \"eMailAddress\":\"wilson@dezapp.com\", \"phoneNumber\":\"480-555-5555\", \"birthday\":1346291313438, \"city\":\"Phoenix\", \"state\":\"AZ\", \"zip\":90210, \"acceptedtos\":true, \"deviceId\":\"" + deviceUUID.toString() + "\", \"deviceType\":\"Android\" }");
			HttpPost httpPost = new HttpPost(signUpCall);
			StringEntity stringEntity = new StringEntity(userInfo.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader("Content-Type","application/json");
			httpPost.setHeader("MobMonkey-partnerId", "00000000-0000-0000-0000-000000000000");
			
			new MMAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		

	}
	
}
