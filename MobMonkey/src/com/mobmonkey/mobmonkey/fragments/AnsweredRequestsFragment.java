package com.mobmonkey.mobmonkey.fragments;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

public class AnsweredRequestsFragment extends MMFragment {

	private static final String TAG = "AnsweredRequestsFragment";
	private ListView lvAnsweredRequests;
	private JSONArray answeredRequests;
	private SharedPreferences userPrefs;
	private int positionClicked;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_answeredrequests_screen, container, false);
		
		try {
			answeredRequests = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS));
			Log.d(TAG, answeredRequests.toString());
			
		} catch (JSONException ex) {
			
		}
		
		
		return view;
	}
	
	@Override
	public void onFragmentBackPressed() {

	}

}
