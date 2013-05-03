package com.mobmonkey.mobmonkeysdk.adapters;

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

import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPutAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDeviceUUID;

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
	 * @param partnerId MobMonkey unique partner id
	 * @param emailAddress The email address of the user
	 * @param password The password that is associated with the email address of the user
	 */
	public static void signInUser(MMCallback mmCallback,
			  					  String partnerId,
								  String user,
								  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SIGNIN);
		uriBuilder.appendQueryParameter(MMSDKConstants.KEY_DEVICE_TYPE, MMSDKConstants.DEVICE_TYPE)
			.appendQueryParameter(MMSDKConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMSDKConstants.KEY_USE_OAUTH, Boolean.toString(false));
		
		Log.d(TAG, TAG + "signin uri: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_USER, user);
		// TODO: encrypt password??
		httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * Function that signs user in to MobMonkey with Facebook credentials
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign in url
	 * @param oauthToken Facebook OAuth access token
	 * @param providerUserName Facebook provider username
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signInUserFacebook(MMCallback mmCallback,
										  String oauthToken,
										  String providerUserName,
										  String partnerId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SIGNIN);
		uriBuilder.appendQueryParameter(MMSDKConstants.KEY_DEVICE_TYPE, MMSDKConstants.DEVICE_TYPE)
			.appendQueryParameter(MMSDKConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMSDKConstants.KEY_USE_OAUTH, Boolean.toString(true))
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_FACEBOOK)
			.appendQueryParameter(MMSDKConstants.KEY_OAUTH_TOKEN, oauthToken)
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER_USERNAME, providerUserName);
		
		Log.d(TAG, TAG + "signin uri: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_FACEBOOK);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
	 * Function that signs user in to MobMonkey with Twitter credentials
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign in url
	 * @param oauthToken Twitter OAuth access token
	 * @param providerUserName Twitter provider username
	 * @param partnerId MobMonkey unique partner id
	 */
	public static void signInUserTwitter(MMCallback mmCallback,
										 String partnerId,
										 String providerUserName,
										 String oauthToken) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SIGNIN);
		uriBuilder.appendQueryParameter(MMSDKConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMSDKConstants.KEY_DEVICE_TYPE, MMSDKConstants.DEVICE_TYPE)
			.appendQueryParameter(MMSDKConstants.KEY_USE_OAUTH, Boolean.toString(true))
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_TWITTER)
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER_USERNAME, providerUserName);
		
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_TWITTER);
		
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
	public static void signUpNewUser(MMCallback mmCallback,
									 String firstName,
									 String lastName,
									 String emailAddress,
									 String password,
									 String birthdate,
									 int gender,
									 boolean checkedToS,
									 String partnerId) {		
		createUriBuilderInstance(MMSDKConstants.URI_PATH_USER);
		uriBuilder.appendQueryParameter(MMSDKConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMSDKConstants.KEY_DEVICE_TYPE, MMSDKConstants.DEVICE_TYPE);
		createParamsInstance();
		
		try {
			params.put(MMSDKConstants.KEY_FIRST_NAME, firstName);
			params.put(MMSDKConstants.KEY_LAST_NAME, lastName);
			params.put(MMSDKConstants.KEY_EMAIL_ADDRESS, emailAddress);
			params.put(MMSDKConstants.KEY_PASSWORD, password);
			params.put(MMSDKConstants.KEY_BIRTHDATE, birthdate);
			params.put(MMSDKConstants.KEY_GENDER, gender);
			params.put(MMSDKConstants.KEY_ACCEPTEDTOS, checkedToS);
			
			// TODO: remove hardcoded values
			params.put(MMSDKConstants.KEY_CITY, "Tempe");
			params.put(MMSDKConstants.KEY_STATE, "Arizona");
			params.put(MMSDKConstants.KEY_ZIP, "85283");
			params.put(MMSDKConstants.KEY_PHONE_NUMBER, "480-555-5555");
			// end TODO:
			
			Log.d(TAG, TAG + "userInfo: " + params.toString());
			Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
			
			HttpPut httpPut = new HttpPut(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPut.setEntity(stringEntity);
			httpPut.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPut.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			
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
	 * @param partnerId MobMonkey unique partner id
	 * @param oauthToken Facebook OAuth access token
	 * @param providerUserName Facebook provider username
	 */
	public static void signUpNewUserFacebook(MMCallback mmCallback,
											 String partnerId,
											 String providerUserName,
											 String oauthToken) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SIGNIN);
		uriBuilder.appendQueryParameter(MMSDKConstants.KEY_DEVICE_TYPE, MMSDKConstants.DEVICE_TYPE)
			.appendQueryParameter(MMSDKConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMSDKConstants.KEY_USE_OAUTH, Boolean.toString(true))
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_FACEBOOK)
			.appendQueryParameter(MMSDKConstants.KEY_OAUTH_TOKEN, oauthToken)
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER_USERNAME, providerUserName);
		
