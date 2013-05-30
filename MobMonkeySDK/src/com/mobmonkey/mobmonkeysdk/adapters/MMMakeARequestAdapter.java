package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import android.util.Log;

import com.mobmonkey.mobmonkeysdk.asynctasks.MMPostAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMakeARequestAdapter extends MMAdapter {
	private static final String TAG = "MMSendRequestAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMMakeARequestAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param message
	 * @param scheduleDate
	 * @param providerId
	 * @param locationId
	 * @param duration
	 * @param repeating
	 * @param mediaType
	 */
	public static void makeARequest(MMCallback mmCallback,
									String message,
									String scheduleDate,
									String providerId,
									String locationId,
									int duration,
									String repeating,
									String mediaType) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_REQUESTMEDIA, mediaType);
		createParamsInstance();
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.JSON_KEY_MESSAGE, message);
			params.put(MMSDKConstants.JSON_KEY_SCHEDULE_DATE, scheduleDate);
			params.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, providerId);
			params.put(MMSDKConstants.JSON_KEY_LOCATION_ID, locationId);
			params.put(MMSDKConstants.JSON_KEY_DURATION, duration);
			if(repeating.equals(MMSDKConstants.REQUEST_REPEAT_RATE_NONE)) {
				params.put(MMSDKConstants.JSON_KEY_RECURRING, 0);
			} else {
				params.put(MMSDKConstants.JSON_KEY_RECURRING, true);
				long freq = MMSDKConstants.REQUEST_FREQUENCY_DAILY; //TODO: Change these to calculated values. This is miliseconds in a day for Daily repeat.
				if(repeating.equals(MMSDKConstants.REQUEST_REPEAT_RATE_WEEKLY))
					freq = MMSDKConstants.REQUEST_FREQUENCY_WEEKLY;
				if(repeating.equals(MMSDKConstants.REQUEST_REPEAT_RATE_MONTHLY))
					freq = MMSDKConstants.REQUEST_FREQUENCY_MONTHLY;
				params.put(MMSDKConstants.JSON_KEY_FREQUENCY_IN_MS, freq);
			}
			
			HttpPost httpPost = newHttpPostInstance();
			StringEntity stringEntity = new StringEntity(params.toString());
			httpPost.setEntity(stringEntity);

			new MMPostAsyncTask(mmCallback).execute(httpPost);
		} catch(JSONException ex) {
			
		} catch(UnsupportedEncodingException ex) {
			
		}		
	}
}
