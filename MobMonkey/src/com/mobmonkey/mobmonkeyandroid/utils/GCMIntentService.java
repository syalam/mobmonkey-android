package com.mobmonkey.mobmonkeyandroid.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.R.drawable;
import com.mobmonkey.mobmonkeyandroid.R.string;
import com.mobmonkey.mobmonkeyandroid.MainScreen;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class GCMIntentService extends GCMBaseIntentService {
	// TODO: senderid hardcoded
	public static final String SENDER_ID = "406893575625";
	public static final String SERVER_URL = MMSDKConstants.MOBMONKEY_URL;
	
	public GCMIntentService() {
        super(SENDER_ID);
    }

	@Override
	protected void onError(Context arg0, String error) {
		Log.d(TAG, error);
	}

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = "From GCM: you got message!";
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

	@Override
	protected void onDeletedMessages(Context context, int total) {
		 generateNotification(context, "Delete Message");
	}
	
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.d(TAG, "onRegistered");
		ServerUtility.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.d(TAG, "onUnregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtility.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
	
	/**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.app_icon;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainScreen.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
    
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(MMSDKConstants.INTENT_FILTER_DISPLAY_MESSAGE);
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }

}
