package com.mobmonkey.mobmonkeyandroid.arrayadaptersitems;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public class MMAnsweredRequestItem {

	public String title, time;
	public int mediaType;
	public Uri mediaUri;
	public boolean isAccepted;
	public Context context;
	
	public MMAnsweredRequestItem() {
		super();
	}
	
	public MMAnsweredRequestItem(String title, Uri mediaUri, String time, int mediaType, boolean isAccepted, Context context) throws IOException {
		super();
		this.title = title;
		this.mediaUri = mediaUri;
		this.time = time;
		this.isAccepted = isAccepted;
		
		this.mediaType = mediaType;
		this.context = context;
	}
}
