package com.mobmonkey.mobmonkeysdk.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Dezapp, LLC
 *
 */
public class MMGetVideoThumbnailTask extends AsyncTask<Uri, Void, Bitmap> {
	private static final String TAG = "MMGetVideoThumbnailTask";
	
	private Context context;
	private MMCallback mmCallback;
	
	public MMGetVideoThumbnailTask(Context context, MMCallback mmCallback) {
		this.context = context;
		this.mmCallback = mmCallback;
	}
	
	@Override
	protected Bitmap doInBackground(Uri... params) {
		Bitmap videoThumbnail = null;
		
		InputStream is;
		
		try {
			is = context.getContentResolver().openInputStream(params[0]);
			videoThumbnail = BitmapFactory.decodeStream(is);
			Log.d(TAG, "videoThumbnail: " + videoThumbnail);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return videoThumbnail;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		mmCallback.processCallback(result);
	}
}
