package com.mobmonkey.mobmonkeysdk.utils;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;

/**
 * @author Dezapp, LLC
 *
 */
public class MMAdapter {
	protected static Builder uriBuilder;
	protected static JSONObject params;
	protected static String partnerId;

	private static boolean useOAuth;
	private static String mmUser;
	private static String mmAuth;
	private static String oAuthProviderUserName;
	private static String oAuthProvider;

	/**
	 * 
	 * @param partnerID
	 * @param oauthProviderUserName
	 * @param oauthProvider
	 */
	public static void useOAuth(String partnerID, String oauthProviderUserName, String oauthProvider) {
		partnerId = partnerID;
		useOAuth = true;
		oAuthProviderUserName = oauthProviderUserName;
		oAuthProvider = oauthProvider;
	}
	
	/**
	 * 
	 * @param partnerID
	 * @param user
	 * @param auth
	 */
	public static void useMobMonkey(String partnerID, String user, String auth) {
		partnerId = partnerID;
		useOAuth = false;
		mmUser = user;
		mmAuth = auth;
	}
	
	/**
	 * 
	 * @return
	 */
	protected static String getEmail() {
		if(useOAuth) {
			return oAuthProviderUserName;
		} else {
			return mmUser;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static HttpDelete newHttpDeleteInstance() {
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		httpDelete.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		
		if(useOAuth) {
			httpDelete.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, oAuthProviderUserName);
			httpDelete.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER, oAuthProvider);
		} else {
			httpDelete.setHeader(MMSDKConstants.KEY_USER, mmUser);
			httpDelete.setHeader(MMSDKConstants.KEY_AUTH, mmAuth);
		}
		
		return httpDelete;
	}
	
	/**
	 * 
	 * @return
	 */
	public static HttpGet newHttpGetInstance() {
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		
		if(useOAuth) {
			httpGet.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, oAuthProviderUserName);
			httpGet.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER, oAuthProvider);
		} else {
			httpGet.setHeader(MMSDKConstants.KEY_USER, mmUser);
			httpGet.setHeader(MMSDKConstants.KEY_AUTH, mmAuth);
		}
		
		return httpGet;
	}
	
	/**
	 * 
	 * @return
	 */
	public static HttpPost newHttpPostInstance() {
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		
		if(useOAuth) {
			httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, oAuthProviderUserName);
			httpPost.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER, oAuthProvider);
		} else {
			httpPost.setHeader(MMSDKConstants.KEY_USER, mmUser);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, mmAuth);
		}
		
		return httpPost;
	}
	
	/**
	 * 
	 * @return
	 */
	public static HttpPut newHttpPutInstance() {
		HttpPut httpPut = new HttpPut(uriBuilder.toString());
		httpPut.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpPut.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		
		if(useOAuth) {
			httpPut.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME, oAuthProviderUserName);
			httpPut.setHeader(MMSDKConstants.KEY_OAUTH_PROVIDER, oAuthProvider);
		} else {
			httpPut.setHeader(MMSDKConstants.KEY_USER, mmUser);
			httpPut.setHeader(MMSDKConstants.KEY_AUTH, mmAuth);
		}
		return httpPut;
	}
	
	/**
	 * 
	 * @param path
	 */
	public static void createUriBuilderInstance(String... path) {
		uriBuilder = Uri.parse(MMSDKConstants.MOBMONKEY_URL).buildUpon();
		for(int i = 0; i < path.length; i++) {
			uriBuilder.appendPath(path[i]);
		}
	}
	
	public static void createParamsInstance() {
		params = new JSONObject();
	}
}
