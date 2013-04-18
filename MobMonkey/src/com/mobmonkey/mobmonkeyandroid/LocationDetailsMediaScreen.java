package com.mobmonkey.mobmonkeyandroid;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.utils.MMMediaArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMMediaItem;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * @author Dezapp, LLC
 *
 */
public class LocationDetailsMediaScreen extends Activity implements OnCheckedChangeListener {
	private static final String TAG = "LocationDetailsMediaScreen";
	
	private RadioGroup rgMedia;
	private RadioButton rbStream;
	private RadioButton rbVideo;
	private RadioButton rbImage;
	private ListView lvStreamMedia;
	private ListView lvVideoMedia;
	private ListView lvImageMedia;
	
	private String mediaType;
	private int mediaWidth;
	private int mediaHeight;
	
	private LinkedList<MMMediaItem> mmStreamMediaItems;
	private LinkedList<MMMediaItem> mmVideoMediaItems;
	private LinkedList<MMMediaItem> mmImageMediaItems;
	
	private boolean retrieveStreamMedia;
	private boolean retrieveVideoMedia;
	private boolean retrieveImageMedia;
	private boolean lastImageMedia;
	
	private JSONArray streamMediaUrls;
	private JSONArray videoMediaUrls;
	private JSONArray imageMediaUrls;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.location_details_media_screen);
		
		rgMedia = (RadioGroup) findViewById(R.id.rgmedia);
		rbStream = (RadioButton) findViewById(R.id.rbstreammedia);
		rbVideo = (RadioButton) findViewById(R.id.rbvideomedia);
		rbImage = (RadioButton) findViewById(R.id.rbimagemedia);
		lvStreamMedia = (ListView) findViewById(R.id.lvstreammedia);
		lvVideoMedia = (ListView) findViewById(R.id.lvvideomedia);
		lvImageMedia = (ListView) findViewById(R.id.lvimagemedia);
		
		mediaType = getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_MEDIA_TYPE);
		mediaWidth = getIntent().getIntExtra(MMAPIConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_WIDTH, MMAPIConstants.DEFAULT_INT);
		mediaHeight = getIntent().getIntExtra(MMAPIConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_HEIGHT, MMAPIConstants.DEFAULT_INT);
		
		mmStreamMediaItems = new LinkedList<MMMediaItem>();
		mmVideoMediaItems = new LinkedList<MMMediaItem>();
		mmImageMediaItems = new LinkedList<MMMediaItem>();
		
		retrieveStreamMedia = true;
		retrieveVideoMedia = true;
		retrieveImageMedia = true;
		
		rgMedia.setOnCheckedChangeListener(LocationDetailsMediaScreen.this);
		
		try {
			getMediaUrls();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
			case R.id.rbstreammedia:
				rbStreamChecked();
				break;
			case R.id.rbvideomedia:
				rbVideoChecked();
				break;
			case R.id.rbimagemedia:
				rbImageChecked();
				break;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}

	private void getMediaUrls() throws JSONException {
		streamMediaUrls = new JSONArray(getIntent().getStringExtra(MMAPIConstants.MEDIA_TYPE_LIVESTREAMING));
		videoMediaUrls = new JSONArray(getIntent().getStringExtra(MMAPIConstants.MEDIA_TYPE_VIDEO));
		imageMediaUrls = new JSONArray(getIntent().getStringExtra(MMAPIConstants.MEDIA_TYPE_IMAGE));
		if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_LIVESTREAMING)) {
			rbStream.setChecked(true);
		} else if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_VIDEO)) {
			rbVideo.setChecked(true);
		} else if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_IMAGE)) {
			rbImage.setChecked(true);
		}
	}
	
	private void rbStreamChecked() {
		lvStreamMedia.setVisibility(View.VISIBLE);
		lvVideoMedia.setVisibility(View.INVISIBLE);
		lvImageMedia.setVisibility(View.INVISIBLE);
		try {
			getStreamMediaItems();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void rbVideoChecked() {
		lvStreamMedia.setVisibility(View.INVISIBLE);
		lvVideoMedia.setVisibility(View.VISIBLE);
		lvImageMedia.setVisibility(View.INVISIBLE);
		try {
			getVideoMediaItems();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void rbImageChecked() {
		lvStreamMedia.setVisibility(View.INVISIBLE);
		lvVideoMedia.setVisibility(View.INVISIBLE);
		lvImageMedia.setVisibility(View.VISIBLE);
		try {
			getImageMediaItems();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void getStreamMediaItems() throws JSONException {
		if(retrieveStreamMedia) {
			for(int i = 0; i < streamMediaUrls.length(); i++) {
				JSONObject jObj = streamMediaUrls.getJSONObject(i);
				MMMediaItem mmMediaItem = new MMMediaItem();
				// TODO: load thumbnails for videos
				mmMediaItem.setIsVideo(true);
				mmMediaItem.setPlayOnClickListener(new PlayOnClickListener(jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL)));
				mmMediaItem.setShareMediaOnClickListener(new ShareMediaOnClickListener());
				mmStreamMediaItems.add(mmMediaItem);
			}
		}
	}
	
	private void getVideoMediaItems() throws JSONException {
		if(retrieveVideoMedia) {
			for(int i = 0; i < videoMediaUrls.length(); i++) {
				JSONObject jObj = videoMediaUrls.getJSONObject(i);
				MMMediaItem mmMediaItem = new MMMediaItem();
				// TODO: load thumbnails for videos
				mmMediaItem.setIsVideo(true);
				mmMediaItem.setPlayOnClickListener(new PlayOnClickListener(jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL)));
				mmMediaItem.setShareMediaOnClickListener(new ShareMediaOnClickListener());
				mmVideoMediaItems.add(mmMediaItem);
			}
		}
	}
	
	private void getImageMediaItems() throws JSONException {
		if(retrieveImageMedia) {
			for(int i = 0; i < imageMediaUrls.length(); i++) {
				JSONObject jObj = imageMediaUrls.getJSONObject(i);
				MMMediaItem mmMediaItem = new MMMediaItem();
				if(i == imageMediaUrls.length() - 1) {
					lastImageMedia = true;
				}
				MMImageLoaderAdapter.loadImage(new LoadImageCallback(i), jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
				mmMediaItem.setIsImage(true);
				mmMediaItem.setShareMediaOnClickListener(new ShareMediaOnClickListener());
				mmImageMediaItems.add(mmMediaItem);
			}
		}
	}
	
	/**
	 * Callback to display the image it retrieve from the mediaurl
	 * @author Dezapp, LLC
	 *
	 */
	private class LoadImageCallback implements MMCallback {
		int mediaPosition;
		
		public LoadImageCallback(int mediaPosition) {
			this.mediaPosition = mediaPosition;
		}
		
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				mmImageMediaItems.get(mediaPosition).setImageMedia(ThumbnailUtils.extractThumbnail((Bitmap) obj, mediaWidth, mediaHeight));
				mmImageMediaItems.get(mediaPosition).setImageOnClickListener(new ImageOnClickListener((Bitmap) obj));
				if(lastImageMedia) {
					MMMediaArrayAdapter adapter = new MMMediaArrayAdapter(LocationDetailsMediaScreen.this, R.layout.media_list_row, mmImageMediaItems);
					lvImageMedia.setAdapter(adapter);
					retrieveImageMedia = false;
				}
			}
		}
	}
	
	private class PlayOnClickListener implements OnClickListener {
		private String videoUrl;
		
		public PlayOnClickListener(String videoUrl) {
			this.videoUrl = videoUrl;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(LocationDetailsMediaScreen.this, MMVideoPlayerScreen.class);
			intent.putExtra(MMAPIConstants.JSON_KEY_MEDIA_URL, videoUrl);
			startActivity(intent);
		}
	}
	
	private class ImageOnClickListener implements OnClickListener {
		Bitmap imageMedia;
		
		public ImageOnClickListener(Bitmap imageMedia) {
			this.imageMedia = imageMedia;
		}
		
		@Override
		public void onClick(View v) {
			// TODO: start a dialog activity to display the full image
			Intent intent = new Intent(LocationDetailsMediaScreen.this, ExpandedThumbnailScreen.class);
			intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_IMAGE_MEDIA, imageMedia);
			startActivity(intent);
		}		
	}
	
	private class ShareMediaOnClickListener implements OnClickListener {
		public ShareMediaOnClickListener() {
			
		}
		
		@Override
		public void onClick(View v) {
			
		}
	}
}
