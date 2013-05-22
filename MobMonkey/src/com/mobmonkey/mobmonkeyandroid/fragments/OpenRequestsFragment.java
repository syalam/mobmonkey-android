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
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMOpenRequestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMOpenRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMRequestAdapter;
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
public class OpenRequestsFragment extends MMFragment implements OnItemClickListener {
	private static final String TAG = "OpenRequestsScreen: ";
	
	private MMExpandedListView elvOpenedRequests;
	private Location location;
	private JSONArray openRequests;
	private MMOpenRequestsArrayAdapter arrayAdapter;
	private SharedPreferences userPrefs;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		MMRequestAdapter.getOpenRequests(new OpenRequestCallback(), 
										 MMConstants.PARTNER_ID,
										 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_retrieving_all_open_requests));

		View view = inflater.inflate(R.layout.fragment_openrequests_screen, container, false);
		elvOpenedRequests = (MMExpandedListView) view.findViewById(R.id.elvopenrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
				
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		final int pos = position;
		
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.ad_title_delete_request)
			.setMessage(R.string.ad_message_delete_request)
			.setCancelable(false)
			.setPositiveButton(R.string.ad_btn_delete, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteRequest(pos);
				}
				
			})
			.setNegativeButton(R.string.ad_btn_cancel, null)
			.show();
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
			if(jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_DATE).equals(MMSDKConstants.DEFAULT_STRING_NULL)) {
				item.time = MMSDKConstants.DEFAULT_STRING_EMPTY;
			} else {
				item.time = MMUtility.getDate(Long.parseLong(jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_DATE)),
											  MMSDKConstants.DATE_FORMAT_MMMM_DD_HH_SEMICOLON_MMA);
			}
			
			item.dis = MMUtility.calcDist(location,
										  jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE),
										  jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + getString(R.string.miles);
			item.mediaType = jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE);
			
			openedRequestItems[i] = item;
		}
		
		return openedRequestItems;
	}
	
	/**
	 * 
	 * @param position
	 */
	private void deleteRequest(int position) {
		try {
			MMRequestAdapter.deleteRequest(new DeleteRequestCallback(position),
										   openRequests.getJSONObject(position).getString(MMSDKConstants.JSON_KEY_REQUEST_ID),
										   openRequests.getJSONObject(position).getString(MMSDKConstants.JSON_KEY_RECURRING),
										   MMConstants.PARTNER_ID,
										   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_deleting_request));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class DeleteRequestCallback implements MMCallback {
		private int position;
		
		public DeleteRequestCallback(int position) {
			this.position = position;
		}
		
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, (String)obj);
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String)obj);
						if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							
							ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
							
							for(int i = 0; i < openRequests.length(); i++) {
								temp.add(openRequests.getJSONObject(i));
							}
							temp.remove(position);
							openRequests = new JSONArray(temp);
							
							arrayAdapter = new MMOpenRequestsArrayAdapter(getActivity(), R.layout.listview_row_openrequests, getOpenedRequestItems());
							elvOpenedRequests.setAdapter(arrayAdapter);
							elvOpenedRequests.invalidate();
							
							Toast.makeText(getActivity().getApplicationContext(), 
									getString(R.string.toast_request) + MMSDKConstants.DEFAULT_STRING_SPACE + jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), 
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getActivity().getApplicationContext(), jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
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
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class OpenRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						Log.d(TAG, "OpenRequestCallback: " + (String) obj);
						openRequests = new JSONArray((String) obj);
						arrayAdapter = new MMOpenRequestsArrayAdapter(getActivity(), R.layout.listview_row_openrequests, getOpenedRequestItems());
						elvOpenedRequests.setAdapter(arrayAdapter);
						elvOpenedRequests.setVisibility(View.VISIBLE);
						elvOpenedRequests.setOnItemClickListener(OpenRequestsFragment.this);				
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
}
