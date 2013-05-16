package com.mobmonkey.mobmonkeyandroid.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMUtility {
	private static final String TAG = "MMUtility: ";
	
	private MMUtility () {
		Log.d(TAG, TAG + "private constructor, cannot initialize");
		throw new AssertionError();
	}
	
	/**
	 * Filter the result into locations and subLocations and add the subLocations into the respective location
	 * @param result
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray filterSubLocations(String result) throws JSONException {
		JSONArray jArr = new JSONArray(result);
		ArrayList<JSONObject> locations = new ArrayList<JSONObject>();
		ArrayList<MMSubLocations> subLocations = new ArrayList<MMSubLocations>();
		
		for(int i = 0; i < jArr.length(); i++) {
			JSONObject jObj = jArr.getJSONObject(i);
			if(jObj.isNull(MMSDKConstants.JSON_KEY_PARENT_LOCATION_ID)) {
				locations.add(jObj);
			} else {
				String parentLocationId = jObj.getString(MMSDKConstants.JSON_KEY_PARENT_LOCATION_ID);
				boolean sameParentLocationId = false;
				for(int j = 0; j < subLocations.size(); j++) {
					if(subLocations.get(j).parentLocationId.equals(parentLocationId)) {
						subLocations.get(j).subLocations.put(jObj);
						sameParentLocationId = true;
						break;
					}
				}
				if(!sameParentLocationId) {
					MMSubLocations mmSubLocations = new MMSubLocations();
					mmSubLocations.parentLocationId = parentLocationId;
					mmSubLocations.subLocations.put(jObj);
					subLocations.add(mmSubLocations);
				}
			}
		}
		
		for(int i = 0; i < subLocations.size(); i++) {
			String parentLocationId = subLocations.get(i).parentLocationId;
			for(int j = 0; j < locations.size(); j++) {
				if(locations.get(j).getString(MMSDKConstants.JSON_KEY_LOCATION_ID).equals(parentLocationId)) {
					locations.get(j).put(MMSDKConstants.JSON_KEY_SUB_LOCATIONS, subLocations.get(i).subLocations);
					break;
				}
			}
		}
		
		return new JSONArray(locations);
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
		DecimalFormat decimalFormat = new DecimalFormat(MMSDKConstants.DECIMAL_FORMAT_ZEROES_TWO);
		decimalFormat.setDecimalSeparatorAlwaysShown(true);
		
		return decimalFormat.format(dist);
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
