package com.mobmonkey.mobmonkeyapi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class MMGETAsyncTask extends AsyncTask<HttpGet, Void, String>{
	private StringBuilder stringBuilder;
	private MMCallback mmCallback;
	
	/**
	 * Constructor that takes in a {@link MMCallback} to be invoke after the background task is finished
	 * @param mmc
	 */
	public MMGETAsyncTask(MMCallback mmCallback) {
		this.mmCallback = mmCallback;
	}
	
	@Override
	protected String doInBackground(HttpGet... arg0) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(arg0[0]);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inStream = httpEntity.getContent();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"), 8);
			String line = MMAPIConstants.DEFAULT_STRING;
			stringBuilder = new StringBuilder();
			while((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			inStream.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}
