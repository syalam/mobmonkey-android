package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;
import java.net.URL;

import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class MMTopViewedItem {

	private String title;
	private Bitmap imageMedia;
	
	public MMTopViewedItem() {
		super();
	}

	/**
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @return the imageMedia
	 */
	public Bitmap getImageMedia() {
		return imageMedia;
	}

	/**
	 * 
	 * @param imageMedia the imageMedia to set
	 */
	public void setImageMedia(Bitmap imageMedia) {
		this.imageMedia = imageMedia;
	}
}
