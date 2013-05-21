package com.mobmonkey.mobmonkeyandroid;

import java.io.File;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMMediaArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMMediaItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMImageOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMShareMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMVideoPlayOnClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
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
	
	private int mediaType;
	
	private LinkedList<MMMediaItem> mmStreamMediaItems;
	private LinkedList<MMMediaItem> mmVideoMediaItems;
	private LinkedList<MMMediaItem> mmImageMediaItems;
	
	MMMediaArrayAdapter streamAdapter;
	MMMediaArrayAdapter videoAdapter;
	MMMediaArrayAdapter imageAdapter;
	
	private boolean retrieveStreamMedia;
	private boolean retrieveVideoMedia;
	private boolean retrieveImageMedia;
	
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
		
		mediaType = getIntent().getIntExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE, MMSDKConstants.DEFAULT_INT);
		
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
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
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

	/**
	 * 
	 * @throws JSONException
	 */
	private void getMediaUrls() throws JSONException {
		streamMediaUrls = new JSONArray(getIntent().getStringExtra(MMSDKConstants.MEDIA_LIVESTREAMING));
		videoMediaUrls = new JSONArray(getIntent().getStringExtra(MMSDKConstants.MEDIA_VIDEO));
		imageMediaUrls = new JSONArray(getIntent().getStringExtra(MMSDKConstants.MEDIA_IMAGE));
		if(mediaType == MMSDKConstants.MEDIA_TYPE_LIVESTREAMING) {
			rbStream.setChecked(true);
		} else if(mediaType == MMSDKConstants.MEDIA_TYPE_VIDEO) {
			rbVideo.setChecked(true);
		} else if(mediaType == MMSDKConstants.MEDIA_TYPE_IMAGE) {
			rbImage.setChecked(true);
		}
	}
	
	/**
	 * 
	 */
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
	
	/**
	 * 
	 */
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
	
	/**
	 * 
	 */
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
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getStreamMediaItems() throws JSONException {
		if(retrieveStreamMedia) {
			for(int i = 0; i < streamMediaUrls.length(); i++) {
				JSONObject jObj = streamMediaUrls.getJSONObject(i);
				MMMediaItem mmMediaItem = new MMMediaItem();
				MMImageLoaderAdapter.loadImage(new LoadStreamThumbnailCallback(i),
											   jObj.getString(MMSDKConstants.JSON_KEY_THUMB_URL),
											   this.getApplicationContext());
				if(i == streamMediaUrls.length() - 1) {
					retrieveStreamMedia = false;
				}
				mmMediaItem.setExpiryDate(MMUtility.getExpiryDate(System.currentTimeMillis() - jObj.getLong(MMSDKConstants.JSON_KEY_UPLOADED_DATE)));
				mmMediaItem.setIsVideo(true);
				mmMediaItem.setPlayOnClickListener(new MMVideoPlayOnClickListener(LocationDetailsMediaScreen. this, jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
				mmMediaItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(LocationDetailsMediaScreen.this));
				mmStreamMediaItems.add(mmMediaItem);
			}
			streamAdapter = new MMMediaArrayAdapter(LocationDetailsMediaScreen.this, R.layout.listview_row_media, mmStreamMediaItems);
			lvStreamMedia.setAdapter(streamAdapter);
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getVideoMediaItems() throws JSONException {
		if(retrieveVideoMedia) {
			for(int i = 0; i < videoMediaUrls.length(); i++) {
				JSONObject jObj = videoMediaUrls.getJSONObject(i);
				MMMediaItem mmMediaItem = new MMMediaItem();
				MMImageLoaderAdapter.loadImage(new LoadVideoThumbnailCallback(i),
											   jObj.getString(MMSDKConstants.JSON_KEY_THUMB_URL),
											   this.getApplicationContext());
				if(i == videoMediaUrls.length() - 1) {
					retrieveVideoMedia = false;
				}
				mmMediaItem.setExpiryDate(MMUtility.getExpiryDate(System.currentTimeMillis() - jObj.getLong(MMSDKConstants.JSON_KEY_UPLOADED_DATE)));
				mmMediaItem.setIsVideo(true);
				mmMediaItem.setPlayOnClickListener(new MMVideoPlayOnClickListener(LocationDetailsMediaScreen.this, jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
				mmMediaItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(LocationDetailsMediaScreen.this));
				mmVideoMediaItems.add(mmMediaItem);
			}
			videoAdapter = new MMMediaArrayAdapter(LocationDetailsMediaScreen.this, R.layout.listview_row_media, mmVideoMediaItems);
			lvVideoMedia.setAdapter(videoAdapter);
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getImageMediaItems() throws JSONException {
		if(retrieveImageMedia) {
			for(int i = 0; i < imageMediaUrls.length(); i++) {
				JSONObject jObj = imageMediaUrls.getJSONObject(i);
				MMImageLoaderAdapter.loadImage(new LoadImageCallback(i), jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL), this.getApplicationContext());
				MMMediaItem mmMediaItem = new MMMediaItem();
				if(i == imageMediaUrls.length() - 1) {
					retrieveImageMedia = false;
				}
				mmMediaItem.setExpiryDate(MMUtility.getExpiryDate(System.currentTimeMillis() - jObj.getLong(MMSDKConstants.JSON_KEY_UPLOADED_DATE)));
				mmMediaItem.setIsImage(true);
				mmMediaItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(LocationDetailsMediaScreen.this));
				mmImageMediaItems.add(mmMediaItem);
			}
			imageAdapter = new MMMediaArrayAdapter(LocationDetailsMediaScreen.this, R.layout.listview_row_media, mmImageMediaItems);
			lvImageMedia.setAdapter(imageAdapter);
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class LoadStreamThumbnailCallback implements MMCallback {
		int mediaPosition;
		
		public LoadStreamThumbnailCallback(int mediaPosition) {
			this.mediaPosition = mediaPosition;
		}
		
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(LocationDetailsMediaScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					}
				} else if(obj instanceof Bitmap) {
					mmStreamMediaItems.get(mediaPosition).setImageMedia(ThumbnailUtils.extractThumbnail((Bitmap) obj,
																		MMUtility.getImageMediaMeasuredWidth(LocationDetailsMediaScreen.this),
																		MMUtility.getImageMediaMeasuredHeight(LocationDetailsMediaScreen.this)));
					mmStreamMediaItems.get(mediaPosition).setImageOnClickListener(new MMImageOnClickListener(LocationDetailsMediaScreen.this, (Bitmap) obj));
					streamAdapter.notifyDataSetChanged();
				}
			}
		}	
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class LoadVideoThumbnailCallback implements MMCallback {
		int mediaPosition;
		
		public LoadVideoThumbnailCallback(int mediaPosition) {
			this.mediaPosition = mediaPosition;
		}
		
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(LocationDetailsMediaScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					}
				} else if(obj instanceof Bitmap) {
					mmVideoMediaItems.get(mediaPosition).setImageMedia(ThumbnailUtils.extractThumbnail((Bitmap) obj,
																	   MMUtility.getImageMediaMeasuredWidth(LocationDetailsMediaScreen.this),
																	   MMUtility.getImageMediaMeasuredHeight(LocationDetailsMediaScreen.this)));
					mmVideoMediaItems.get(mediaPosition).setImageOnClickListener(new MMImageOnClickListener(LocationDetailsMediaScreen.this, (Bitmap) obj));
					videoAdapter.notifyDataSetChanged();
				}
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
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(LocationDetailsMediaScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					}
				} else if(obj instanceof Bitmap) {
					mmImageMediaItems.get(mediaPosition).setImageMedia(ThumbnailUtils.extractThumbnail((Bitmap) obj,
																	   MMUtility.getImageMediaMeasuredWidth(LocationDetailsMediaScreen.this),
																	   MMUtility.getImageMediaMeasuredHeight(LocationDetailsMediaScreen.this)));
					mmImageMediaItems.get(mediaPosition).setImageOnClickListener(new MMImageOnClickListener(LocationDetailsMediaScreen.this, (Bitmap) obj));
					imageAdapter.notifyDataSetChanged();
				}
			}
		}
	}
}
