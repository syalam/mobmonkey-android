package com.mobmonkey.mobmonkeyandroid.fragments;

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
import android.widget.Button;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchResultsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.*;
import com.mobmonkey.mobmonkeysdk.utils.*;

/**
 * @author Dezapp, LLC
 *
 */
public class HistoryFragment extends MMFragment implements OnClickListener,
														   OnItemClickListener {
	private static final String TAG = "HistoryFragment";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private JSONArray history;
	private Location location;
	private MMResultsLocation[] historyLocations;
	
	private ListView lvHistory;
	ArrayAdapter<MMResultsLocation> arrayAdapter;
	
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
				getHistoryLocations();
				arrayAdapter = new MMHistoryArrayAdapter(getActivity(), R.layout.mm_history_listview_row, historyLocations);
				lvHistory.setAdapter(arrayAdapter);
				
				btnClear.setOnClickListener(HistoryFragment.this);
				lvHistory.setOnItemClickListener(HistoryFragment.this);
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
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		try {
			searchResultsLocationSelectListener.onSearchResultsFragmentItemClick(history.getJSONObject(position));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void getHistoryLocations() throws JSONException {
		historyLocations = new MMResultsLocation[history.length()];
			for(int i = 0; i < history.length(); i++) {
				JSONObject jObj = history.getJSONObject(i);
				historyLocations[i] = new MMResultsLocation();
				historyLocations[i].setLocName(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
				historyLocations[i].setLocDist(MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + 
						getString(R.string.miles));
				historyLocations[i].setLocAddr(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS) + MMSDKConstants.DEFAULT_STRING_NEWLINE + jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + 
										jObj.getString(MMSDKConstants.JSON_KEY_REGION) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + jObj.getString(MMSDKConstants.JSON_KEY_POSTCODE));
			}
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
		if(historyLocations.length > 0) {
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
	
	/**
	 * 
	 */
	private void clearHistory() {
		historyLocations = new MMResultsLocation[0];
		arrayAdapter = new MMHistoryArrayAdapter(getActivity(), R.layout.search_result_list_row, historyLocations);
		lvHistory.setAdapter(arrayAdapter);
		lvHistory.invalidate();
		
		userPrefsEditor.remove(MMSDKConstants.SHARED_PREFS_KEY_HISTORY);
		userPrefsEditor.commit();
	}
}
