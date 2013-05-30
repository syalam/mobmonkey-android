package com.mobmonkey.mobmonkeysdk.asynctasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.os.AsyncTask;

/**
 * Custom {@link AsyncTask} for MobMonkey to do a {@link HttpPost} to the MobMonkey server as a background task on the Android device
 * @author Dezapp, LLC
 *
 */
public class MMPostAsyncTask extends AsyncTask<HttpPost, Void, String> {
	private StringBuilder stringBuilder;
	private MMCallback mmCallback;
	
	/**
	 * Constructor that takes in a {@link MMCallback} to be invoke after the background task is finished
	 * @param mmc
	 */
	public MMPostAsyncTask(MMCallback mmCallback) {
		this.mmCallback = mmCallback;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(HttpPost... params) {
		stringBuilder = new StringBuilder();
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, MMSDKConstants.TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(httpParams, MMSDKConstants.TIMEOUT_CONNECTION);
			ConnManagerParams.setTimeout(httpParams, MMSDKConstants.TIMEOUT_CONNECTION);
			
			HttpResponse httpResponse = httpClient.execute(params[0]);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inStream = httpEntity.getContent();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"), 8);
			String line = MMSDKConstants.DEFAULT_STRING_EMPTY;
			while((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + MMSDKConstants.DEFAULT_STRING_NEWLINE);
			}
			inStream.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return MMSDKConstants.CONNECTION_TIMED_OUT;
		} catch (SocketException e) {
			e.printStackTrace();
			if(e.getMessage().equals(MMSDKConstants.OPERATION_TIMED_OUT)) {
				return MMSDKConstants.CONNECTION_TIMED_OUT;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stringBuilder.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		if(mmCallback != null) {
			mmCallback.processCallback(result);
		}
		super.onPostExecute(result);
	}
}