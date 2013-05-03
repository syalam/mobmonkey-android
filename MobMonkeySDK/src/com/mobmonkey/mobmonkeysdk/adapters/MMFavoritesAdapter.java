package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;


import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

public class MMFavoritesAdapter extends MMAdapter {
	private final static String TAG = "MMFavorites";
	
	private static MMGetAsyncTask mmGetAsyncTask;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMFavoritesAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param locationId
	 * @param providerId
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void addFavorite(MMCallback mmCallback,
								   String locationId,
								   String providerId,
								   String partnerId,
								   String user,
								   String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_FAVORITES);
		createParamsInstance();

		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId);
			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMSDKConstants.KEY_USER, user);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch(JSONException ex) {
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
	public static void getFavorites(MMCallback mmCallback,
									String partnerId,
									String user,
									String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_FAVORITES);
		
		HttpGet HttpGet = new HttpGet(uriBuilder.toString());

		HttpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		HttpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		HttpGet.setHeader(MMSDKConstants.KEY_USER, user);
		HttpGet.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(HttpGet);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param locationId
	 * @param providerId
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void removeFavorite(MMCallback mmCallback,
									  String locationId,
									  String providerId,
									  String partnerId,
									  String user,
									  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId)
			.appendQueryParameter(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		
		httpDelete.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMSDKConstants.KEY_USER, user);
		httpDelete.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
	
	/**
	 * 
	 */
	public static void cancelGetFavorites() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}
