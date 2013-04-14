package com.mobmonkey.mobmonkeysdk.utils;

import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Final class that handles obtaining the device {@link UUID} with {@link TelephonyManager} device id and {@link Secure} android id
 * @author Dezapp, LLC
 *
 */
public final class MMDeviceUUID {
	private static final String TAG = "MMDeviceUUID: ";
	private static UUID deviceUUID;
	private static Context context;
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMDeviceUUID() {
		throw new AssertionError();
	}
	
	/**
	 * Function that gets the deviceId from the {@link TelephonyManager} and gets the android id from {@link Secure} to obtain the {@link UUID} of the device
	 * @return deviceUUID
	 */
	public static UUID getDeviceUUID() {
		if(deviceUUID == null) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        
	        String tmDevice = telephonyManager.getDeviceId();
	        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	        deviceUUID = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32));
			Log.d(TAG, TAG + "deviceUUID: " + deviceUUID);
		}
		return deviceUUID;
	}
	
	/**
	 * Function set {@link Context} for this class to be used to obtain the {@link TelephonyManager}
	 * @param c {@link Context}
	 */
	public static void setContext(Context c) {
		context = c;
	}
}
