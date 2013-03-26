package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.*;

/**
 * Final adapter class that handles all the sign up functionalities of MobMonkey Android
 * @author Dezapp, LLC
 *
 */
public final class MMSignUpAdapter {
	private final static String TAG = "MMSignUp: ";
	private static String signUpURL;
	private static JSONObject userInfo;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMSignUpAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * Function that signs user up normally to MobMonkey with entered information
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign up url
	 * @param firstName User first name
	 * @param lastName User last name
	 * @param emailAddress User email address
	 * @param password User password
	 * @param birthdate User birth date
	 * @param gender User gender
	 * @param checkedToS User accepted Term of Use flag
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signUpNewUser(MMCallback mmCallback, String firstName, String lastName, String emailAddress, String password, String birthdate, int gender, boolean checkedToS, String partnerId) {
		signUpURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signup/user";
		try {
			userInfo = new JSONObject();
			userInfo.put(MMAPIConstants.KEY_FIRST_NAME, firstName);
			userInfo.put(MMAPIConstants.KEY_LAST_NAME, lastName);
			userInfo.put(MMAPIConstants.KEY_EMAIL_ADDRESS, emailAddress);
			userInfo.put(MMAPIConstants.KEY_PASSWORD, password);
			userInfo.put(MMAPIConstants.KEY_BIRTHDATE, birthdate);
			userInfo.put(MMAPIConstants.KEY_GENDER, gender);
			userInfo.put(MMAPIConstants.KEY_ACCEPTEDTOS, checkedToS);
			
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
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that signs user in to MobMonkey with Facebook credentials
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign up url
	 * @param oauthToken Facebook OAuth access token
	 * @param providerUserName Facebook provider username
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signUpNewUserFacebook(MMCallback mmCallback, String oauthToken, String providerUserName, String partnerId) {
		signUpURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + 
				"&deviceId=" + MMDeviceUUID.getDeviceUUID().toString() + 
				"&useOAuth=true&provider=" + MMAPIConstants.OAUTH_PROVIDER_FACEBOOK + 
				"&oauthToken=" + oauthToken + 
				"&providerUserName=" + providerUserName;
		
		Log.d(TAG, TAG + "signUpURL: " + signUpURL);
		
		HttpPost httpPost = new HttpPost(signUpURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * Function that signs user up to MobMonkey with Twitter credentials and user entered info
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign up url
	 * @param firstName User first name
	 * @param lastName User last name
	 * @param oauthToken Twitter OAuth access token
	 * @param providerUserName Twitter provider username
	 * @param emailAddress User email address
	 * @param birthdate User birth date
	 * @param gender User gender
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signUpNewUserTwitter(MMCallback mmCallback, String firstName, String lastName, String oauthToken, String providerUserName, String emailAddress, String birthdate, int gender, String partnerId) {
		signUpURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signin/registeremail?deviceType=" + MMAPIConstants.DEVICE_TYPE +
				"&deviceId=" + MMDeviceUUID.getDeviceUUID().toString() + 
				"&oauthToken=" + oauthToken + 
				"&providerUserName=" + providerUserName + 
				"&eMailAddress=" + emailAddress +
				"&provider=" + MMAPIConstants.OAUTH_PROVIDER_TWITTER;
		
		Log.d(TAG, TAG + "signUpURL: " + signUpURL);
		
		HttpPost httpPost = new HttpPost(signUpURL);
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, (String) oauthToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_TWITTER);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
}
