package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
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
		signUpURL = MMAPIConstants.MOBMONKEY_URL + "user";
		
		Builder uri = Uri.parse(signUpURL).buildUpon();
		uri.appendQueryParameter(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
		
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
			
//			userInfo.put(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
//			userInfo.put(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString());
			
			Log.d(TAG, TAG + "userInfo: " + userInfo.toString());
			Log.d(TAG, TAG + "uri: " + uri.toString());
			
			HttpPut httpPut = new HttpPut(uri.toString());
			StringEntity stringEntity = new StringEntity(userInfo.toString());
			httpPut.setEntity(stringEntity);
			httpPut.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPut.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			
			new MMPutAsyncTask(mmCallback).execute(httpPut);
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
//		signUpURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signin?deviceType=" + MMAPIConstants.DEVICE_TYPE + 
//				"&deviceId=" + MMDeviceUUID.getDeviceUUID().toString() + 
//				"&useOAuth=true&provider=" + MMAPIConstants.OAUTH_PROVIDER_FACEBOOK + 
//				"&oauthToken=" + oauthToken + 
//				"&providerUserName=" + providerUserName;
		
		signUpURL = MMAPIConstants.MOBMONKEY_URL + "signin";
		
		Builder uriBuilder = Uri.parse(signUpURL).buildUpon();
		uriBuilder.appendQueryParameter(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE)
			.appendQueryParameter(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMAPIConstants.KEY_USE_OAUTH, Boolean.toString(true))
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK)
			.appendQueryParameter(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken)
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER_USERNAME, providerUserName);
		
//		Log.d(TAG, TAG + "signUpURL: " + signUpURL);
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
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
//		signUpURL = MMAPIConstants.TEST_MOBMONKEY_URL + "signin/registeremail?deviceType=" + MMAPIConstants.DEVICE_TYPE +
//				"&deviceId=" + MMDeviceUUID.getDeviceUUID().toString() + 
//				"&oauthToken=" + oauthToken + 
//				"&providerUserName=" + providerUserName + 
//				"&eMailAddress=" + emailAddress +
//				"&provider=" + MMAPIConstants.OAUTH_PROVIDER_TWITTER;
		
		signUpURL = MMAPIConstants.MOBMONKEY_URL + "signin/registeremail";
		
		Builder uriBuilder = Uri.parse(signUpURL).buildUpon();
		uriBuilder.appendQueryParameter(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE)
			.appendQueryParameter(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_TWITTER)
			.appendQueryParameter(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken)
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER_USERNAME, providerUserName)
			.appendQueryParameter(MMAPIConstants.KEY_EMAIL_ADDRESS, emailAddress)
			.appendQueryParameter(MMAPIConstants.KEY_FIRST_NAME, firstName)
			.appendQueryParameter(MMAPIConstants.KEY_LAST_NAME, lastName)
			.appendQueryParameter(MMAPIConstants.KEY_GENDER, Integer.toString(gender))
			.appendQueryParameter(MMAPIConstants.KEY_BIRTHDATE, birthdate);
		
//		Log.d(TAG, TAG + "signUpURL: " + signUpURL);
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	public static void getUserInfo(MMCallback mmCallback, String partnerId, String emailAddress, String password)
	{
		signUpURL = MMAPIConstants.MOBMONKEY_URL + "user";
		
		HttpGet httpGet = new HttpGet(signUpURL);
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
}