//		Log.d(TAG, TAG + "signUpURL: " + signUpURL);
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, providerUserName);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_TOKEN, oauthToken);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_FACEBOOK);
		
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
	public static void signUpNewUserTwitter(MMCallback mmCallback,
											String firstName,
											String lastName,
											String oauthToken,
											String providerUserName,
											String emailAddress,
											String birthdate,
											int gender,
											String partnerId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_SIGNIN, MMSDKConstants.URI_PATH_REGISTEREMAIL);
		uriBuilder.appendQueryParameter(MMSDKConstants.KEY_DEVICE_TYPE, MMSDKConstants.DEVICE_TYPE)
			.appendQueryParameter(MMSDKConstants.KEY_DEVICE_ID, MMDeviceUUID.getDeviceUUID().toString())
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER, MMSDKConstants.OAUTH_PROVIDER_TWITTER)
			.appendQueryParameter(MMSDKConstants.KEY_OAUTH_TOKEN, oauthToken)
			.appendQueryParameter(MMSDKConstants.KEY_PROVIDER_USERNAME, providerUserName)
			.appendQueryParameter(MMSDKConstants.KEY_EMAIL_ADDRESS, emailAddress)
			.appendQueryParameter(MMSDKConstants.KEY_FIRST_NAME, firstName)
			.appendQueryParameter(MMSDKConstants.KEY_LAST_NAME, lastName)
			.appendQueryParameter(MMSDKConstants.KEY_GENDER, Integer.toString(gender))
			.appendQueryParameter(MMSDKConstants.KEY_BIRTHDATE, birthdate);
		
//		Log.d(TAG, TAG + "signUpURL: " + signUpURL);
		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
	
	/**
 	 * Function that signs out user from MobMonkey server
	 * @param mmCallback The {@link MMCallback} to handle the response from MobMonkey server after posting the sign out url
	 * @param partnerId MobMonkey unique partner id
	 * @param user The email of the user if signed in normally with email or the provider username if signed in through social networks
	 * @param auth The password of the user if signed in normally with email or the provider OAuth token if signed in through social networks
	 */
	public static void signOut(MMCallback mmCallback,
							   String partnerId,
							   String user,
							   String auth) {
		Builder uriBuilder = Uri.parse(MMSDKConstants.MOBMONKEY_URL).buildUpon();
		uriBuilder.appendPath(MMSDKConstants.URI_PATH_SIGNOUT)
			.appendPath(MMSDKConstants.DEVICE_TYPE)
			.appendPath(MMDeviceUUID.getDeviceUUID().toString());

		Log.d(TAG, TAG + "uriBuilder: " + uriBuilder.toString());
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_USER, user);
		httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
		httpPost.setHeader(MMSDKConstants.KEY_OAUTH_TOKEN, auth);
		
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
	public static void getUserInfo(MMCallback mmCallback,
								   String partnerId,
								   String user,
								   String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_USER);
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, user);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param firstName
	 * @param lastName
	 * @param password
	 * @param birthdate
	 * @param gender
	 * @param city
	 * @param state
	 * @param zip
	 * @param acceptedtos
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void updateUserInfo(MMCallback mmCallback,
									  String firstName,
									  String lastName,
			  						  String password,
									  long birthdate,
									  int gender,
									  String city,
									  String state,
									  String zip,
									  boolean acceptedtos,
									  String partnerId,
									  String user,
									  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_USER);
		createParamsInstance();
		
		try {
			params.put(MMSDKConstants.KEY_FIRST_NAME, firstName);
			params.put(MMSDKConstants.KEY_LAST_NAME, lastName);
			params.put(MMSDKConstants.KEY_PASSWORD, password);
			params.put(MMSDKConstants.KEY_BIRTHDATE, birthdate);
			params.put(MMSDKConstants.KEY_GENDER, gender);
			params.put(MMSDKConstants.KEY_CITY, city);
			params.put(MMSDKConstants.KEY_STATE, state);
			params.put(MMSDKConstants.KEY_ZIP, zip);
			params.put(MMSDKConstants.KEY_ACCEPTEDTOS, acceptedtos);
			
			Log.d(TAG, params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMSDKConstants.KEY_USER, user);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void subscribeUser(MMCallback mmCallback, String partnerId, String user, String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_USER, MMSDKConstants.URI_PATH_PAID_SUBSCRIPTION);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_EMAIL, user)
			.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_PARTNER_ID, partnerId);
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_USER, user);
		httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
	}
}
