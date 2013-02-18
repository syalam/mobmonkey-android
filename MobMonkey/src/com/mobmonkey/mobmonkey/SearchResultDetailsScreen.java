package com.mobmonkey.mobmonkey;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.ExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchResultDetailsScreen extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_details_screen);
		
		TextView tvLocNameTitle = (TextView) findViewById(R.id.tvlocnametitle);
		TextView tvLocName = (TextView) findViewById(R.id.tvlocname);
		TextView tvMembersFound = (TextView) findViewById(R.id.tvmembersfound);
		ExpandedListView elvLocDetails = (ExpandedListView) findViewById(R.id.elvlocdetails);
		TextView tvBookmark = (TextView) findViewById(R.id.tvbookmark);
		
		try {
			JSONObject jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.INTENT_EXTRA_LOCATION_DETAILS));
			tvLocNameTitle.setText(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			tvLocName.setText(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			tvMembersFound.setText(jObj.getString(MMAPIConstants.JSON_KEY_MONKEYS) + MMAPIConstants.DEFAULT_SPACE + getString(R.string.tv_members_found));
			
			int[] icons = new int[]{R.drawable.telephone_icon, R.drawable.map_pin_icon, R.drawable.alarm_clock};
			String[] details = new String[3];
			details[0] = jObj.getString(MMAPIConstants.JSON_KEY_PHONENUMBER);
			details[1] = jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
						 jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE);
			details[2] = getString(R.string.tv_add_notifications);
			ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(SearchResultDetailsScreen.this, R.layout.expanded_listview_row, icons, details, android.R.style.TextAppearance_Small, Typeface.DEFAULT);
			elvLocDetails.setAdapter(arrayAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
