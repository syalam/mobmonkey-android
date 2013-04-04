package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;

public class MMAnswerRequestAdapter {
	
	private static String TAG = "MMAnswerRequestAdapter";
	private static String AnswerRequestURL;
	private static JSONObject mediaInfo;
	
	public static void AnswerRequest(MMCallback mmCallback,
							   // headers
							   String partnerId,
							   String emailAddress,
							   String password,
							   // body
							   String requestID,
							   int requestType,
							   String contentType,
							   String mediaData,
							   String mediaType
							   ) {
		AnswerRequestURL = MMAPIConstants.TEST_MOBMONKEY_URL + "media/" + mediaType;
		
		mediaInfo = new JSONObject();
		try {
			//AnswerRequestURL = MMAPIConstants.TEST_MOBMONKEY_URL + "media/" + mediaType;
			mediaInfo.put(MMAPIConstants.JSON_KEY_REQUESTID, requestID);
			mediaInfo.put(MMAPIConstants.JSON_KEY_REQUEST_TYPE, requestType);
			mediaInfo.put(MMAPIConstants.JSON_KEY_CONTENT_TYPE, contentType);
			mediaInfo.put(MMAPIConstants.JSON_KEY_MEDIADATA, mediaData);
			
			HttpPost httppost = new HttpPost(AnswerRequestURL);
			// add header
			httppost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httppost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httppost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httppost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
			StringEntity stringEntity = new StringEntity(mediaInfo.toString());
			httppost.setEntity(stringEntity);
			
			new MMPostAsyncTask(mmCallback).execute(httppost);
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
