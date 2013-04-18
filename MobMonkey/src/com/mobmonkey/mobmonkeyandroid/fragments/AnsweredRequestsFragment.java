package com.mobmonkey.mobmonkeyandroid.fragments;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMAnsweredRequestArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMAnsweredRequestItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMInboxAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * Android {@link Fragment} to display Answered Requests.
 * @author Dezapp, LLC
 *
 */

public class AnsweredRequestsFragment extends MMFragment {
	private static final String TAG = "AnsweredRequestsFragment";
	private ListView lvAnsweredRequests;
	private JSONArray answeredRequests;
	private SharedPreferences userPrefs;
	private int positionClicked;
	private Location location;
	private MMAnsweredRequestArrayAdapter arrayAdapter;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_answeredrequests_screen, container, false);
		lvAnsweredRequests = (ListView) view.findViewById(R.id.lvAnsweredrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		// get all the assigned request, and then update the badge counter
		MMInboxAdapter.getAnsweredRequests(new AnsweredRequestCallback(), 
										   MMConstants.PARTNER_ID, 
										   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
										   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY));
		
		return view;
	}
	
	@Override
	public void onFragmentBackPressed() {

	}

	/**
	 * function that generate an array of {@link MMAnsweredRequestItem} and returns it.
	 * @return {@link MMAnsweredRequestItem[]}
	 * @throws JSONException
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	
	private MMAnsweredRequestItem[] getAnsweredRequestItems() throws JSONException, NumberFormatException, ParseException {
		MMAnsweredRequestItem[] answeredRequestItems = new MMAnsweredRequestItem[answeredRequests.length()];

		for(int i = 0; i < answeredRequests.length(); i++) {
			JSONObject jObj = answeredRequests.getJSONObject(i);
			
			MMAnsweredRequestItem item = new MMAnsweredRequestItem();
			
			// media file can be null
			if(jObj.getJSONArray(MMAPIConstants.JSON_KEY_MEDIA).length() > 0) {
				JSONObject media = jObj.getJSONArray(MMAPIConstants.JSON_KEY_MEDIA).getJSONObject(0);
				
				// title
				item.title = jObj.getString(MMAPIConstants.JSON_KEY_NAME_OF_LOCATION);
				
				// media uri
				item.mediaUri = Uri.parse(media.getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
				
				// media type
				item.mediaType = jObj.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE);
				
				// is fulfilled
				item.isAccepted = media.getBoolean(MMAPIConstants.JSON_KEY_ACCEPTED);
				
				// expiry date
				item.time = media.getString(MMAPIConstants.JSON_KEY_EXPIRY_DATE);
				
				item.context = getActivity();
			}
			// if no data for media, ignore it and prints out rest of the data
			else {
				
				// title
				item.title = jObj.getString(MMAPIConstants.JSON_KEY_NAME_OF_LOCATION);
				
				// media type
				item.mediaType = jObj.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE);
				
				// is fulfilled
				item.isAccepted = false;
				
				// expiry date
				item.time = MMAPIConstants.DEFAULT_STRING_EMPTY;
				
				item.context = getActivity();
			}
			
			answeredRequestItems[i] = item;
		}
		
		Log.d(TAG, "size of items: " + answeredRequestItems.length);
		
		return answeredRequestItems;
	}
	
	/**
	 * The {@link OnItemClickListener} for {@link ListView} in AnsweredRequestsFragment.
	 *
	 */
	private class onAnsweredRequestsClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			
		}
		
	}
	
	private class AnsweredRequestCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String) obj);
			if(obj != null) {
				try {
					answeredRequests = new JSONArray((String) obj);
					arrayAdapter = new MMAnsweredRequestArrayAdapter(getActivity(), R.layout.answeredrequests_listview_row, getAnsweredRequestItems());
					lvAnsweredRequests.setAdapter(arrayAdapter);
					lvAnsweredRequests.setOnItemClickListener(new onAnsweredRequestsClick());
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
