package com.mobmonkey.mobmonkeyapi.utils;

import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMGetDeviceUUID {
	private static UUID deviceUUID;
	private static Context context;
	
	private MMGetDeviceUUID() {
		
	}
	
	public static UUID getDeviceUUID() {
		if(deviceUUID == null) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        
	        String tmDevice = telephonyManager.getDeviceId();
	        String tmSerial = telephonyManager.getSimSerialNumber();
	        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	        deviceUUID = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		}
		
		return deviceUUID;
	}
	
	public static void setContext(Context c) {
		context = c;
	}
}
