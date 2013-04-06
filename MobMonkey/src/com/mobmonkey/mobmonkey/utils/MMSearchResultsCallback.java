package com.mobmonkey.mobmonkey.utils;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.fragments.SearchFragment.OnNoCategoryItemClickListener;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

public class MMSearchResultsCallback implements MMCallback {
	private static final String TAG = "MMSearchResultsCallback: ";
	private Activity activity;
	private String searchCategory;
	
	private OnNoCategoryItemClickListener noCategoryItemClickListener;
	private MMCallback mmCallback;
	
	public MMSearchResultsCallback(Activity activity, String searchCategory, MMCallback mmCallback) {
		this.activity = activity;
		this.searchCategory = searchCategory;
		
		if(activity instanceof OnNoCategoryItemClickListener) {
			noCategoryItemClickListener = (OnNoCategoryItemClickListener) activity;
		}
		
		this.mmCallback = mmCallback;
	}
	
	@Override
	public void processCallback(Object obj) {
		MMProgressDialog.dismissDialog();
		
		if(obj != null) {
			Log.d(TAG, TAG + "response: " + ((String) obj));
			try {
				JSONArray searchResults = new JSONArray((String) obj);
				if(searchResults.isNull(0)) {
					displayAlertDialog();
				} else {
					if(mmCallback != null) {
						mmCallback.processCallback(obj);
					}
					noCategoryItemClickListener.onNoCategoryItemClick(true, searchCategory, ((String) obj));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displayAlertDialog() {
		new AlertDialog.Builder(activity)
			.setTitle("MobMonkey")
			.setMessage("No locations found")
			.setCancelable(false)
			.setPositiveButton(R.string.ad_btn_ok, null)
			.show();
	}
}
