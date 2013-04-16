package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMGetAsyncTask;

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
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	private static void getTrending(MMCallback mmCallback,
									String emailAddress,
									String password,
									String partnerId) {
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
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
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param categoryIds
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void getTrendingCounts(MMCallback mmCallback,
									   	String timeSpan,
									   	double latitude,
									   	double longitude,
									   	int radius,
									   	String categoryIds,
									   	String emailAddress,
									   	String password,
									   	String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_TRENDING, MMAPIConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_COUNTS_ONLY, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_FAVORITES_ONLY, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_MY_INTEREST, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_CATEGORY_IDS, categoryIds);
		
		getTrending(mmCallback, emailAddress, password, partnerId);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void getAllFavorites(MMCallback mmCallback,
								   	   String timeSpan,
								   	   String emailAddress,
								   	   String password,
								   	   String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_TRENDING, MMAPIConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan);
		getTrending(mmCallback, emailAddress, password, partnerId);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void getFavoritesNearby(MMCallback mmCallback,
									   	  String timeSpan,
									   	  double latitude,
									   	  double longitude,
									   	  int radius,
									   	  String emailAddress,
									   	  String password,
									   	  String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_TRENDING, MMAPIConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius));
		getTrending(mmCallback, emailAddress, password, partnerId);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param categoryIds
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void getFavoritesNearbyByInterests(MMCallback mmCallback,
											   	     String timeSpan,
											   	     double latitude,
											   	     double longitude,
											   	     int radius,
											   	     String categoryIds,
											   	     String emailAddress,
											   	     String password,
											   	     String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_TRENDING, MMAPIConstants.URI_PATH_FAVORITES);
		uriBuilder.appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_MY_INTEREST, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_CATEGORY_IDS, categoryIds);
		getTrending(mmCallback, emailAddress, password, partnerId);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void getTopViewed(MMCallback mmCallback,
											String timeSpan,
											String emailAddress,
											String password,
											String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_TRENDING, MMAPIConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan);
		getTrending(mmCallback, emailAddress, password, partnerId);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void getTopViewedNearby(MMCallback mmCallback,
										  String timeSpan,
										  double latitude,
										  double longitude,
										  int radius,
										  String emailAddress,
										  String password,
										  String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_TRENDING, MMAPIConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
  				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius));
		getTrending(mmCallback, emailAddress, password, partnerId);
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param timeSpan
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param categoryIds
	 * @param emailAddress
	 * @param password
	 * @param partnerId
	 */
	public static void getTopViewedNearbyByInterests(MMCallback mmCallback,
											   	     String timeSpan,
											   	     double latitude,
											   	     double longitude,
											   	     int radius,
											   	     String categoryIds,
											   	     String emailAddress,
											   	     String password,
											   	     String partnerId) {
		createUriBuilderInstance(MMAPIConstants.URI_PATH_TRENDING, MMAPIConstants.URI_PATH_TOP_VIEWED);
		uriBuilder.appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_TIME_SPAN, timeSpan)
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_NEARBY, Boolean.toString(true))
  				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LATITUDE, Double.toString(latitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_LONGITUDE, Double.toString(longitude))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_RADIUS, Integer.toString(radius))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_MY_INTEREST, Boolean.toString(true))
				  .appendQueryParameter(MMAPIConstants.URI_QUERY_PARAM_KEY_CATEGORY_IDS, categoryIds);
		getTrending(mmCallback, emailAddress, password, partnerId);
	}
}
