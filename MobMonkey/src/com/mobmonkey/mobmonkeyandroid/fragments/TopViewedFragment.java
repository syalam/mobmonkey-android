package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMTopViewedArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMTopViewedItem;
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
	
	private ListView lvtopviewed;
	private JSONArray topViewed;
	private LinkedList<MMTopViewedItem> topViewedItems;
	private SharedPreferences userPrefs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		View view = inflater.inflate(R.layout.fragment_topviewed_screen, container, false);
		lvtopviewed = (ListView) view.findViewById(R.id.lvtopviewed);
		topViewedItems = new LinkedList<MMTopViewedItem>();
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
			MMProgressDialog.displayDialog(getActivity(), MMSDKConstants.DEFAULT_STRING_EMPTY, "Loading all top viewed...");
			MMTrendingAdapter.getTopViewed(new TopViewedCallback(),
												   MMSDKConstants.SEARCH_TIME_WEEK,
												   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
												   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY),
												   MMConstants.PARTNER_ID);
		}
	}
	
	private void getMediaForAllLocation() {
		try {
			for(int i = 0; i < topViewed.length(); i++) {
				JSONObject jObj = topViewed.getJSONObject(i);
				JSONObject jObjMedia = jObj.getJSONObject(MMSDKConstants.JSON_KEY_MEDIA);
				if(jObjMedia.getString(MMSDKConstants.JSON_KEY_TYPE).equals(MMSDKConstants.MEDIA_TYPE_IMAGE)) {
					MMImageLoaderAdapter.loadImage(new LoadImageCallback(i), jObjMedia.getString(MMSDKConstants.JSON_KEY_MEDIA_URL));
				} else if(jObjMedia.getString(MMSDKConstants.JSON_KEY_TYPE).equals(MMSDKConstants.MEDIA_TYPE_VIDEO)) {
					// TODO: create thumbnail from video
				}
				
				topViewedItems.add(new MMTopViewedItem());
				topViewedItems.get(i).setTitle(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
			}
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
			
			if(obj != null) {
				Log.d(TAG, TAG + "topViewed: " + ((String) obj));
				try {
					topViewed = new JSONArray((String) obj);
					Log.d(TAG, TAG + topViewed.toString());
					getMediaForAllLocation();
				} catch (JSONException e) {
					e.printStackTrace();
					// TODO: display custom dialog informing user there is an error occurred while loading topview locations
//					MMDialog.displayCustomDialog(getActivity(), customProgressDialog);
					getActivity().onBackPressed();
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
	private class LoadImageCallback implements MMCallback {
		int topViewedLocation;
		
		public LoadImageCallback(int topViewedLocation) {
			this.topViewedLocation = topViewedLocation;
		}
		
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				topViewedItems.get(topViewedLocation).setImageMedia((Bitmap) obj);
				MMTopViewedArrayAdapter adapter = new MMTopViewedArrayAdapter(getActivity(), R.layout.top_viewed_listview_row, topViewedItems);
				lvtopviewed.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
