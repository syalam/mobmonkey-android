package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMPostAsyncTask;

/**
 * The adapter for answering a request that is assigned to the user.
 * @author Dezapp, LLC
 *  
 */
public class MMAnswerRequestAdapter {
	
	private static String TAG = "MMAnswerRequestAdapter";
	private static String AnswerRequestURL;
	private static JSONObject mediaInfo;
	
	/**
	 * Answer a request.
	 * @param {@link MMCallback} 
	 * @param partnerId
	 * @param Email address
	 * @param Password
	 * @param requestID. The unique ID of the request that we are fulfilling.
	 * @param requestType. 0 = Non-recurring, 1 = Recurring
	 * @param contentType. The type of the data that is going to be send to the server. Currently supported types are "image/jpg",
	 * 		  "image/jpeg", "image/png", "video/mp4", "video/mpeg", and "video/quicktime".
	 * @param mediaData. The Base64 encoded media data.
	 * @param mediaType. The type of media file. Takes either "video" or "image".
	 */
	
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
			mediaInfo.put(MMAPIConstants.JSON_KEY_REQUEST_ID, requestID);
			mediaInfo.put(MMAPIConstants.JSON_KEY_REQUEST_TYPE, requestType);
			mediaInfo.put(MMAPIConstants.JSON_KEY_CONTENT_TYPE, contentType);
			mediaInfo.put(MMAPIConstants.JSON_KEY_MEDIA_DATA, mediaData);
			
			HttpPost httppost = new HttpPost(AnswerRequestURL);
			// add header
			httppost.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httppost.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httppost.setHeader(MMAPIConstants.KEY_USER, emailAddress);
			httppost.setHeader(MMAPIConstants.KEY_AUTH, password);
			
			// might cause outofmemory
			try {
				StringEntity stringEntity = new StringEntity(mediaInfo.toString());
				httppost.setEntity(stringEntity);
				new MMPostAsyncTask(mmCallback).execute(httppost);
				
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
					mediaInfo = null;
					
					// try to write small piece of data into bufferedwriter
					bw.write("{");
					bw.write("\"" + MMAPIConstants.JSON_KEY_REQUEST_ID + "\":\"" + requestID + "\",");
					bw.write("\"" + MMAPIConstants.JSON_KEY_REQUEST_TYPE + "\":" + requestType + ",");
					bw.write("\"" + MMAPIConstants.JSON_KEY_CONTENT_TYPE + "\":\"" + contentType + "\",");
					// divide mediaData into smaller pieces
					bw.write("\"" + MMAPIConstants.JSON_KEY_MEDIA_DATA + "\":\"");
					
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
					FileEntity reqEntity = new FileEntity(tempfile, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			        httppost.setEntity(reqEntity);
//			        
//			        // delete tempfile
//			        tempfile.delete();
			        
			        new MMPostAsyncTask(mmCallback).execute(httppost);
			        
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
}
