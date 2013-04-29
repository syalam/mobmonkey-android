package com.mobmonkey.mobmonkeysdk.adapters;

import android.content.Context;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.GeocoderTask;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMGeocoderAdapter {
	
	/**
	 * 
	 * @param context
	 * @param mmCallback
	 * @param address
	 */
	public static void getFromLocationName(Context context,
										   MMCallback mmCallback,
										   String address) {
		new GeocoderTask(context, mmCallback).execute(address);
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
		new GeocoderTask(context, mmCallback).execute(latitude, longitude);
	}
}
