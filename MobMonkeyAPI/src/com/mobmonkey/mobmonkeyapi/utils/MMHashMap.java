/**
 * 
 */
package com.mobmonkey.mobmonkeyapi.utils;

import java.util.HashMap;

/**
 * @author Wilson
 *
 */
public final class MMHashMap extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9091692249221377431L;

	private MMHashMap() {
		
	}
	
	public static HashMap<String, Object> getInstance(String partnerId) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
		hashMap.put(MMAPIConstants.KEY_PARTNER_ID, partnerId);
		return hashMap;
	}
}
