package com.mobmonkey.mobmonkeyandroid.listeners;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageDownloaderAdapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMImageOnClickListener implements OnClickListener {
	private Context context;
	private Bitmap imageMedia;
	private Drawable imageDrawable;
	private String imageUrl;
	
	public MMImageOnClickListener(Context context, Bitmap imageMedia) {
		this.context = context;
		this.imageMedia = imageMedia;
	}
	
	public MMImageOnClickListener(Context context, String imageUrl) {
		this.context = context;
		this.imageUrl = imageUrl;
	}
	
	public MMImageOnClickListener(Context context, Drawable imageDrawable) {
		this.context = context;
		this.imageDrawable = imageDrawable;
	}
	
	@Override
	public void onClick(View v) {
		final Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.media_image_fullscreen, null);
		ImageView ivMediaImage = (ImageView) view.findViewById(R.id.ivmediaimage);
		ImageButton ibCancel = (ImageButton) view.findViewById(R.id.ibcancel);
		
//		ivMediaImage.setImageDrawable(imageDrawable);
		ivMediaImage.setImageBitmap(imageMedia);
//		ivExpandedThumbnail.setImageBitmap(MMImageDownloaderAdapter.getBitmapFromCache(imageUrl));
		ibCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setContentView(view);
		dialog.show();
	}	
}
