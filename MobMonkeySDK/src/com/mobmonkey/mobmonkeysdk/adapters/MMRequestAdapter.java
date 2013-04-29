package com.mobmonkey.mobmonkeysdk.adapters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.os.Environment;
import android.util.Log;

import com.mobmonkey.mobmonkeysdk.utils.MMDeleteAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMGetAsyncTask;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMPostAsyncTask;

/**
 * The adapter for answering a request that is assigned to the user.
 * @author Dezapp, LLC
 *  
 */
public class MMRequestAdapter extends MMAdapter {
	private static String TAG = "MMAnswerRequestAdapter: ";
	
	/**
	 * Private class to prevent the instantiation of this class outside the scope of this class
	 */
	private MMRequestAdapter() {
		throw new AssertionError();
	}
	
	/**
	 * Answer a request.
	 * @param {@link MMCallback} 
	 * @param mediaType. The type of media file. Takes either "video" or "image".
	 * @param requestID. The unique ID of the request that we are fulfilling.
	 * @param requestType. 0 = Non-recurring, 1 = Recurring
	 * @param contentType. The type of the data that is going to be send to the server. Currently supported types are "image/jpg",
	 * 		  "image/jpeg", "image/png", "video/mp4", "video/mpeg", and "video/quicktime".
	 * @param mediaData. The Base64 encoded media data.
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void answerRequest(MMCallback mmCallback,
									 String mediaType,
									 String requestID,
									 int requestType,
									 String contentType,
									 String mediaData,
									 String partnerId,
									 String user,
									 String auth) {
		
		createUriBuilderInstance(MMSDKConstants.URI_PATH_MEDIA, mediaType);
		createParamsInstance();
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		try {
			params.put(MMSDKConstants.JSON_KEY_REQUEST_ID, requestID);
			params.put(MMSDKConstants.JSON_KEY_REQUEST_TYPE, requestType);
			params.put(MMSDKConstants.JSON_KEY_CONTENT_TYPE, contentType);
			params.put(MMSDKConstants.JSON_KEY_MEDIA_DATA, mediaData);
			
			HttpPost httpPost = new HttpPost(uriBuilder.toString());
			// add header
			httpPost.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			httpPost.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
			httpPost.setHeader(MMSDKConstants.KEY_USER, user);
			httpPost.setHeader(MMSDKConstants.KEY_AUTH, auth);
			
			// might cause outofmemory
			try {
				StringEntity stringEntity = new StringEntity(params.toString());
				httpPost.setEntity(stringEntity);
				new MMPostAsyncTask(mmCallback).execute(httpPost);
			} catch (OutOfMemoryError er) {
				// write mediaInfo into sd card as a temp file.
				File root = Environment.getExternalStorageDirectory();
				if (!root.exists()) {
		            root.mkdirs();
		        }
				try {
					// write to file
					File tempfile = new File(root, "mobmonkeyMediaInfo");
					FileWriter writer = new FileWriter(tempfile);
					BufferedWriter bw = new BufferedWriter(writer);
					params = null;
					
					// try to write small piece of data into bufferedwriter
					bw.write("{");
					bw.write("\"" + MMSDKConstants.JSON_KEY_REQUEST_ID + "\":\"" + requestID + "\",");
					bw.write("\"" + MMSDKConstants.JSON_KEY_REQUEST_TYPE + "\":" + requestType + ",");
					bw.write("\"" + MMSDKConstants.JSON_KEY_CONTENT_TYPE + "\":\"" + contentType + "\",");
					// divide mediaData into smaller pieces
					bw.write("\"" + MMSDKConstants.JSON_KEY_MEDIA_DATA + "\":\"");
					
					for(int i = 0; i < mediaData.length(); i++) {
						
						if(!Character.isISOControl(mediaData.charAt(i))) {
//							Log.d(TAG, "At posittion " + i + ", character \"" + mediaData.charAt(i) + "\"");
							bw.write(mediaData.charAt(i));
						}
					}
					
					bw.write("\"");
					bw.write("}");
					bw.flush();
					bw.close();
			        
					// put tempfile into FileEntity
					FileEntity reqEntity = new FileEntity(tempfile, MMSDKConstants.CONTENT_TYPE_APP_JSON);
			        httpPost.setEntity(reqEntity);
//			        
//			        // delete tempfile
//			        tempfile.delete();
			        
			        new MMPostAsyncTask(mmCallback).execute(httpPost);
			        
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param mmCallback
	 * @param requestId
	 * @param isRecurring
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void deleteRequest(MMCallback mmCallback,
									 String requestId,
									 String isRecurring,
									 String partnerId,
									 String user,
									 String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_REQUESTMEDIA);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_REQUEST_ID, requestId);
		uriBuilder.appendQueryParameter(MMSDKConstants.JSON_KEY_IS_RECURRING, isRecurring);
		
		Log.d(TAG, uriBuilder.toString());
		
		HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
		httpDelete.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpDelete.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpDelete.setHeader(MMSDKConstants.KEY_USER, user);
		httpDelete.setHeader(MMSDKConstants.KEY_AUTH, auth);
		new MMDeleteAsyncTask(mmCallback).execute(httpDelete);
	}
	
	/**
	 * Get all answered requests that have been fulfilled or waiting to be fulfilled.
	 * @param mmCallback 
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getAnsweredRequests(MMCallback mmCallback,
										   String partnerId,
										   String user,
										   String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_ANSWEREDREQUESTS);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, user);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	
	/**
	 * Get all requests that have been assigned to you from the checkin API.
	 * @param mmCallback
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getAssignedRequests(MMCallback mmCallback,
										   String partnerId,
										   String user,
										   String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_ASSIGNEDREQUESTS);
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, user);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
	
	/**
	 * Get all open requests that have been fulfilled or waiting to be fulfilled.
	 * @param mmCallback
	 * @param partnerId
	 * @param user
	 * @param auth
	 */
	public static void getOpenRequests(MMCallback mmCallback,
									   String partnerId,
									   String user,
									   String auth) {
		createUriBuilderInstance(MMSDKConstants.URI_PATH_INBOX, MMSDKConstants.URI_PATH_OPENREQUESTS);
		
		Log.d(TAG, TAG + "uri: " + uriBuilder.toString());
		
		HttpGet httpGet = new HttpGet(uriBuilder.toString());
		httpGet.setHeader(MMSDKConstants.KEY_CONTENT_TYPE, MMSDKConstants.CONTENT_TYPE_APP_JSON);
		httpGet.setHeader(MMSDKConstants.KEY_PARTNER_ID, partnerId);
		httpGet.setHeader(MMSDKConstants.KEY_USER, user);
		httpGet.setHeader(MMSDKConstants.KEY_AUTH, auth);
		
		new MMGetAsyncTask(mmCallback).execute(httpGet);
	}
}
