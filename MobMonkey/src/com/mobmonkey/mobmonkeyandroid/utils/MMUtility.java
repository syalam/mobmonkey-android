package com.mobmonkey.mobmonkeyandroid.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

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
	
	/**
	 * 
	 * @param timeToExpiryDateInMillisecond
	 * @return
	 */
	public static String getExpiryDate(long timeToExpiryDateInMillisecond) {		
		String expiryDate = MMSDKConstants.DEFAULT_STRING_EMPTY;
		
		if(timeToExpiryDateInMillisecond < 60000) {
			expiryDate = Integer.toString((int) Math.floor(timeToExpiryDateInMillisecond / 1000)) + "s";
		} else if(timeToExpiryDateInMillisecond < 3600000) {
			expiryDate = Integer.toString((int) Math.floor(timeToExpiryDateInMillisecond / 60000)) + "m";
		} else {
			expiryDate = Integer.toString((int) Math.floor(timeToExpiryDateInMillisecond / 3600000)) + "h";
		}
		
		return expiryDate;
	}
	
	/**
	 * 
	 * @param activity
	 * @return
	 */
	public static int getImageMediaMeasuredWidth(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		int calculatedWidthPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, activity.getResources().getDisplayMetrics());
		return display.getWidth() - calculatedWidthPadding * 2;
	}
	
	/**
	 * 
	 * @param activity
	 * @return
	 */
	public static int getImageMediaMeasuredHeight(Activity activity) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260.0f, activity.getResources().getDisplayMetrics());
	}
}
