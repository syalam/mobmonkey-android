package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

public class MMTrendingAdapter {
	private static String TAG = "MMTrendingAdapter: ";
	
	public static void getTrending(MMCallback mmCallback,
								   // path parameters
								   String type,
								   // query parameters
								   String timeSpan,
								   boolean nearby,
								   boolean bookmarksonly,
								   double latitude,
								   double longitude,
								   int radius,
								   boolean myinterests,
								   String categoryIds,
								   boolean countsonly,
								   // headers
								   String partnerId,
								   String emailAddress,
								   String password) {
		
		Builder uriBuilder = Uri.parse(MMAPIConstants.MOBMONKEY_URL).buildUpon();
		uriBuilder.appendPath(MMAPIConstants.URI_PATH_TRENDING)
			.appendPath(type)
		   .appendQueryParameter("timeSpan", timeSpan);
		
		// if nearby is true, append required parameters
		if(nearby) {
			uriBuilder.appendQueryParameter("nearby", nearby+"")
			   .appendQueryParameter("latitude", latitude+"")
			   .appendQueryParameter("longitude", longitude+"")
			   .appendQueryParameter("radius", radius+"");
		}
		
		// if bookmarks is true, append bookmark
		if(bookmarksonly) {
			uriBuilder.appendQueryParameter("bookmarksonly", bookmarksonly+"");
		}
		
		// if myinterests, append required parameters
		if(myinterests) {
			uriBuilder.appendQueryParameter("myinterests", myinterests+"")
			   .appendQueryParameter("categoryIds", categoryIds);
		}
		
		// if countsonly, append nearby, bookmarksonly, myinterests, and their dependent parameters.
		if(countsonly) {
			uriBuilder.appendQueryParameter("countsonly", countsonly+"")
			   .appendQueryParameter("nearby", nearby+"")
			   .appendQueryParameter("latitude", latitude+"")
			   .appendQueryParameter("longitude", longitude+"")
			   .appendQueryParameter("radius", radius+"")
			   .appendQueryParameter("bookmarksonly", bookmarksonly+"")
			   .appendQueryParameter("myinterests", myinterests+"")
			   .appendQueryParameter("categoryIds", categoryIds);
		}
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		
		// add header
		httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpGet.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		Log.d(TAG, uriBuilder.toString());
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
}
