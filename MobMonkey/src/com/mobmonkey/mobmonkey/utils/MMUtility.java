package com.mobmonkey.mobmonkey.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.location.Location;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMUtility {
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
}
