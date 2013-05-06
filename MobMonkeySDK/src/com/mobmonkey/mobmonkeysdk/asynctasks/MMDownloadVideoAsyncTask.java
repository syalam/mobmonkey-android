package com.mobmonkey.mobmonkeysdk.asynctasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import org.apache.http.conn.ConnectTimeoutException;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class MMDownloadVideoAsyncTask extends AsyncTask<String, Void, Object>{
	private static final String TAG = "MMVideoDownloaderTask";
	
	private MMCallback mmCallback;
	
	public MMDownloadVideoAsyncTask(MMCallback mmCallback) {
		this.mmCallback = mmCallback;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Object doInBackground(String... params) {
//		android.os.Debug.waitForDebugger();
		String videoPath = null;
		
		try {
			URL videoUrl = new URL(params[0]);
			String videoExt = MMSDKConstants.DEFAULT_STRING_EMPTY;
			int i = videoUrl.getFile().lastIndexOf('.');
			if (i > 0) {
			    videoExt = videoUrl.getFile().substring(i);
			}
			long startTime = System.currentTimeMillis();	
			Log.i(TAG, "video download beginning");			
			
			HttpURLConnection conn = (HttpURLConnection) videoUrl.openConnection();
//			conn.setRequestMethod(HttpURLConnection.)
			conn.setConnectTimeout(MMSDKConstants.TIMEOUT_CONNECTION);
			conn.setReadTimeout(MMSDKConstants.TIMEOUT_SOCKET);
			conn.setDoInput(true);
			conn.connect();
			
			if(conn.getContentLength() > 0) {
				InputStream is = conn.getInputStream();
				BufferedInputStream isBuff = new BufferedInputStream(is, 1024 * 5);
				videoPath = Environment.getExternalStorageDirectory() + "/mmTempVideo" + params[1] + videoExt;
				File file = new File(videoPath);
				FileOutputStream outStream = new FileOutputStream(file);
				byte[] buff = new byte[5 * 1024];
				
				int len;
				while((len = isBuff.read(buff)) != -1) {
					outStream.write(buff,0,len);
				}
				
				outStream.flush();
				outStream.close();
				is.close();
				
				Log.i(TAG, "video download finished in " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");			
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
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
		
		return Uri.parse(videoPath);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override 
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		mmCallback.processCallback(result);
	}
}
