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
	
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_inbox_screen, container, false);
		lvInbox = (ListView) view.findViewById(R.id.lvinbox);
		
		//inboxRequests = new JSONArray[4];
		
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
		listener.onInboxItemClick(position);
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
			
			// get counts of each inbox categories
			MMInboxAdapter.getCounts(new InboxCountsCallback(), 
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
		public void onInboxItemClick(int position);
	}
	
	private class InboxCountsCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			int assignedReadRequests, assignedUnreadRequests, fulfilledUnreadCount, openrequests, fulfilledReadCount;
			Log.d(TAG, (String) obj);
			try {
				JSONObject jObj = new JSONObject((String) obj);
				
				assignedReadRequests = jObj.getInt(MMAPIConstants.JSON_KEY_ASSIGNED_READ_REQUESTS);
				assignedUnreadRequests = jObj.getInt(MMAPIConstants.JSON_KEY_ASSIGNED_UNREAD_REQUESTS);
				fulfilledUnreadCount = jObj.getInt(MMAPIConstants.JSON_KEY_FULFILLED_UNREAD_COUNT);
				openrequests = jObj.getInt(MMAPIConstants.JSON_KEY_OPEN_REQUESTS_COUNT);
				fulfilledReadCount = jObj.getInt(MMAPIConstants.JSON_KEY_FULFILLED_READ_COUNT);
				
				int openRequestCount = openrequests,
					answeredRequestsCount = fulfilledUnreadCount + fulfilledReadCount,
					assignedRequestsCount = assignedReadRequests + assignedUnreadRequests;
				
				if(openRequestCount > 0) {
					inboxItems[0].counter = Integer.toString(openRequestCount);
					inboxItems[0].containCounter = openRequestCount;
					arrayAdapter.isEnabled(0);
				}
				
				if(answeredRequestsCount > 0) {
					inboxItems[1].counter = Integer.toString(fulfilledUnreadCount);
					inboxItems[1].containCounter = answeredRequestsCount;
					arrayAdapter.isEnabled(1);
				}
				
				if(assignedRequestsCount > 0) {
					inboxItems[2].counter = Integer.toString(assignedUnreadRequests);
					inboxItems[2].containCounter = assignedRequestsCount;
					arrayAdapter.isEnabled(2);
				}
				arrayAdapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

}
