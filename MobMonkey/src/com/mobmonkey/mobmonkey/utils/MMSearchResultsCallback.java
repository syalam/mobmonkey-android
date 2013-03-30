package com.mobmonkey.mobmonkey.utils;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.SearchResultsScreen;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

public class MMSearchResultsCallback implements MMCallback {
	private static final String TAG = "MMSearchResultsCallback: ";
	Context context;
	ProgressDialog progressDialog;
	Location location;
	String searchCategory;
	
	public MMSearchResultsCallback(Context context, ProgressDialog progressDialog, String searchCategory) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.location = MMLocationManager.getGPSLocation(new MMLocationListener());
		this.searchCategory = searchCategory;
	}
	
	@Override
	public void processCallback(Object obj) {
		if(obj != null) {
			Log.d(TAG, TAG + "response: " + ((String) obj));
			try {
				JSONArray searchResults = new JSONArray((String) obj);
				progressDialog.dismiss();
				if(searchResults.isNull(0)) {
					displayAlertDialog();
				} else {
					Intent searchResultsIntent = new Intent(context, SearchResultsScreen.class);
					searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, true);
					searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
					searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, (String) obj);
					context.startActivity(searchResultsIntent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displayAlertDialog() {
		new AlertDialog.Builder(context)
			.setTitle("MobMonkey")
			.setMessage("No locations found")
			.setPositiveButton(R.string.ad_btn_ok, null)
			.show();
	}
}
