package com.mobmonkey.mobmonkeyandroid;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.listeners.MMImageOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMShareMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMVideoPlayOnClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMMediaArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMMediaItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
		
		mediaType = getIntent().getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE);
		mediaWidth = getIntent().getIntExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_WIDTH, MMSDKConstants.DEFAULT_INT);
		mediaHeight = getIntent().getIntExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_HEIGHT, MMSDKConstants.DEFAULT_INT);
		
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
		streamMediaUrls = new JSONArray(getIntent().getStringExtra(MMSDKConstants.MEDIA_TYPE_LIVESTREAMING));
		videoMediaUrls = new JSONArray(getIntent().getStringExtra(MMSDKConstants.MEDIA_TYPE_VIDEO));
		imageMediaUrls = new JSONArray(getIntent().getStringExtra(MMSDKConstants.MEDIA_TYPE_IMAGE));
		if(mediaType.equals(MMSDKConstants.MEDIA_TYPE_LIVESTREAMING)) {
			rbStream.setChecked(true);
		} else if(mediaType.equals(MMSDKConstants.MEDIA_TYPE_VIDEO)) {
			rbVideo.setChecked(true);
		} else if(mediaType.equals(MMSDKConstants.MEDIA_TYPE_IMAGE)) {
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
				mmMediaItem.setPlayOnClickListener(new MMVideoPlayOnClickListener(LocationDetailsMediaScreen. this, jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
				mmMediaItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(LocationDetailsMediaScreen.this));
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
				mmMediaItem.setPlayOnClickListener(new MMVideoPlayOnClickListener(LocationDetailsMediaScreen.this, jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
				mmMediaItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(LocationDetailsMediaScreen.this));
				mmVideoMediaItems.add(mmMediaItem);
			}
		}
	}
	
	private void getImageMediaItems() throws JSONException {
		if(retrieveImageMedia) {
			for(int i = 0; i < imageMediaUrls.length(); i++) {
				JSONObject jObj = imageMediaUrls.getJSONObject(i);
				MMImageLoaderAdapter.loadImage(new LoadImageCallback(i), jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL));
				MMMediaItem mmMediaItem = new MMMediaItem();
				if(i == imageMediaUrls.length() - 1) {
					lastImageMedia = true;
				}
				mmMediaItem.setExpiryDate(MMUtility.getDate(System.currentTimeMillis() - jObj.getLong(MMSDKConstants.JSON_KEY_EXPIRY_DATE), "mm") + "m");
				mmMediaItem.setIsImage(true);
				mmMediaItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(LocationDetailsMediaScreen.this));
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
				mmImageMediaItems.get(mediaPosition).setImageOnClickListener(new MMImageOnClickListener(LocationDetailsMediaScreen.this, (Bitmap) obj));
				if(lastImageMedia) {
					MMMediaArrayAdapter adapter = new MMMediaArrayAdapter(LocationDetailsMediaScreen.this, R.layout.media_list_row, mmImageMediaItems);
					lvImageMedia.setAdapter(adapter);
					retrieveImageMedia = false;
				}
			}
		}
	}
}
