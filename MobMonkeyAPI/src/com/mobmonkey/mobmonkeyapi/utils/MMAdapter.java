package com.mobmonkey.mobmonkeyapi.utils;

import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;

/**
 * @author Dezapp, LLC
 *
 */
public class MMAdapter {
	protected static Builder uriBuilder;
	protected static JSONObject params;
	
	public static void createUriBuilderInstance(String... path) {
		uriBuilder = Uri.parse(MMAPIConstants.MOBMONKEY_URL)
			.buildUpon();
		for(int i = 0; i < path.length; i++) {
			uriBuilder.appendPath(path[i]);
		}
	}
	
	public static void createParamsInstance() {
		params = new JSONObject();
	}
}
