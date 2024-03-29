package com.mobmonkey.mobmonkeysdk.asynctasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import org.apache.http.conn.ConnectTimeoutException;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.Display;

/**
 * @author Dezapp, LLC
 *
 */
public class MMLoadImageAsyncTask extends AsyncTask<String, Void, Object> {
	private MMCallback callback;
	private Display display;
	
	public MMLoadImageAsyncTask(MMCallback callback, Display display) {
		this.callback = callback;
		this.display = display;
	}
	
	@Override
	protected Object doInBackground(String... params) {
		Bitmap image = null;
		try {
			URL imageUrl = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(MMSDKConstants.TIMEOUT_CONNECTION);
			conn.setReadTimeout(MMSDKConstants.TIMEOUT_SOCKET);
			conn.setDoInput(true);
			conn.connect();
			
			if(conn.getContentLength() > 0) {
				InputStream is = conn.getInputStream();
				image = BitmapFactory.decodeStream(is);
				
				is.close();
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
		
		return Bitmap.createScaledBitmap(image, display.getWidth(), display.getHeight(), true);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		callback.processCallback(result);
	}
}
