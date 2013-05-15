package com.mobmonkey.mobmonkeyandroid.utils;

import org.json.JSONArray;

/**
 * @author Dezapp, LLC
 *
 */
public class MMSubLocations {
	public String parentLocationId;
	public JSONArray subLocations;
	
	public MMSubLocations() {
		subLocations = new JSONArray();
	}
}
