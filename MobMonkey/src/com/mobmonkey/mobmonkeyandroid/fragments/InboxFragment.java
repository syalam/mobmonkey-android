package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMInboxArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMInboxItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMInboxAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
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
	
	private MMOnInboxFragmentItemClickListener listener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_inbox_screen, container, false);
		lvInbox = (ListView) view.findViewById(R.id.lvinbox);
		
		createInbox();
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnInboxFragmentItemClickListener) {
			listener = (MMOnInboxFragmentItemClickListener) activity;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation() != null) {
			getInboxCounts();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		listener.onInboxFragmentItemClick(position);
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	/**
	 * 
	 */
	private void createInbox() {
		inboxItems = new MMInboxItem[getResources().getStringArray(R.array.inbox_category).length];
		for(int i = 0; i < inboxItems.length; i++) {
			inboxItems[i] = new MMInboxItem();
			inboxItems[i].title = getResources().getStringArray(R.array.inbox_category)[i];
			inboxItems[i].counter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		
		arrayAdapter = new MMInboxArrayAdapter(getActivity(), R.layout.listview_row_inbox, inboxItems);
		lvInbox.setAdapter(arrayAdapter);
		lvInbox.setOnItemClickListener(InboxFragment.this);
	}
	
	/**
	 * Update Inbox {@link ListView} when first started or resume.
	 */
	private void getInboxCounts() {
		MMInboxAdapter.getCounts(new InboxCountsCallback());
	}
	
	/**
	 * 
	 * @param jObj
	 * @throws JSONException
	 */
	private void setInboxCounts(JSONObject jObj) throws JSONException {
		int openrequests = jObj.getInt(MMSDKConstants.JSON_KEY_OPEN_REQUESTS_COUNT);
		int assignedReadCount = jObj.getInt(MMSDKConstants.JSON_KEY_ASSIGNED_READ_REQUESTS);
		int assignedUnreadCount = jObj.getInt(MMSDKConstants.JSON_KEY_ASSIGNED_UNREAD_REQUESTS);
		int fulfilledReadCount = jObj.getInt(MMSDKConstants.JSON_KEY_FULFILLED_READ_COUNT);
		int fulfilledUnreadCount = jObj.getInt(MMSDKConstants.JSON_KEY_FULFILLED_UNREAD_COUNT);
		
		int openRequestCount = openrequests;
		int answeredRequestsCount = fulfilledReadCount + fulfilledUnreadCount;
		int assignedRequestsCount = assignedReadCount + assignedUnreadCount;
		
		if(openRequestCount > 0) {
			inboxItems[0].counter = openRequestCount;
			inboxItems[0].containCounter = openRequestCount;
		} else {
			inboxItems[0].counter = MMSDKConstants.DEFAULT_INT_ZERO;
			inboxItems[0].containCounter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		arrayAdapter.isEnabled(0);
		
		if(answeredRequestsCount > 0) {
			inboxItems[1].counter = fulfilledUnreadCount;
			inboxItems[1].containCounter = answeredRequestsCount;
		} else {
			inboxItems[1].counter = MMSDKConstants.DEFAULT_INT_ZERO;
			inboxItems[1].containCounter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		arrayAdapter.isEnabled(1);
		
		if(assignedRequestsCount > 0) {
			inboxItems[2].counter = assignedUnreadCount;
			inboxItems[2].containCounter = assignedRequestsCount;
		} else {
			inboxItems[2].counter = MMSDKConstants.DEFAULT_INT_ZERO;
			inboxItems[2].containCounter = MMSDKConstants.DEFAULT_INT_ZERO;
		}
		arrayAdapter.isEnabled(2);
		
		arrayAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class InboxCountsCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				Log.d(TAG, "inbox: " + (String) obj);
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						setInboxCounts(new JSONObject((String) obj));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
