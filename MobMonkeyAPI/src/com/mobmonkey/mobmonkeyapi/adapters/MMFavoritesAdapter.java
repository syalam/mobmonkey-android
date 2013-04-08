package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;


import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;

public class MMFavoritesAdapter {

	private final static String TAG = "MMBookmarks: ";
	private static String bookmarkURL;
	private static JSONObject bookmarkInfo;
	
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
		bookmarkURL = MMAPIConstants.TEST_MOBMONKEY_URL + "bookmarks";
		try {
			bookmarkInfo = new JSONObject();
			bookmarkInfo.put(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId);
			bookmarkInfo.put(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
			
			HttpPost httpPost = new HttpPost(bookmarkURL);
			StringEntity stringEntity = new StringEntity(bookmarkInfo.toString());
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
		bookmarkURL = MMAPIConstants.TEST_MOBMONKEY_URL + "bookmarks";
		
		HttpGet HttpGet = new HttpGet(bookmarkURL);

		HttpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		HttpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		HttpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		HttpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMGetAsyncTask(mmCallback).execute(HttpGet);
		
	}
	
	public static void removeFavorite(MMCallback mmCallback,
									  String locationId,
									  String providerId,
									  String partnerId,
									  String emailAddress,
									  String password) {
		
		Uri.Builder uriBuilder = Uri.parse(bookmarkURL).buildUpon();
		uriBuilder.appendQueryParameter(MMAPIConstants.JSON_KEY_LOCATION_ID, locationId)
			.appendQueryParameter(MMAPIConstants.JSON_KEY_PROVIDER_ID, providerId);
		
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		
		httpDelete.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpDelete.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
}
