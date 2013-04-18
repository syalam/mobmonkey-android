package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.net.Uri;

public class MMAnsweredRequestItem {

	public String title, time;
	public int mediaType;
	public Uri mediaUri;
	public boolean isFulfilled;
	
	public MMAnsweredRequestItem() {
		super();
	}
	
	public MMAnsweredRequestItem(String title, Uri mediaUri, String time, int mediaType, boolean isFulfilled) throws IOException {
		super();
		this.title = title;
		this.mediaUri = mediaUri;
		this.time = time;
		this.isFulfilled = isFulfilled;
		
		this.mediaType = mediaType;
	}
}
