package com.mobmonkey.mobmonkeyandroid.listeners;

import com.mobmonkey.mobmonkeyandroid.ExpandedThumbnailScreen;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Dezapp, LLC
 *
 */
public class MMImageOnClickListener implements OnClickListener {
	private Context context;
	private Bitmap imageMedia;
	
	public MMImageOnClickListener(Context context, Bitmap imageMedia) {
		this.context = context;
		this.imageMedia = imageMedia;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(context, ExpandedThumbnailScreen.class);
		intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_IMAGE_MEDIA, imageMedia);
		context.startActivity(intent);
	}	

	private Bitmap scaleDownBitmap(int newHeight) {
		float densityMultiplier = context.getResources().getDisplayMetrics().density;
		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * imageMedia.getWidth() / ((double) imageMedia.getHeight()));
		
		return Bitmap.createScaledBitmap(imageMedia, w, h, true);
	}
}
