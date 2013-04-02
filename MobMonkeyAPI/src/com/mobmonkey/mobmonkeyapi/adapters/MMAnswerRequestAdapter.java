package com.mobmonkey.mobmonkeyapi.adapters;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;

public class MMAnswerRequestAdapter {
	
	private static String TAG = "MMAnswerRequestAdapter";
	private static String AnswerRequestURL;
	private static JSONObject mediaInfo;
	private static String contentType = "";
	
	public static void AnswerRequest(MMCallback mmCallback,
							   // headers
							   String partnerId,
							   String emailAddress,
							   String password,
							   // body
							   String requestID,
							   String mediaData,
							   long uploadedDate,
							   // other
							   int type) {
		AnswerRequestURL = MMAPIConstants.TEST_MOBMONKEY_URL + "media/";
		if(type == 1) {
			AnswerRequestURL += "image";
			contentType = "image/jpeg";
		} else if(type == 2) {
			AnswerRequestURL += "video";
		} 
		
		mediaInfo = new JSONObject();
		try {
			mediaInfo.put(MMAPIConstants.JSON_KEY_REQUESTID, requestID);
			mediaInfo.put(MMAPIConstants.JSON_KEY_MEDIADATA, mediaData);
			//mediaInfo.put(MMAPIConstants.JSON_KEY_UPLOADEDDATE, uploadedDate);
			mediaInfo.put("requestType", type);
			mediaInfo.put("contentType", contentType);
			
			HttpPost httppost = new HttpPost(AnswerRequestURL);
			// add header
			httppost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httppost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httppost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httppost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
			new MMPostAsyncTask(mmCallback).execute(httppost);
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}
}
