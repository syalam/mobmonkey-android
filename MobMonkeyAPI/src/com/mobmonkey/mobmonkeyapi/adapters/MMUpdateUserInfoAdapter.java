package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMPutAsyncTask;

public class MMUpdateUserInfoAdapter {
	
	private static final String TAG = "MMUpdateUserInfoAdapter";
	private static String UpdateUserURL;
	private static JSONObject userInfo;
	
	public static void updateUserInfo(MMCallback mmCallback,
									  String emailAddress,
									  String password,
									  String partnerId,
									  String newPassword,
									  String firstName,
									  String lastName,
									  long birthday,
									  int gender,
									  String city,
									  String state,
									  String zip,
									  boolean acceptedtos) {
		UpdateUserURL = MMAPIConstants.TEST_MOBMONKEY_URL + "user";
		
		try {
			userInfo = new JSONObject();
			if(!newPassword.equals("")) {
				userInfo.put(MMAPIConstants.KEY_PASSWORD, newPassword);
			}
			userInfo.put(MMAPIConstants.KEY_FIRST_NAME, firstName);
			userInfo.put(MMAPIConstants.KEY_LAST_NAME, lastName);
			userInfo.put(MMAPIConstants.KEY_BIRTHDATE, birthday);
			userInfo.put(MMAPIConstants.KEY_GENDER, gender);
			userInfo.put(MMAPIConstants.KEY_CITY, city);
			userInfo.put(MMAPIConstants.KEY_STATE, state);
			userInfo.put(MMAPIConstants.KEY_ZIP, zip);
			userInfo.put(MMAPIConstants.KEY_ACCEPTEDTOS, acceptedtos);
			
			HttpPost httpPost = new HttpPost(UpdateUserURL);
			StringEntity stringEntity = new StringEntity(userInfo.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);

			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
