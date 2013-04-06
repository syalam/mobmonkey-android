package com.mobmonkey.mobmonkey.fragments;

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

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMTopviewedArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMTopviewedItem;
import com.mobmonkey.mobmonkeyapi.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

/**
 * @author Dezapp, LLC
 *
 */
public class TopViewedFragment extends MMFragment {
	public static final String TAG = "TopViewedFragment: "; 
	
	private ListView lvtopviewed;
	private JSONArray topViewed;
	private LinkedList<String> mediaUrl = new LinkedList<String>();
	private SharedPreferences userPrefs;
	private boolean isLoading = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_topviewed_screen, container, false);
		lvtopviewed = (ListView) view.findViewById(R.id.lvtopviewed);
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		try {
			topViewed = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED));
			getAllURL(topViewed);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return view;
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	private void getAllURL(JSONArray jArr) {
		
		try {
			for(int i = 0; i < jArr.length(); i++) {
				String locationID = jArr.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_LOCATION_ID);
				String providerID = jArr.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_PROVIDER_ID);
				
				MMMediaAdapter.retrieveAllMediaForLocation(new MedaiCallBack(), 
														   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
														   userPrefs.getString(MMAPIConstants.KEY_AUTH,MMAPIConstants.DEFAULT_STRING),
														   MMConstants.PARTNER_ID,
														   locationID, 
														   providerID);
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}
	
	private MMTopviewedItem[] getTopViewedItems() throws JSONException, IOException {
		MMTopviewedItem topViewedItems[] = new MMTopviewedItem[topViewed.length()];
		
		//Log.d(TAG, topViewed.toString());
		for(int i = 0; i < topViewedItems.length; i++) {
			topViewedItems[i] = new MMTopviewedItem(topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_NAME), 
													mediaUrl.get(i));
		}
		
		return topViewedItems;
	}
	
	private class MedaiCallBack implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			try {
				JSONObject jObj = new JSONObject((String)obj);
				JSONArray mediaArray = jObj.getJSONArray(MMAPIConstants.JSON_KEY_MEDIA);
				Log.d(TAG, "mediaArray: " + mediaArray.toString());
				
				mediaUrl.add(mediaArray.getJSONObject(0).getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
				Log.d(TAG, "mediaUrl: " + mediaArray.getJSONObject(0).getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
				
				Log.d(TAG, "mediaUrl Size: " + mediaUrl.size());
				
				if(mediaUrl.size() == mediaArray.length()) {
					Log.d(TAG, "finish loading");
					MMTopviewedArrayAdapter adapter = new MMTopviewedArrayAdapter(getActivity(), R.layout.top_viewed_listview_row, getTopViewedItems());
					lvtopviewed.setAdapter(adapter);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
