package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.IOException;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMTopviewedArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMTopviewedItem;
import com.mobmonkey.mobmonkeysdk.adapters.MMLocationDetailsAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMTrendingAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDialog;
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
	private LinkedList<String> mediaUrl = new LinkedList<String>();
	private LinkedList<String> mediaType = new LinkedList<String>();
	private SharedPreferences userPrefs;
//	private boolean isLoading = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_topviewed_screen, container, false);
		lvtopviewed = (ListView) view.findViewById(R.id.lvtopviewed);
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		
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
			MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING_EMPTY, "Loading all top viewed...");
			MMTrendingAdapter.getTopViewed(new TopViewedCallback(),
												   MMAPIConstants.SEARCH_TIME_WEEK,
												   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY),
												   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY),
												   MMConstants.PARTNER_ID);
		}
	}
	
	private void getMediaForAllLocation() {
		try {
			for(int i = 0; i < topViewed.length(); i++) {
				String locationID = topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_LOCATION_ID);
				String providerID = topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_PROVIDER_ID);
				
				MMLocationDetailsAdapter.retrieveAllMediaForLocation(new MediaCallBack(), 
														   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
														   userPrefs.getString(MMAPIConstants.KEY_AUTH,MMAPIConstants.DEFAULT_STRING_EMPTY),
														   MMConstants.PARTNER_ID,
														   locationID, 
														   providerID);
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}
	
	private MMTopviewedItem[] getTopViewedItems() {
		MMTopviewedItem topViewedItems[] = new MMTopviewedItem[topViewed.length()];
		try {
			//Log.d(TAG, topViewed.toString());
			for(int i = 0; i < topViewedItems.length; i++) {
				if(mediaType.get(i).equals(MMAPIConstants.MEDIA_TYPE_IMAGE)) {
					topViewedItems[i] = new MMTopviewedItem(topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_NAME), 
															mediaUrl.get(i), false);
				} else if(mediaType.get(i).equals(MMAPIConstants.MEDIA_TYPE_VIDEO)) {
					topViewedItems[i] = new MMTopviewedItem(topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_NAME), 
															mediaUrl.get(i), true);
				}
			
				Log.d(TAG, "title: " + topViewedItems[i].title);
			}
		} catch (IOException ex) {
			Log.d(TAG, "IOExcpetion at getTopViewedItems()");
			ex.printStackTrace();
		} catch (JSONException e) {
			Log.d(TAG, "JSONException at getTopViewedItems()");
			e.printStackTrace();
		}
		
		return topViewedItems;
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
	
	private class MediaCallBack implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			try {
				JSONObject jObj = new JSONObject((String)obj);
				JSONArray mediaArray = jObj.getJSONArray(MMAPIConstants.JSON_KEY_MEDIA);
				
				mediaUrl.add(mediaArray.getJSONObject(0).getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
				mediaType.add(mediaArray.getJSONObject(0).getString(MMAPIConstants.JSON_KEY_TYPE));
				if(mediaUrl.size() > 0) {
					MMTopviewedArrayAdapter adapter = new MMTopviewedArrayAdapter(getActivity(), R.layout.top_viewed_listview_row, getTopViewedItems());
					lvtopviewed.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
