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
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMOpenRequestsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMOpenRequestsItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
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
				item.message = jobj.getString("message");
				item.title = jobj.getString("nameOfLocation");
				item.time = getDate(Long.parseLong(jobj.getString("requestDate")), "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				
				LocationManager locationManager =
				        (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				
				double latitude = location.getLatitude(),
					   longitude = location.getLongitude();
				
				double dis = (Math.sqrt(Math.pow(latitude - jobj.getDouble("latitude"), 2) + Math.pow(longitude - jobj.getDouble("longitude"), 2))) / 1.609344;
				DecimalFormat df = new DecimalFormat( "#,###,###,##0.00" );
				item.dis = df.format(dis) + " miles";
				item.mediaType = jobj.getInt("mediaType");
				
				data[i] = item;
			}
			
			arrayAdapter = new MMOpenRequestsArrayAdapter(OpenRequestsScreen.this, R.layout.openrequests_list_row, data);
			lvOpenRequests.setAdapter(arrayAdapter);
		} catch (JSONException ex) {
			
		}
		
	}
	
	public static String getDate(long milliSeconds, String dateFormat) throws ParseException
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     
	     String month = formatter.format(calendar.getTime()).substring(5, 7);
	     String day = formatter.format(calendar.getTime()).substring(8, 10);
	     month = new DateFormatSymbols().getMonths()[Integer.parseInt(month)-1];
	     
	     String time = formatter.format(calendar.getTime()).substring(11,16);
	     formatter = new SimpleDateFormat("hh:mm");
	     Date date = formatter.parse(time);
	     formatter = new SimpleDateFormat("h:mma");
	     time = formatter.format(date).toLowerCase().toString();
	    		 
	    		 
	     String displayDate = month + " " + day + " " + time;
	     return displayDate;
	}
}
