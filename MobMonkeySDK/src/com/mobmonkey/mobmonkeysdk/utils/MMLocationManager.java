package com.mobmonkey.mobmonkeysdk.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMLocationManager {
	private static LocationManager locationManager;
	
	public static void setContext(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);		
	}
	
	public static boolean isGPSEnabled() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static Location getGPSLocation(LocationListener locationListener) {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
}
