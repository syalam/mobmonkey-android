package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMVideoPlayerScreen extends Activity {
	VideoView vvVideoPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mm_video_player_screen);
		vvVideoPlayer = (VideoView) findViewById(R.id.vvvideoplayer);
		vvVideoPlayer.setVideoURI(Uri.parse(getIntent().getStringExtra(MMAPIConstants.JSON_KEY_MEDIA_URL)));
		vvVideoPlayer.setMediaController(new MediaController(MMVideoPlayerScreen.this));
	}
}
