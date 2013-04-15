package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;


import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMPostAsyncTask;

public class MMFavoritesAdapter extends MMAdapter {
	private final static String TAG = "MMFavorites";
	
	private static MMGetAsyncTask mmGetAsyncTask;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMFavoritesAdapter() {
		throw new AssertionError();
	}
	
	/* HTTP POST http://api.mobmonkey.com/rest/bookmarks
	 * 
 	   Body
	    "locationId":"eeb203e7-a4f0-4318-be8b-b00f613c7e37",
	    "providerId":"222e736f-c7fa-4c40-b78e-d99243441fae"
	 */
	public static void addFavorite(MMCallback mmCallback,
									  String locationId,
									  String providerId,
									  String partnerId,
									  String emailAddress,
									  String password) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_FAVORITES);
		createParamsInstance();

		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId);
			params.put(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
			
			Log.d(TAG, TAG + "params: " + params.toString());
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPost.setEntity(stringEntity);
			httpPost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httpPost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch(JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void getFavorites(MMCallback mmCallback,
									String partnerId,
									String emailAddress,
									String password) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_FAVORITES);
		
		HttpGet HttpGet = new HttpGet(uriBuilder.toString());

		HttpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		HttpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		HttpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		HttpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		mmGetAsyncTask = new MMGetAsyncTask(mmCallback);
		mmGetAsyncTask.execute(HttpGet);
	}
	
	public static void removeFavorite(MMCallback mmCallback,
									  String locationId,
									  String providerId,
									  String partnerId,
									  String emailAddress,
									  String password) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId)
			.appendQueryParameter(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		
		httpDelete.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpDelete.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
	
	public static void cancelGetFavorites() {
		if(mmGetAsyncTask != null) {
			if(!mmGetAsyncTask.isCancelled()) {
				mmGetAsyncTask.cancel(true);
			}
		}
	}
}
