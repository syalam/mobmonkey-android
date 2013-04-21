package com.mobmonkey.mobmonkeyandroid.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.location.Location;
import android.util.Log;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMUtility {
	private static final String TAG = "MMUtility";
	
	private MMUtility () {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param loc1
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static String calcDist(Location loc1, double latitude, double longitude) {
		Location resultLocation = new Location(loc1);
		resultLocation.setLatitude(latitude);
		resultLocation.setLongitude(longitude);
		
		return convertMetersToMiles(loc1.distanceTo(resultLocation));
	}
	
	/**
	 * 
	 * @param dist
	 * @return
	 */
	public static String convertMetersToMiles(double dist) {
		dist = dist * 0.000621371f;
		
		return new DecimalFormat("#.##").format(dist);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getDate(long milliSeconds, String format) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(milliSeconds);
	     
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    return sdf.format(calendar.getTime());
	}
	
	public static String getExpiryDate(long timeToExpiryDateInMillisecond) {
		Log.d(TAG, "expiryDate diff: " + timeToExpiryDateInMillisecond);
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(timeToExpiryDateInMillisecond);
		SimpleDateFormat sdf;
		String expiryDate = MMSDKConstants.DEFAULT_STRING_EMPTY;
		
		if(timeToExpiryDateInMillisecond < 60000) {
			sdf = new SimpleDateFormat("s");
			expiryDate = sdf.format(calendar.getTime()) + "s";
		} else if(timeToExpiryDateInMillisecond < 3600000) {
			sdf = new SimpleDateFormat("m");
			expiryDate = sdf.format(calendar.getTime()) + "m";
		} else {
			sdf = new SimpleDateFormat("m");
			expiryDate = sdf.format(calendar.getTime()) + "h";
		}
		
		return expiryDate;
	}
}
