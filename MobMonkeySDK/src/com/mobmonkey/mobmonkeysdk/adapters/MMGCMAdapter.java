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
	 */
	private MMGCMAdapter() {
		throw new AssertionError();
	}

	/**
	 * 
	 * @param mmCallback
	 * @param context
	 * @param regId
	 */
	public static void registerGCMRegId(MMCallback mmCallback,
										Context context,
										String regId) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA, MMSDKConstants.URI_PATH_TESTGCM);
		uriBuilder.appendQueryParameter(MMSDKConstants.URI_QUERY_PARAM_KEY_DEVICE_ID, regId);
		
		HttpPost httpPost = newHttpPostInstance();
		
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
