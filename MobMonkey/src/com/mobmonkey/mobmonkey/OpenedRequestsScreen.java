package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMOpenedRequestsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMOpenedRequestsItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

public class OpenedRequestsScreen extends Activity{

	private static final String TAG = "OpenRequestsScreen";
	
	private ListView lvOpenedRequests;
	private Location location;
	private JSONArray openedRequests;
	private MMOpenedRequestsArrayAdapter arrayAdapter;
	
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void init() throws NumberFormatException, ParseException, JSONException {
		lvOpenedRequests = (ListView) findViewById(R.id.lvopenrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		openedRequests = new JSONArray(getIntent().getStringExtra(MMAPIConstants.JSON_KEY_OPENREQUESTS));
		arrayAdapter = new MMOpenedRequestsArrayAdapter(OpenedRequestsScreen.this, R.layout.openrequests_list_row, getOpenedRequestItems());
		lvOpenedRequests.setAdapter(arrayAdapter);
	}
	
	private MMOpenedRequestsItem[] getOpenedRequestItems() throws JSONException, NumberFormatException, ParseException {
		MMOpenedRequestsItem[] openedRequestItems = new MMOpenedRequestsItem[openedRequests.length()];

		for(int i = 0; i < openedRequests.length(); i++) {
			JSONObject jobj = openedRequests.getJSONObject(i);
			MMOpenedRequestsItem item = new MMOpenedRequestsItem();
			item.title = jobj.getString(MMAPIConstants.JSON_KEY_NAME_OF_LOCATION);
			if(jobj.getString(MMAPIConstants.JSON_KEY_MESSAGE).equals(MMAPIConstants.DEFAULT_STRING_NULL)) {
				item.message = MMAPIConstants.DEFAULT_STRING;
			} else {
				item.message = jobj.getString(MMAPIConstants.JSON_KEY_MESSAGE);
			}
			item.time = getDate(Long.parseLong(jobj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE)));
			
			double latitude = location.getLatitude(),
				   longitude = location.getLongitude();
			
			double dis = (Math.sqrt(Math.pow(latitude - jobj.getDouble(MMAPIConstants.KEY_LATITUDE), 2) + Math.pow(longitude - jobj.getDouble(MMAPIConstants.KEY_LONGITUDE), 2))) / 1.609344;
			DecimalFormat df = new DecimalFormat("#.##");
			item.dis = df.format(dis) + " miles";
			item.mediaType = jobj.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE);
			
			openedRequestItems[i] = item;
		}
		
		return openedRequestItems;
	}
	
	public String getDate(long milliSeconds) throws ParseException {
	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     
	     SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd hh:mma");
	     return sdf.format(calendar.getTime());
	}
}
