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
		
		// TODO: hard coded information. needs to use callback to get infomation from server.
		data = new MMInboxItem[4];
		for(int i = 0; i < data.length; i++) {
			MMInboxItem item = new MMInboxItem();
			switch(i) {
			case 0:
				item.title = "Open Requests";
				break;
			case 1:
				item.title = "Answered Request";
				break;
			case 2:
				item.title = "Assigned Request";
				break;
			case 3:
				item.title = "Notifications";
				break;
			default:
				break;
			}
			item.counter = 0 + MMAPIConstants.DEFAULT_STRING;
			data[i] = item;
		}
		
		arrayAdapter = new MMInboxArrayAdapter(InboxScreen.this, R.layout.inbox_list_row, data, Color.GRAY) {
			@Override
			public boolean isEnabled(int position) {
				return false;
			}	
		};
		
		lvInbox.setAdapter(arrayAdapter);
		lvInbox.setEnabled(false);
		
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
			try {
				JSONArray jobj = new JSONArray((String) obj);
				data[0].counter = jobj.length()+"";
				//Log.d(TAG, (String) obj);
				arrayAdapter.notifyDataSetChanged();
				
				
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private class AssignedRequestCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			try {
				JSONArray jobj = new JSONArray((String) obj);
				data[2].counter = jobj.length() + MMAPIConstants.DEFAULT_STRING;
				arrayAdapter.notifyDataSetChanged();
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
		
	}
}
