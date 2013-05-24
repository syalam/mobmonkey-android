package com.mobmonkey.mobmonkeyandroid.listeners;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Dezapp, LLC
 *
 */
public interface MMOnCreateHotSpotFragmentClickListener {
	public void onCreateHotSpotClick(JSONArray jArr);
	
	public void onCreateHotSpotClick(JSONObject jObj, int requestCode);
}
