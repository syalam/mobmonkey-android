package com.mobmonkey.mobmonkey.utils;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.mobmonkey.mobmonkey.CategoryScreen;
import com.mobmonkey.mobmonkey.SearchResultsScreen;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

public class MMSearchResultsCallback implements MMCallback {
	private static final String TAG = "MMSearchResultsCallback: ";
	Context context;
	ProgressDialog progressDialog;
	Location location;
	String searchCategory;
	
	public MMSearchResultsCallback(Context context, ProgressDialog progressDialog, Location location, String searchCategory) {
		this.context = context;
		this.progressDialog = progressDialog;
		this.location = location;
		this.searchCategory = searchCategory;
	}
	
	@Override
	public void processCallback(Object obj) 
	{
		if(obj == null)
			Log.d(TAG, TAG + "SearchResultsCallback is null");
		else
		{
			Log.d(TAG, TAG + "response: " + ((String) obj));
			try {
				JSONArray searchResults = new JSONArray((String) obj);
				progressDialog.dismiss();
				if(searchResults.isNull(0))
				{
					displayAlertDialog();
				}
				else
				{
					Intent searchResultsIntent = new Intent(context, SearchResultsScreen.class);
					searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, true);
					searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
					searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
					searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, (String) obj);
					context.startActivity(searchResultsIntent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displayAlertDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("MobMonkey");
		alertDialogBuilder.setMessage("No locations found");
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {				
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
