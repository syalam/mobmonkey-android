package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMInboxArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMInboxItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

/**
 * Android {@link Activity} screen displays the inbox for the user
 * @author Dezapp, LLC
 *
 */
public class InboxScreen extends Activity {

	private ListView lvInbox;
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox_screen);
		
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
		MMInboxItem[] data = new MMInboxItem[4];
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
			item.counter = i + MMAPIConstants.DEFAULT_STRING;
			data[i] = item;
		}
		
		MMInboxArrayAdapter arrayAdapter = new MMInboxArrayAdapter(InboxScreen.this, R.layout.inbox_list_row, data, Color.GRAY) {
			@Override
			public boolean isEnabled(int position) {
				return false;
			}	
		};
		
		lvInbox.setAdapter(arrayAdapter);
		lvInbox.setEnabled(false);
	}
}
