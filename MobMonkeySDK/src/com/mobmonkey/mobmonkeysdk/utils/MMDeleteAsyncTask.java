package com.mobmonkey.mobmonkeysdk.utils;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;


public class MMDeleteAsyncTask extends AsyncTask<HttpDelete, Void, String>{

	private StringBuilder stringBuilder;
	private MMCallback mmCallback;
	
	public MMDeleteAsyncTask(MMCallback mmCallback) {
		this.mmCallback = mmCallback;
	}
	
	@Override
	protected String doInBackground(HttpDelete... http) {
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			
			HttpResponse httpResponse = httpClient.execute(http[0]);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inStream = httpEntity.getContent();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"), 8);
			String line = MMSDKConstants.DEFAULT_STRING_EMPTY;
			stringBuilder = new StringBuilder();
			while((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + MMSDKConstants.DEFAULT_STRING_NEWLINE);
			}
			inStream.close();
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stringBuilder.toString();
		
	}

	@Override
	protected void onPostExecute(String result) {
		mmCallback.processCallback(result);
		super.onPostExecute(result);
	}
	
	
}