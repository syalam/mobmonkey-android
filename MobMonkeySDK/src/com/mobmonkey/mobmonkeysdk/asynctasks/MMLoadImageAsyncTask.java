package com.mobmonkey.mobmonkeysdk.asynctasks;

import java.io.ByteArrayOutputStream;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Dezapp, LLC
 *
 */
public class MMLoadImageAsyncTask extends AsyncTask<String, Void, Object> {
	private MMCallback callback;
	private Context context;
	
	public MMLoadImageAsyncTask(MMCallback callback, Context context) {
		this.callback = callback;
		this.context = context;
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
				BitmapFactory.Options options = new BitmapFactory.Options();
				//options.inSampleSize = 4;
				//options.inJustDecodeBounds = false;
				options.inPreferQualityOverSpeed = true;
				image = BitmapFactory.decodeStream(is, null, options);
				
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
		
		image = scaleDownBitmap(image);
		//image = codec(image, Bitmap.CompressFormat.PNG, 0);
		
		return image;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		callback.processCallback(result);
	}
	
	private Bitmap scaleDownBitmap(Bitmap imageMedia) {
		float densityMultiplier = context.getResources().getDisplayMetrics().density / 6;
		int h, w;
		if(imageMedia.getHeight() > imageMedia.getWidth()) {
			int newMax = context.getResources().getDisplayMetrics().heightPixels;
			h = (int) (newMax * densityMultiplier);
			w = (int) (h * imageMedia.getWidth() / ((double) imageMedia.getHeight()));
		} else {
			int newMax = context.getResources().getDisplayMetrics().widthPixels;
			w = (int) (newMax * densityMultiplier);
			h = (int) (w * imageMedia.getHeight() / ((double) imageMedia.getWidth()));
		}
		
		return Bitmap.createScaledBitmap(imageMedia, w, h, true);
	}
}
