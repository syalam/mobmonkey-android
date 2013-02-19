package com.mobmonkey.mobmonkeyapi.adapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMGETAsyncTask;

public class MMCategoriesAdapter {
	private static final String TAG = "TAG";
	private static String subCategoryURL;
	
	public static void getSubCategoryListWithCategoryID(MMCallback mmCallback, int categoryID, String partnerId, String user, String auth)
	{
		subCategoryURL = MMAPIConstants.MOBMONKEY_URL + "/category?categoryId=" + categoryID;
		
		try 
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(subCategoryURL);			
			httpGet.setHeader(MMAPIConstants.KEY_CONTENT_TYPE, MMAPIConstants.CONTENT_TYPE_APP_JSON);
			httpGet.setHeader(MMAPIConstants.KEY_PARTNER_ID, partnerId);
			httpGet.setHeader(MMAPIConstants.KEY_USER, user);
			httpGet.setHeader(MMAPIConstants.KEY_AUTH, auth);
			
			HttpResponse response = httpclient.execute(httpGet);
			
			
			// Get hold of the response entity
	        HttpEntity entity = response.getEntity();
	        // If the response does not enclose an entity, there is no need
	        // to worry about connection release

	        if (entity != null) {

	            // A Simple JSON Response Read
	            InputStream instream = entity.getContent();
	            String result= convertStreamToString(instream);
	            
				Log.d(TAG, TAG + " RESPONSE: " + result);

	            // now you have the string representation of the HTML request
	            instream.close();
	        }
			
			new MMGETAsyncTask(mmCallback).execute(httpGet);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
		
		private static String convertStreamToString(InputStream is) {
		    /*
		     * To convert the InputStream to String we use the BufferedReader.readLine()
		     * method. We iterate until the BufferedReader return null which means
		     * there's no more data to read. Each line will appended to a StringBuilder
		     * and returned as String.
		     */
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
	}
}
