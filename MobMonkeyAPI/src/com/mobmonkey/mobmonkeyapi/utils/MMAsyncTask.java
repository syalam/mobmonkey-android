/**
 * 
 */
package com.mobmonkey.mobmonkeyapi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

/**
 * @author Dezapp, LLC
 *
 */
public class MMAsyncTask extends AsyncTask<HttpPost, Void, String> {
	StringBuilder stringBuilder;
	MMCallback mmCallback;
	
	public MMAsyncTask(MMCallback mmc) {
		mmCallback = mmc;
	}
	
	@Override
	protected String doInBackground(HttpPost... params) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(params[0]);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inStream = httpEntity.getContent();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"), 8);
			String line = MMConstants.DEFAULT_STRING;
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

	@Override
	protected void onPostExecute(String result) {
		mmCallback.processCallback(result);
		super.onPostExecute(result);
	}
}