package com.mobmonkey.mobmonkeyandroid.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class MMSearchResultsCallback implements MMCallback {
	private static final String TAG = "MMSearchResultsCallback: ";
	private Activity activity;
	private String searchCategory;
	
	private MMOnNoCategoryFragmentItemClickListener noCategoryFragmentItemClickListener;
	private MMCallback mmCallback;
	
	public MMSearchResultsCallback(Activity activity, String searchCategory, MMCallback mmCallback) {
		this.activity = activity;
		this.searchCategory = searchCategory;
		
		if(activity instanceof MMOnNoCategoryFragmentItemClickListener) {
			noCategoryFragmentItemClickListener = (MMOnNoCategoryFragmentItemClickListener) activity;
		}
		
		this.mmCallback = mmCallback;
	}
	
	@Override
	public void processCallback(Object obj) {
		MMProgressDialog.dismissDialog();
		
		if(obj != null) {
			Log.d(TAG, TAG + "response: " + ((String) obj));
			try {
				JSONObject searchResults = new JSONObject((String) obj);
				if(searchResults.getInt(MMSDKConstants.JSON_KEY_TOTAL_ITEMS) == 0) {
					displayAlertDialog();
				} else {
					if(mmCallback != null) {
						mmCallback.processCallback(obj);
					}
					noCategoryFragmentItemClickListener.onNoCategoryFragmentItemClick(0, searchCategory, searchResults.getJSONArray(MMSDKConstants.JSON_KEY_DEFAULT_TEXTS).toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displayAlertDialog() {
		new AlertDialog.Builder(activity)
			.setTitle(searchCategory)
			.setMessage(R.string.ad_message_no_locations_found)
			.setCancelable(false)
			.setPositiveButton(R.string.ad_btn_ok, null)
			.show();
	}
}
