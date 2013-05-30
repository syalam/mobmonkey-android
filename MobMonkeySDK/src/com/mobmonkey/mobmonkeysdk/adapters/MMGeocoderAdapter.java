package com.mobmonkey.mobmonkeysdk.adapters;

import android.content.Context;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMGeocodeAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMGeocoderAdapter {
	
	/**
	 * 
	 */
	private MMGeocoderAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param context
	 * @param mmCallback
	 * @param address
	 */
	public static void getFromLocationName(Context context,
										   MMCallback mmCallback,
										   String address) {
		new MMGeocodeAsyncTask(context, mmCallback).execute(address);
	}
	
	/**
	 * 
	 * @param context
	 * @param mmCallback
	 * @param latitude
	 * @param longitude
	 */
	public static void getFromLocation(Context context,
									   MMCallback mmCallback,
									   double latitude,
									   double longitude) {
		new MMGeocodeAsyncTask(context, mmCallback).execute(latitude, longitude);
	}
}
