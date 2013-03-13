package com.mobmonkey.mobmonkey;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService{

	public static final String SENDER_ID = "41188709907";
	
	public GCMIntentService() {
        super(SENDER_ID);
    }

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d(TAG, arg1);
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.d(TAG, arg1.getDataString());
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		Log.d(TAG, arg1);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.d(TAG, arg1);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
	
	

}
