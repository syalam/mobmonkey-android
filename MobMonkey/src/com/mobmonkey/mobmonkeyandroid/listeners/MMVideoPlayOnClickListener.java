package com.mobmonkey.mobmonkeyandroid.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobmonkey.mobmonkeyandroid.VideoPlayerScreen;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class MMVideoPlayOnClickListener implements OnClickListener {
	Context context;
	private String videoUrl;
	
	public MMVideoPlayOnClickListener(Context context, String videoUrl) {
		this.context = context;
		this.videoUrl = videoUrl;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(context, VideoPlayerScreen.class);
		intent.putExtra(MMSDKConstants.JSON_KEY_MEDIA_URL, videoUrl);
		context.startActivity(intent);
	}
}
