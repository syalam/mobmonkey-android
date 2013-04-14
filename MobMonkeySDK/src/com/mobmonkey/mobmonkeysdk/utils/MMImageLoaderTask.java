package com.mobmonkey.mobmonkeysdk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * @author Dezapp, LLC
 *
 */
public class MMImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
	private MMCallback callback;
	
	public MMImageLoaderTask(MMCallback callback) {
		this.callback = callback;
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap image = null;
		try {
			URL imageUrl = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		callback.processCallback(result);
	}
}
