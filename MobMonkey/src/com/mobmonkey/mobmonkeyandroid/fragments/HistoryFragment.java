package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMHistoryArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchResultsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.*;
import com.mobmonkey.mobmonkeysdk.utils.*;

/**
 * @author Dezapp, LLC
 *
 */
public class HistoryFragment extends MMFragment implements OnClickListener,
														   OnItemClickListener,
														   OnItemLongClickListener {
	private static final String TAG = "HistoryFragment";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private JSONArray history;
	private Location location;
	private ArrayList<JSONObject> historyLocations;
	
	private ListView lvHistory;
	ArrayAdapter<JSONObject> arrayAdapter;
	
	private MMOnSearchResultsFragmentItemClickListener searchResultsLocationSelectListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_history_screen, container, false);
		Button btnClear = (Button) view.findViewById(R.id.btnclear);
		lvHistory = (ListView) view.findViewById(R.id.lvhistory);
		
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		try {
			if(!userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_HISTORY, MMSDKConstants.DEFAULT_STRING_EMPTY).equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				history = new JSONArray(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_HISTORY, MMSDKConstants.DEFAULT_STRING_EMPTY));

				if(history.length() > 0) {
					getHistoryLocations();
					
					btnClear.setOnClickListener(HistoryFragment.this);
					lvHistory.setOnItemClickListener(HistoryFragment.this);
					lvHistory.setOnItemLongClickListener(HistoryFragment.this);
				} else {
					displayNoHistoryAlert();
				}
			} else {
				displayNoHistoryAlert();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnSearchResultsFragmentItemClickListener) {
			searchResultsLocationSelectListener = (MMOnSearchResultsFragmentItemClickListener) activity;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnclear:
				promptClearHistory();
				break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		searchResultsLocationSelectListener.onSearchResultsFragmentItemClick(arrayAdapter.getItem(position));
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		try {
			promptRemoveLocationFromHistory(arrayAdapter.getItem(position));
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_HISTORY, history.toString());
		userPrefsEditor.commit();
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getHistoryLocations() throws JSONException {
		historyLocations = new ArrayList<JSONObject>();
		for(int i = 0; i < history.length(); i++) {
			historyLocations.add(history.getJSONObject(i));
		}
		
		arrayAdapter = new MMHistoryArrayAdapter(getActivity(), R.layout.listview_row_history, historyLocations);
		lvHistory.setAdapter(arrayAdapter);
	}
	
	/**
	 * 
	 */
	private void displayNoHistoryAlert() {
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.ad_title_no_history)
			.setMessage(R.string.ad_message_no_history)
			.setCancelable(false)
			.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getActivity().onBackPressed();
				}
			})
			.show();
	}
	
	/**
	 * 
	 */
	private void promptClearHistory() {
		if(historyLocations.size() > 0) {
			new AlertDialog.Builder(getActivity())
				.setTitle(R.string.ad_title_clear_history)
				.setMessage(R.string.ad_message_clear_history)
				.setCancelable(false)
				.setPositiveButton(R.string.ad_btn_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						clearHistory();
					}
				})
				.setNegativeButton(R.string.ad_btn_no, null)
				.show();
		} else {
			displayNoHistoryAlert();
		}
	}
	
	private void promptRemoveLocationFromHistory(final JSONObject loc) throws JSONException {
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.ad_title_remove_history)
			.setMessage(getString(R.string.ad_message_remove) + loc.getString(MMSDKConstants.JSON_KEY_NAME) + getString(R.string.ad_message_from_history))
			.setCancelable(false)
			.setPositiveButton(R.string.ad_btn_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeFromHistory(loc);
				}
			})
			.setNegativeButton(R.string.ad_btn_no, null)
			.show();
	}
	
	/**
	 * 
	 */
	private void clearHistory() {
		historyLocations.removeAll(historyLocations);
		history = new JSONArray(historyLocations);
		arrayAdapter.notifyDataSetChanged();
	}
	
	private void removeFromHistory(JSONObject loc) {
		historyLocations.remove(loc);
		history = new JSONArray(historyLocations);
		arrayAdapter.notifyDataSetChanged();
	}
}
