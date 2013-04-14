package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class MMTopviewedItem {

	public String title;
	public Bitmap imageIcon;
	
	public MMTopviewedItem() {
		super();
	}
	
	public MMTopviewedItem(String title, String imageIcon, boolean isVideo) throws IOException {
		super();
		this.title = title;
		
		if(imageIcon != null) {
			if(isVideo) {
				Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(imageIcon, MediaStore.Video.Thumbnails.MINI_KIND );
				this.imageIcon = thumbnail;
			} else {
				URL url = new URL(imageIcon);
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				this.imageIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				this.imageIcon = Bitmap.createBitmap(this.imageIcon, 
														   0, 0, 
														   this.imageIcon.getWidth(), 
														   this.imageIcon.getHeight(), 
														   matrix,
														   true);
			}
		}
	}
}
