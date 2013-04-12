package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMDeviceUUID;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMPutAsyncTask;

/**
 * Final adapter class that handles all the user functionalities of MobMonkey Android
 * @author Dezapp, LLC
 *
 */
public class MMUserAdapter extends MMAdapter {
	private final static String TAG = "MMUserAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMUserAdapter() {
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_SIGNIN);
		uriBuilder.appendQueryParameter(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE)
			.appendQueryParameter(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMAPIConstants.KEY_USE_OAUTH, Boolean.toString(false));
		
		Log.d(TAG, TAG + "signin uri: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_SIGNIN);
		uriBuilder.appendQueryParameter(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE)
			.appendQueryParameter(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMAPIConstants.KEY_USE_OAUTH, Boolean.toString(true))
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_FACEBOOK)
			.appendQueryParameter(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken)
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER_USERNAME, providerUserName);
		
		Log.d(TAG, TAG + "signin uri: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_SIGNIN);
		uriBuilder.appendQueryParameter(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE)
			.appendQueryParameter(MMAPIConstants.KEY_USE_OAUTH, Boolean.toString(true))
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_TWITTER)
			.appendQueryParameter(MMAPIConstants.KEY_PROVIDER_USERNAME, providerUserName);
		
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.OAUTH_PROVIDER_TWITTER);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_USER);
		uriBuilder.appendQueryParameter(MMAPIConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMAPIConstants.KEY_DEVICE_TYPE, MMAPIConstants.DEVICE_TYPE);
		createParamsInstance();
		
		try {
			params.put(MMAPIConstants.KEY_FIRST_NAME, firstName);
			params.put(MMAPIConstants.KEY_LAST_NAME, lastName);
			params.put(MMAPIConstants.KEY_EMAIL_ADDRESS, emailAddress);
			params.put(MMAPIConstants.KEY_PASSWORD, password);
			params.put(MMAPIConstants.KEY_BIRTHDATE, birthdate);
			params.put(MMAPIConstants.KEY_GENDER, gender);
			params.put(MMAPIConstants.KEY_ACCEPTEDTOS, checkedToS);
			
			// TODO: remove hardcoded values
			params.put(MMAPIConstants.KEY_CITY, "Tempe");
			params.put(MMAPIConstants.KEY_STATE, "Arizona");
			params.put(MMAPIConstants.KEY_ZIP, "85283");
			params.put(MMAPIConstants.KEY_PHONE_NUMBER, "480-555-5555");
			// end TODO:
			
			Log.d(TAG, TAG + "userInfo: " + params.toString());
			Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
			
			HttpPut httpPut = new HttpPut(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_SIGNIN);
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_SIGNIN, MMAPIConstants.URI_PATH_REGISTEREMAIL);
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
	
	/**
 	 * Function that signs out user from MobMonkey server
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign out url
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signOut(MMCallback mmCallback, String user, String auth, String partnerId) {
		Builder uriBuilder = Uri.parse(MMAPIConstants.MOBMONKEY_URL).buildUpon();
		uriBuilder.appendPath(MMAPIConstants.URI_PATH_SIGNOUT)
			.appendPath(MMAPIConstants.DEVICE_TYPE)
			.appendPath(MMDeviceUUID.getDeviceUUID().toString());

		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMAPIConstants.KEY_USER, user);
		httpPost.setHeader(MMAPIConstants.KEY_AUTH, auth);
		httpPost.setHeader(MMAPIConstants.KEY_OAUTH_TOKEN, auth);
		
		for(Header header : httpPost.getAllHeaders()) {
			Log.d(TAG, TAG + "header name: " + header.getName());
			Log.d(TAG, TAG + "header value: " + header.getValue());
		}
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param partnerId
	 * @param emailAddress
	 * @param password
	 */
	public static void getUserInfo(MMCallback mmCallback, String partnerId, String emailAddress, String password) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_USER);
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 * @param newPassword
	 * @param firstName
	 * @param lastName
	 * @param birthday
	 * @param gender
	 * @param city
	 * @param state
	 * @param zip
	 * @param acceptedtos
	 */
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
		createUriBuilderInstance(MMAPIConstants.URI_PATH_USER);
		createParamsInstance();
		
		try {
			if(!newPassword.equals(MMAPIConstants.DEFAULT_STRING)) {
				params.put(MMAPIConstants.KEY_PASSWORD, newPassword);
			}
			
			params.put(MMAPIConstants.KEY_FIRST_NAME, firstName);
			params.put(MMAPIConstants.KEY_LAST_NAME, lastName);
			params.put(MMAPIConstants.KEY_BIRTHDATE, birthday);
			params.put(MMAPIConstants.KEY_GENDER, gender);
			params.put(MMAPIConstants.KEY_CITY, city);
			params.put(MMAPIConstants.KEY_STATE, state);
			params.put(MMAPIConstants.KEY_ZIP, zip);
			params.put(MMAPIConstants.KEY_ACCEPTEDTOS, acceptedtos);
			
			Log.d(TAG, params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
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
