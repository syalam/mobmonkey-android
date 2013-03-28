package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpGet;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGetAsyncTask;

public class MMTrendingAdapter {
	private static String TAG = "MMTrendingAdapter";
	private  static String trendingURL;
	
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
		
		trendingURL = MMAPIConstants.TEST_MOBMONKEY_URL +"trending";
		
		Builder uri = Uri.parse(trendingURL).buildUpon();
		uri.appendPath(type)
		   .appendQueryParameter("timeSpan", timeSpan);
		
		// if nearby is true, append required parameters
		if(nearby) {
			uri.appendQueryParameter("nearby", nearby+"")
			   .appendQueryParameter("latitude", latitude+"")
			   .appendQueryParameter("longitude", longitude+"")
			   .appendQueryParameter("radius", radius+"");
		}
		
		// if bookmarks is true, append bookmark
		if(bookmarksonly) {
			uri.appendQueryParameter("bookmarksonly", bookmarksonly+"");
		}
		
		// if myinterests, append required parameters
		if(myinterests) {
			uri.appendQueryParameter("myinterests", myinterests+"")
			   .appendQueryParameter("categoryIds", categoryIds);
		}
		
		// if countsonly, append nearby, bookmarksonly, myinterests, and their dependent parameters.
		if(countsonly) {
			uri.appendQueryParameter("countsonly", countsonly+"")
			   .appendQueryParameter("nearby", nearby+"")
			   .appendQueryParameter("latitude", latitude+"")
			   .appendQueryParameter("longitude", longitude+"")
			   .appendQueryParameter("radius", radius+"")
			   .appendQueryParameter("bookmarksonly", bookmarksonly+"")
			   .appendQueryParameter("myinterests", myinterests+"")
			   .appendQueryParameter("categoryIds", categoryIds);
		}
		
		HttpGet httpget = new HttpGet(uri.toString());
		
		// add header
		httpget.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		httpget.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		httpget.setHeader(MMAPIConstants.KEY_USER, emailAddress);
		httpget.setHeader(MMAPIConstants.KEY_AUTH, password);
		
		Log.d(TAG, uri.toString());
		
		new MMGetAsyncTask(mmCallback).execute(httpget);
	}
}
