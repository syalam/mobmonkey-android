package com.mobmonkey.mobmonkeysdk.adapters;

import org.apache.http.client.methods.HttpPost;

import com.google.android.gcm.GCMRegistrar;
import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;

/**
 * @author Dezapp, LLC
 *
 */
public class MMGCMAdapter extends MMAdapter {

	/**
	 * 
	 * @param mmCallback
	 * @param context
	 * @param regId
	 */
	public static void register(MMCallback mmCallback,
								Context context,
								String regId,
								String partnerId,
								String user,
								String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA, MMSDKConstants.URI_PATH_TESTGCM);
		
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_DEVICE_ID, regId);
		
		HttpPost httpPost = new HttpPost(uriBuilder.toString());
		httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);		
		httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpPost.setHeader(MMSDKConstants.KEY_USER, user);
		httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMPostAsyncTask(mmCallback).execute(httpPost);
		
		GCMRegistrar.setRegisteredOnServer(context, true);
	}
	
	/**
	 * 
	 * @param context
	 * @param regId
	 */
	public static void unRegister(Context context,
								  String regId) {
		GCMRegistrar.setRegisteredOnServer(context, false);
	}
}
