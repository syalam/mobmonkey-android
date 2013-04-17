package com.mobmonkey.mobmonkeyandroid.fragments;

import java.text.ParseException;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
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
import com.mobmonkey.mobmonkeyandroid.utils.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMOpenRequestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMOpenRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMRequestMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * Android {@link Fragment} to display Open requests Fragment.
 * @author Dezapp, LLC
 *
 */
public class OpenRequestsFragment extends MMFragment {
	private static final String TAG = "OpenRequestsScreen: ";
	
	private ListView lvOpenedRequests;
	private Location location;
	private JSONArray openRequests;
	private MMOpenRequestsArrayAdapter arrayAdapter;
	private int clickedPosition;
	private SharedPreferences userPrefs;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		View view = inflater.inflate(R.layout.fragment_openrequests_screen, container, false);
		lvOpenedRequests = (ListView) view.findViewById(R.id.lvopenrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		try {
			openRequests = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS));
			arrayAdapter = new MMOpenRequestsArrayAdapter(getActivity(), R.layout.openrequests_list_row, getOpenedRequestItems());
			lvOpenedRequests.setAdapter(arrayAdapter);
			lvOpenedRequests.setOnItemClickListener(new DeleteRequestListener());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	/**
	 * function that generate an array of {@link MMOpenRequestsItem} and returns it.
	 * @return {@link MMOpenedRequestsItem[]}
	 * @throws JSONException
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	private MMOpenRequestsItem[] getOpenedRequestItems() throws JSONException, NumberFormatException, ParseException {
		MMOpenRequestsItem[] openedRequestItems = new MMOpenRequestsItem[openRequests.length()];

		for(int i = 0; i < openRequests.length(); i++) {
			JSONObject jObj = openRequests.getJSONObject(i);
			MMOpenRequestsItem item = new MMOpenRequestsItem();
			item.title = jObj.getString(MMAPIConstants.JSON_KEY_NAME_OF_LOCATION);
			if(jObj.getString(MMAPIConstants.JSON_KEY_MESSAGE).equals(MMAPIConstants.DEFAULT_STRING_NULL)) {
				item.message = MMAPIConstants.DEFAULT_STRING_EMPTY;
			} else {
				item.message = jObj.getString(MMAPIConstants.JSON_KEY_MESSAGE);
			}
			//date can be null. leave time as a blank string if its null
			if(jObj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE).compareTo(MMAPIConstants.DEFAULT_STRING_NULL) == 0) {
				item.time = MMAPIConstants.DEFAULT_STRING_EMPTY;
			}
			else {
				item.time = MMUtility.getDate(Long.parseLong(jObj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE)), "MMMM dd hh:mma");
			}
			
			item.dis = MMUtility.calcDist(location, jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles);
			item.mediaType = jObj.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE);
			
			openedRequestItems[i] = item;
		}
		
		return openedRequestItems;
	}
	
	private void deleteRequest() {
		try {
			MMRequestMediaAdapter.deleteMedia(new DeleteRequestCallback(), 
											  MMConstants.PARTNER_ID, 
											  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
											  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY),
											  openRequests.getJSONObject(clickedPosition).getString(MMAPIConstants.JSON_KEY_REQUEST_ID), 
											  openRequests.getJSONObject(clickedPosition).getString(MMAPIConstants.JSON_KEY_RECURRING));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public class DeleteRequestListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			clickedPosition = position;
			new AlertDialog.Builder(getActivity())
				.setTitle("Open Requests")
				.setMessage("Delete Request?")
				.setCancelable(false)
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteRequest();
					}
					
				})
				.setNegativeButton("Cancel", null)
				.show();
		}
	}
	
	private class DeleteRequestCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String)obj);
			try {
				MMOpenRequestsItem[] items, data;
				data = getOpenedRequestItems();
				items = new MMOpenRequestsItem[data.length - 1];
				
				for(int i = 0; i < data.length; i++) {
					if(i < clickedPosition) {
						items[i] = data[i];
					} else if (i > clickedPosition) {
						items[i-1] = data[i];
					}
				}
				
				arrayAdapter = new MMOpenRequestsArrayAdapter(getActivity(), R.layout.openrequests_list_row, items);
				lvOpenedRequests.setAdapter(arrayAdapter);
				lvOpenedRequests.invalidate();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
	}
}
