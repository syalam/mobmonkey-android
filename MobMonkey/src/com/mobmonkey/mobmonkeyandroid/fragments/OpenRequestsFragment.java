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
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMOpenRequestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMOpenRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMInboxAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMRequestMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
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
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		View view = inflater.inflate(R.layout.fragment_openrequests_screen, container, false);
		lvOpenedRequests = (ListView) view.findViewById(R.id.lvopenrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		try {
			// get all the open request, and then update the badge counter
			MMInboxAdapter.getOpenRequests(new OpenRequestCallback(), 
										   MMConstants.PARTNER_ID, 
					  					   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
					  					   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		} catch (NumberFormatException e) {
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
			item.title = jObj.getString(MMSDKConstants.JSON_KEY_NAME_OF_LOCATION);
			if(jObj.getString(MMSDKConstants.JSON_KEY_MESSAGE).equals(MMSDKConstants.DEFAULT_STRING_NULL)) {
				item.message = MMSDKConstants.DEFAULT_STRING_EMPTY;
			} else {
				item.message = jObj.getString(MMSDKConstants.JSON_KEY_MESSAGE);
			}
			//date can be null. leave time as a blank string if its null
			if(jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_DATE).compareTo(MMSDKConstants.DEFAULT_STRING_NULL) == 0) {
				item.time = MMSDKConstants.DEFAULT_STRING_EMPTY;
			}
			else {
				item.time = MMUtility.getDate(Long.parseLong(jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_DATE)), "MMMM dd hh:mma");
			}
			
			item.dis = MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles);
			item.mediaType = jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE);
			
			openedRequestItems[i] = item;
		}
		
		return openedRequestItems;
	}
	
	private void deleteRequest() {
		try {
			MMProgressDialog.displayDialog(getActivity(), "Open Request", "Deleting request...");
			MMRequestMediaAdapter.deleteMedia(new DeleteRequestCallback(), 
											  MMConstants.PARTNER_ID, 
											  userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
											  userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY),
											  openRequests.getJSONObject(clickedPosition).getString(MMSDKConstants.JSON_KEY_REQUEST_ID), 
											  openRequests.getJSONObject(clickedPosition).getString(MMSDKConstants.JSON_KEY_RECURRING));
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
				JSONObject jObj = new JSONObject((String)obj);
				if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
//					MMOpenRequestsItem[] items, data;
//					data = getOpenedRequestItems();
//					items = new MMOpenRequestsItem[data.length - 1];
//					
//					for(int i = 0; i < data.length; i++) {
//						if(i < clickedPosition) {
//							items[i] = data[i];
//						} else if (i > clickedPosition) {
//							items[i-1] = data[i];
//						}
//					}
					MMProgressDialog.dismissDialog();
					
					JSONArray newArray = new JSONArray();
					for(int i = 0; i < newArray.length(); i++) {
						if(i != clickedPosition) {
							newArray.put(openRequests.getJSONObject(i));
						}
					}
					openRequests = newArray;
					
					arrayAdapter = new MMOpenRequestsArrayAdapter(getActivity(), R.layout.openrequests_list_row, getOpenedRequestItems());
					lvOpenedRequests.setAdapter(arrayAdapter);
					lvOpenedRequests.invalidate();
					
					Toast.makeText(getActivity().getApplicationContext(), 
							   "You have successfully deleted a request.", 
							   Toast.LENGTH_LONG)
							   .show();
				}
				else {
					Toast.makeText(getActivity().getApplicationContext(), 
								   "An error has occured while deleting a request.", 
								   Toast.LENGTH_LONG)
								   .show();
				}
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	private class OpenRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {			
				try {
					Log.d(TAG, "OpenRequestCallback: " + (String) obj);
					openRequests = new JSONArray((String) obj);
					arrayAdapter = new MMOpenRequestsArrayAdapter(getActivity(), R.layout.openrequests_list_row, getOpenedRequestItems());
					lvOpenedRequests.setAdapter(arrayAdapter);
					lvOpenedRequests.setOnItemClickListener(new DeleteRequestListener());				
				} catch (JSONException ex) {
					ex.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
