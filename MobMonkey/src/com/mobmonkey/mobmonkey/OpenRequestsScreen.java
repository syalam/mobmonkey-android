package com.mobmonkey.mobmonkey;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMOpenRequestsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMOpenRequestsItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

public class OpenRequestsScreen extends Activity{

	private static final String TAG = "OpenRequestsScreen";
	
	private ListView lvOpenRequests;
	private MMOpenRequestsArrayAdapter arrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openrequests_screen);
		
		try {
			init();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void init() throws NumberFormatException, ParseException {
		lvOpenRequests = (ListView) findViewById(R.id.lvopenrequests);
		try {
			JSONArray jArr = new JSONArray(getIntent().getStringExtra(MMAPIConstants.JSON_KEY_OPENREQUESTS));
			int dataLength = jArr.length();
			MMOpenRequestsItem[] data = new MMOpenRequestsItem[dataLength];

			for(int i = 0; i < dataLength; i++) {
				MMOpenRequestsItem item = new MMOpenRequestsItem();
				JSONObject jobj = jArr.getJSONObject(i);
				item.title = jobj.getString("nameOfLocation");
				if(jobj.getString("message") == null) {
					item.message = MMAPIConstants.DEFAULT_STRING;
				} else {
					item.message = jobj.getString("message");
				}
				item.time = getDate(Long.parseLong(jobj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE)));
				
				Location location = MMLocationManager.getGPSLocation(new MMLocationListener());
				
				double latitude = location.getLatitude(),
					   longitude = location.getLongitude();
				
				double dis = (Math.sqrt(Math.pow(latitude - jobj.getDouble(MMAPIConstants.KEY_LATITUDE), 2) + Math.pow(longitude - jobj.getDouble(MMAPIConstants.KEY_LONGITUDE), 2))) / 1.609344;
				DecimalFormat df = new DecimalFormat("#.##");
				item.dis = df.format(dis) + " miles";
				item.mediaType = jobj.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE);
				
				data[i] = item;
			}
			
			arrayAdapter = new MMOpenRequestsArrayAdapter(OpenRequestsScreen.this, R.layout.openrequests_list_row, data);
			lvOpenRequests.setAdapter(arrayAdapter);
		} catch (JSONException ex) {
			
		}
		
	}
	
	public static String getDate(long milliSeconds) throws ParseException {
	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     
	     SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd hh:mma");
	     return sdf.format(calendar.getTime());
	}
}
