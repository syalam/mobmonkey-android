package com.mobmonkey.mobmonkey.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService{

	private static final String SENDER_ID = "41188709907";
	
	public GCMIntentService(Context context) {
        super(SENDER_ID);
        init(context);
    }
	
	private void init(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		final String regId = GCMRegistrar.getRegistrationId(context);
		Log.d(TAG, regId);
		if (regId.equals("")) {
		  GCMRegistrar.register(context, SENDER_ID);
		} else {
		  Log.d(TAG, "Already registered.");
		}
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d(TAG, "onError");
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.d(TAG, "onMessage");
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		Log.d(TAG, "onRegistered.");
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.d(TAG, "onMessage");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		
	}
	
	

}
