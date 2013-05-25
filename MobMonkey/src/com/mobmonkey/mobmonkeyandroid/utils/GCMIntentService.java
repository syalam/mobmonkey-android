package com.mobmonkey.mobmonkeyandroid.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.mobmonkey.mobmonkeyandroid.MainScreen;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.R.drawable;
import com.mobmonkey.mobmonkeyandroid.R.string;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "GCMIntentService: ";
	
	public static final String SENDER_ID = "406893575625";
	public static final String SERVER_URL = "http://api.mobmonkey.com/rest/media/testGCM?deviceId=";
	
	public GCMIntentService() {
		super(SENDER_ID);
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onError(android.content.Context, java.lang.String)
	 */
	@Override
	protected void onError(Context context, String error) {
		Log.d(TAG, TAG + "error: " + error);
	}

	/* (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onMessage(android.content.Context, android.content.Intent)
	 */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d(TAG, TAG + "message: " + intent.getExtras().getString("message"));
		
        String message = intent.getExtras().getString("message");
        displayMessage(context, message);
        generateNotification(context, message);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onDeletedMessages(android.content.Context, int)
	 */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		generateNotification(context, "Delete Message");
	}

	/* (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onRegistered(android.content.Context, java.lang.String)
	 */
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.d(TAG, "Device registered: regId = " + registrationId);
//		ServerUtility.register(context, registrationId);
	}

	/* (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onUnregistered(android.content.Context, java.lang.String)
	 */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.d(TAG, "unregistered = " + registrationId);
		if(GCMRegistrar.isRegisteredOnServer(context)) {
//			ServerUtility.unregister(context, registrationId);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onRecoverableError(android.content.Context, java.lang.String)
	 */
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
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainScreen.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
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
