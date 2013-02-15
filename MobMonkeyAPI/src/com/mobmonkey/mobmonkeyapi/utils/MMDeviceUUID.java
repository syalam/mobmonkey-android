package com.mobmonkey.mobmonkeyapi.utils;

import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMDeviceUUID {
	private static final String TAG = "MMDeviceUUID: ";
	private static UUID deviceUUID;
	private static Context context;
	
	private MMDeviceUUID() {
		
	}
	
	public static UUID getDeviceUUID() {
		if(deviceUUID == null) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        
	        String tmDevice = telephonyManager.getDeviceId();
	        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	        deviceUUID = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32));
		}
		
		return deviceUUID;
	}
	
	public static void setContext(Context c) {
		context = c;
	}
}
