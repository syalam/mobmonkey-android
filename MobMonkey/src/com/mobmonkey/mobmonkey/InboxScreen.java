package com.mobmonkey.mobmonkey;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMInboxArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMInboxItem;
import com.mobmonkey.mobmonkey.utils.MMTrendingItem;
import com.mobmonkey.mobmonkeyapi.adapters.MMInboxAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

/**
 * Android {@link Activity} screen displays the inbox for the user
 * @author Dezapp, LLC
 *
 */
public class InboxScreen extends Activity {

	private final String TAG = "InboxScreen";
	private MMInboxItem[] data;
	private MMInboxArrayAdapter arrayAdapter;
	
	private ListView lvInbox;
	private SharedPreferences userPrefs;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox_screen);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		init();
	}
	
	/**
	 * Handler when back button is pressed, it will not close and destroy the current {@link Activity} but instead it will remain on the current {@link Activity}
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		return;
	}
	
	private void init() {
		lvInbox = (ListView) findViewById(R.id.lvinbox);
		
		data = new MMInboxItem[getResources().getStringArray(R.array.inbox_category).length];
		for(int i = 0; i < data.length; i++) {
			data[i] = new MMInboxItem();
			data[i].title = getResources().getStringArray(R.array.inbox_category)[i];
			data[i].counter = "0";
		}
		
		arrayAdapter = new MMInboxArrayAdapter(InboxScreen.this, R.layout.inbox_list_row, data);
		lvInbox.setAdapter(arrayAdapter);
		
		// get all the open request, and then update the badge counter
		MMInboxAdapter.getOpenRequests(new OpenRequestCallback(), 
									   MMConstants.PARTNER_ID, 
				  					   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
				  					   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
		
		// get all the assigned request, and then update the badge counter
		MMInboxAdapter.getAssignedRequests(new AssignedRequestCallback(), 
										   MMConstants.PARTNER_ID, 
										   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
										   userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	private class OpenRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {			
				try {
					JSONArray jArr = new JSONArray((String) obj);
					data[0].counter = Integer.toString(jArr.length());
					if(jArr.length() > 0) {
						arrayAdapter.isEnabled(0);
					}
					arrayAdapter.notifyDataSetChanged();				
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private class AssignedRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				try {
					JSONArray jArr = new JSONArray((String) obj);
					data[2].counter = Integer.toString(jArr.length());
					if(jArr.length() > 0) {
						arrayAdapter.isEnabled(2);
					}
					arrayAdapter.notifyDataSetChanged();
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}
		
	}
}
