package com.mobmonkey.mobmonkey.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class MMTopviewedItem {

	public String title;
	public Bitmap imageIcon;
	
	public MMTopviewedItem() {
		super();
	}
	
	public MMTopviewedItem(String title, String imageIcon) throws IOException {
		super();
		this.title = title;
		
		if(imageIcon != null) {
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
