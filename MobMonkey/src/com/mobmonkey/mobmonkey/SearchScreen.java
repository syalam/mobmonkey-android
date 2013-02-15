package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.adapters.MMSearchLocationAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchScreen extends Activity {
	private static final String TAG = "TAG";
	SharedPreferences userPrefs;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		setContentView(R.layout.searchscreen);
		
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double longitudeValue = location.getLongitude();
		double latitudeValue = location.getLatitude();
		DecimalFormat twoDForm = new DecimalFormat("#.######");
		latitudeValue = Double.valueOf(twoDForm.format(latitudeValue));
		longitudeValue = Double.valueOf(twoDForm.format(longitudeValue));
		
		Log.d(TAG, TAG + "LOCATION Longitude: " + longitudeValue + " Latitude: " + latitudeValue);
		
		String email = userPrefs.getString(MMAPIConstants.KEY_EMAIL_ADDRESS, MMAPIConstants.DEFAULT_STRING);
		String password = userPrefs.getString(MMAPIConstants.KEY_PASSWORD, MMAPIConstants.DEFAULT_STRING);

		MMSearchLocationAdapter.searchTextWithLocation(new SearchCallback(), "coffee", latitudeValue, longitudeValue, email, password);
		
	}
	
	private class SearchCallback implements MMCallback {
		public void processCallback(Object obj) {
			try {
				if(obj == null)
					Log.d(TAG, TAG + "The response object is empty");
				else
				{
					Log.d(TAG, TAG + "Callback Object " + obj);
					JSONArray response = new JSONArray((String) obj);
					Log.d(TAG, TAG + "Search Location Response: " + response);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}
}
