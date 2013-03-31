package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMAssignedRequestsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkey.utils.MMOpenedRequestsItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class AssignedRequestsScreen extends Activity{

	private static final String TAG = "AssignedRequestsScreen";
	private Location location;
	private ListView lvassignedrequests;
	private JSONArray assginedRequests;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assignedrequests_screen);
		
		try {
			init();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws NumberFormatException, JSONException, ParseException {
		lvassignedrequests = (ListView) findViewById(R.id.lvassignedrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		assginedRequests = new JSONArray(getIntent().getStringExtra(MMAPIConstants.JSON_KEY_ASSIGNEDREQUESTS));
		MMAssignedRequestsArrayAdapter arrayAdapter = new MMAssignedRequestsArrayAdapter(AssignedRequestsScreen.this, R.layout.assignedrequests_listview_row, getAssginedRequestItems());
		lvassignedrequests.setAdapter(arrayAdapter);
		
	}
	
	private MMAssignedRequestsItem[] getAssginedRequestItems() throws JSONException, NumberFormatException, ParseException {
		MMAssignedRequestsItem[] assginedRequestItems = new MMAssignedRequestsItem[assginedRequests.length()];

		for(int i = 0; i < assginedRequests.length(); i++) {
			JSONObject jobj = assginedRequests.getJSONObject(i);
			MMAssignedRequestsItem item = new MMAssignedRequestsItem();
			item.title = jobj.getString(MMAPIConstants.JSON_KEY_NAME_OF_LOCATION);
			if(jobj.getString(MMAPIConstants.JSON_KEY_MESSAGE).equals(MMAPIConstants.DEFAULT_STRING_NULL)) {
				item.message = MMAPIConstants.DEFAULT_STRING;
			} else {
				item.message = jobj.getString(MMAPIConstants.JSON_KEY_MESSAGE);
			}
			
			//date can be null. leave time as a blank string if its null
			if(jobj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE).compareTo("null") == 0) {
				item.time = "";
			}
			else {
				item.time = getDate(Long.parseLong(jobj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE)));
			}
			
			double latitude = location.getLatitude(),
				   longitude = location.getLongitude();
			
			double dis = (Math.sqrt(Math.pow(latitude - jobj.getDouble(MMAPIConstants.KEY_LATITUDE), 2) + Math.pow(longitude - jobj.getDouble(MMAPIConstants.KEY_LONGITUDE), 2))) / 1.609344;
			DecimalFormat df = new DecimalFormat("#.##");
			item.dis = df.format(dis) + " miles";
			item.mediaType = jobj.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE);
			
			assginedRequestItems[i] = item;
			
			Log.d(TAG, assginedRequestItems[i].title);
		}
		
		return assginedRequestItems;
	}
	
	public String getDate(long milliSeconds) throws ParseException {
	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     
	     SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd hh:mma");
	     return sdf.format(calendar.getTime());
	}
}
