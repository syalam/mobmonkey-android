package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.AssignedRequestsScreen;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMInboxArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMInboxItem;
import com.mobmonkey.mobmonkeysdk.adapters.MMInboxAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * Android {@link Fragment} to display Inbox Fragment.
 * @author Dezapp, LLC
 *
 */
public class InboxFragment extends MMFragment implements OnItemClickListener {
	private final String TAG = "InboxFragment: ";
	
	private SharedPreferences userPrefs;
	
	private ListView lvInbox;
	private MMInboxItem[] inboxItems;
	private MMInboxArrayAdapter arrayAdapter;
	
	private OnInboxItemClickListener listener;
	
	private JSONArray[] inboxRequests;
	
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_inbox_screen, container, false);
		lvInbox = (ListView) view.findViewById(R.id.lvinbox);
		
		inboxRequests = new JSONArray[4];
		
		lvInbox.setOnItemClickListener(InboxFragment.this);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnInboxItemClickListener) {
			listener = (OnInboxItemClickListener) activity;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
//		Intent intent = new Intent(getActivity(), AssignedRequestsScreen.class);
//		intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS, inboxRequests[position].toString());
//		startActivity(intent);
		listener.onInboxItemClick(position, inboxRequests[position].toString());
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	@Override
	public void onResume() {
		inboxUpdate();
		super.onResume();
	}
	
	/**
	 * Update Inbox {@link ListView} when first started or resume.
	 */
	private void inboxUpdate() {
		inboxItems = new MMInboxItem[getResources().getStringArray(R.array.inbox_category).length];
		for(int i = 0; i < inboxItems.length; i++) {
			inboxItems[i] = new MMInboxItem();
			inboxItems[i].title = getResources().getStringArray(R.array.inbox_category)[i];
			inboxItems[i].counter = "0";
		}
		
		arrayAdapter = new MMInboxArrayAdapter(getActivity(), R.layout.inbox_list_row, inboxItems);
		lvInbox.setAdapter(arrayAdapter);
		
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			// get all the open request, and then update the badge counter
			MMInboxAdapter.getOpenRequests(new OpenRequestCallback(), 
										   MMConstants.PARTNER_ID, 
					  					   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
					  					   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY));
			
			// get all the answered request, and then update the badge counter
			MMInboxAdapter.getAnsweredRequests(new AnsweredRequestCallback(), 
										   MMConstants.PARTNER_ID, 
										   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
										   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY));
			
			// get all the assigned request, and then update the badge counter
			MMInboxAdapter.getAssignedRequests(new AssignedRequestCallback(), 
											   MMConstants.PARTNER_ID, 
											   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
											   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY));
		}
	}
	
	/**
	 * The {@link OnItemClickListener} for {@link ListView} in InboxFragment.
	 *
	 */
	public interface OnInboxItemClickListener {
		public void onInboxItemClick(int position, String requests);
	}
	
	/**
	 * {@link MMCallback} function. Get call Opened requests.
	 *
	 */
	private class OpenRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {			
				try {
					JSONArray jArr = new JSONArray((String) obj);
					
					inboxRequests[0] = jArr;
					
					inboxItems[0].counter = Integer.toString(jArr.length());
					if(jArr.length() > 0) {
						arrayAdapter.isEnabled(0);
					}
					arrayAdapter.notifyDataSetChanged();				
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * {@link MMCallback} function. Get call Answered requests.
	 *
	 */
	private class AnsweredRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				try {
					JSONArray jArr = new JSONArray((String) obj);
					JSONArray newJarr = new JSONArray();
					for(int i = 0; i < jArr.length(); i++) {
						JSONObject jObj = jArr.getJSONObject(i);
						if(jObj.getBoolean("requestFulfilled")) {
							newJarr.put(jObj);
						}
					}
					inboxRequests[2] = newJarr;
					
					inboxItems[2].counter = Integer.toString(newJarr.length());
					
					if(newJarr.length() > 0) {
						arrayAdapter.isEnabled(2);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	/**
	 * {@link MMCallback} function. Get call Assigned requests.
	 *
	 */
	private class AssignedRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				Log.d(TAG, "AssignedRequest: " + (String) obj);
				try {
					JSONArray jArr = new JSONArray((String) obj);
					
					inboxRequests[2] = jArr;
					
					inboxItems[2].counter = Integer.toString(jArr.length());
					
					if(jArr.length() > 0) {
						arrayAdapter.isEnabled(2);
					}
					
					arrayAdapter.notifyDataSetChanged();
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
