package com.mobmonkey.mobmonkeysdk.utils;

import java.text.DecimalFormat;

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
	private static Location location;
	private static DecimalFormat decimalFormat = new DecimalFormat(MMSDKConstants.DECIMAL_FORMAT_SIX);
	
	public static void setContext(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);		
	}
	
	public static boolean isGPSEnabled() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static Location getGPSLocation(LocationListener locationListener) {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location;
	}
	
	public static double getLocationLatitude() {
		return Double.valueOf(decimalFormat.format(location.getLatitude()));
//		return 37.787205; // test (near iOS simulator location);
//		return 33.42358; // Brickyard
//		return 33.414883; // Domain
	}
	
	public static double getLocationLongitude() {
		return Double.valueOf(decimalFormat.format(location.getLongitude()));
//		return -122.410973;
//		return -111.93929; // Brickyard
//		return -111.904784; // Domain
	}
}
