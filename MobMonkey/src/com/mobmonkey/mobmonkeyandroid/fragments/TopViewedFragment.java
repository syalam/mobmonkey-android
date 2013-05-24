package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.File;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMTopViewedArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMMediaItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMImageOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMShareMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMVideoPlayOnClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMTrendingAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * @author Dezapp, LLC
 *
 */
public class TopViewedFragment extends MMFragment {
	public static final String TAG = "TopViewedFragment: "; 
	
	private ListView lvTopViewed;
	private JSONArray topViewed;
	private MMMediaItem[] topViewedItems;
	private MMTopViewedArrayAdapter adapter;
	
	private SharedPreferences userPrefs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		View view = inflater.inflate(R.layout.fragment_topviewed_screen, container, false);
		lvTopViewed = (ListView) view.findViewById(R.id.lvtopviewed);
		getTrending();
		return view;
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	/**
	 * 
	 * @param position
	 */
	private void getTrending() {
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			MMTrendingAdapter.getTopViewed(new TopViewedCallback(),
										   MMSDKConstants.SEARCH_TIME_WEEK,
										   MMConstants.PARTNER_ID,
										   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_retrieving_all_top_viewed));
		}
	}
	
	/**
	 * 
	 */
	private void getMediaForAllLocation() {
		try {
			for(int i = 0; i < topViewed.length(); i++) {
				JSONObject jObj = topViewed.getJSONObject(i);
				topViewedItems[i] = new MMMediaItem();
				
				JSONObject jObjMedia = jObj.getJSONObject(MMSDKConstants.JSON_KEY_MEDIA);
				if(jObjMedia.getString(MMSDKConstants.JSON_KEY_TYPE).equals(MMSDKConstants.MEDIA_IMAGE)) {
					MMImageLoaderAdapter.loadImage(new LoadMediaThumbnailCallback(i),
												   getActivity().getWindowManager().getDefaultDisplay(),
												   jObjMedia.getString(MMSDKConstants.JSON_KEY_MEDIA_URL));
					topViewedItems[i].setIsImage(true);
				} else if(jObjMedia.getString(MMSDKConstants.JSON_KEY_TYPE).equals(MMSDKConstants.MEDIA_VIDEO)) {
					MMImageLoaderAdapter.loadImage(new LoadMediaThumbnailCallback(i),
												   getActivity().getWindowManager().getDefaultDisplay(),
												   jObjMedia.getString(MMSDKConstants.JSON_KEY_THUMB_URL));
					topViewedItems[i].setIsVideo(true);
					topViewedItems[i].setPlayOnClickListener(new MMVideoPlayOnClickListener(getActivity(), jObjMedia.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
				}
				
				topViewedItems[i].setLocationName(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
				topViewedItems[i].setExpiryDate(MMUtility.getExpiryDate(System.currentTimeMillis() - jObjMedia.getLong(MMSDKConstants.JSON_KEY_UPLOADED_DATE)));
				topViewedItems[i].setShareMediaOnClickListener(new MMShareMediaOnClickListener(getActivity()));
			}
			
			adapter = new MMTopViewedArrayAdapter(getActivity(), R.layout.listview_row_topviewed, topViewedItems);
			lvTopViewed.setAdapter(adapter);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class TopViewedCallback implements MMCallback {		
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			Log.d(TAG, TAG + "topViewed: " + ((String) obj));
			
			if(obj != null) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					} else {
						try {
							topViewed = new JSONArray((String) obj);
							topViewedItems = new MMMediaItem[topViewed.length()];
							Log.d(TAG, TAG + topViewed.toString());
							getMediaForAllLocation();
						} catch (JSONException e) {
							e.printStackTrace();
							// TODO: display custom dialog informing user there is an error occurred while loading topview locations
//							MMDialog.displayCustomDialog(getActivity(), customProgressDialog);
							getActivity().onBackPressed();
						}
					}
			} else {
				// TODO: display custom dialog informing user there are no topviewed locations
//				MMDialog.displayCustomDialog(getActivity(), customProgressDialog);
				getActivity().onBackPressed();
			}
		}
	}
	
	/**
	 * Callback to display the image it retrieve from the mediaurl
	 * @author Dezapp, LLC
	 *
	 */
	private class LoadMediaThumbnailCallback implements MMCallback {
		int topViewedLocation;
		
		public LoadMediaThumbnailCallback(int topViewedLocation) {
			this.topViewedLocation = topViewedLocation;
		}
		
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					}
				} else if(obj instanceof Bitmap) {
					topViewedItems[topViewedLocation].setImageMedia(ThumbnailUtils.extractThumbnail((Bitmap) obj,
																	MMUtility.getImageMediaMeasuredWidth(getActivity()),
																	MMUtility.getImageMediaMeasuredHeight(getActivity())));
					topViewedItems[topViewedLocation].setImageOnClickListener(new MMImageOnClickListener(getActivity(), (Bitmap) obj));
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}