package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

public class MMTrendingAdapter extends MMAdapter {
	private static String TAG = "MMTrendingAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMTrendingAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	private static void getTrending(MMCallback mmCallback,
									String partnerId,
									String user,
									String auth) {
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
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
	 * @param timeSpan
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getTrendingCounts(MMCallback mmCallback,
									   	String timeSpan,
									   	String partnerId,
									   	String user,
									   	String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_TRENDING, MMSDKConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_COUNTS_ONLY, Boolean.toString(true));
		
		getTrending(mmCallback, partnerId, user, auth);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getAllFavorites(MMCallback mmCallback,
								   	   String timeSpan,
								   	   String partnerId,
								   	   String user,
								   	   String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_TRENDING, MMSDKConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan);
		getTrending(mmCallback, partnerId, user, auth);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getFavoritesNearby(MMCallback mmCallback,
									   	  String timeSpan,
									   	  double latitude,
									   	  double longitude,
									   	  int radius,
									   	  String partnerId,
									   	  String user,
									   	  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_TRENDING, MMSDKConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius));
		getTrending(mmCallback, partnerId, user, auth);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param categoryIds
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getFavoritesNearbyByInterests(MMCallback mmCallback,
											   	     String timeSpan,
											   	     double latitude,
											   	     double longitude,
											   	     int radius,
											   	     String categoryIds,
											   	     String partnerId,
											   	     String user,
											   	     String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_TRENDING, MMSDKConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_MY_INTEREST, Boolean.toString(true))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_CATEGORY_IDS, categoryIds);
		getTrending(mmCallback, partnerId, user, auth);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getTopViewed(MMCallback mmCallback,
											String timeSpan,
											String partnerId,
											String user,
											String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_TRENDING, MMSDKConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan);
		getTrending(mmCallback, partnerId, user, auth);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getTopViewedNearby(MMCallback mmCallback,
										  String timeSpan,
										  double latitude,
										  double longitude,
										  int radius,
										  String partnerId,
										  String user,
										  String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_TRENDING, MMSDKConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
  				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius));
		getTrending(mmCallback, partnerId, user, auth);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param categoryIds
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getTopViewedNearbyByInterests(MMCallback mmCallback,
											   	     String timeSpan,
											   	     double latitude,
											   	     double longitude,
											   	     int radius,
											   	     String categoryIds,
											   	     String partnerId,
											   	     String user,
											   	     String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_TRENDING, MMSDKConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
  				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_MY_INTEREST, Boolean.toString(true))
				  .appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_CATEGORY_IDS, categoryIds);
		getTrending(mmCallback, partnerId, user, auth);
	}
}
