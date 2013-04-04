package com.mobmonkey.mobmonkey.fragments;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMTopviewedArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMTopviewedItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class TopViewedFragment extends MMFragment {
	public static final String TAG = "TopViewedFragment: "; 
	
	private ListView lvtopviewed;
	private JSONArray topViewed;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_topviewed_screen, container, false);
		lvtopviewed = (ListView) view.findViewById(R.id.lvtopviewed);
		
		try {
			topViewed = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED));
			MMTopviewedArrayAdapter adapter = new MMTopviewedArrayAdapter(getActivity(), R.layout.top_viewed_listview_row, getTopViewedItems());
			lvtopviewed.setAdapter(adapter);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
	
	private MMTopviewedItem[] getTopViewedItems() throws JSONException, IOException {
		MMTopviewedItem topViewedItems[] = new MMTopviewedItem[topViewed.length()];
		
		for(int i = 0; i < topViewedItems.length; i++) {
			if(!topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_MEDIA).equals(MMAPIConstants.DEFAULT_STRING_NULL)) {
				JSONObject media = new JSONObject(topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_MEDIA));
				topViewedItems[i] = new MMTopviewedItem(topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_NAME), 
											  media.getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
			} else {
				topViewedItems[i] = new MMTopviewedItem(topViewed.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_NAME), null);
			}
			
		}
		
		return topViewedItems;
	}
}
