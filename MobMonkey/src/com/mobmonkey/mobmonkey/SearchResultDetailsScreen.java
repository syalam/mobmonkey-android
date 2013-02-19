package com.mobmonkey.mobmonkey;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.ExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
			JSONObject jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
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
			elvLocDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					if(position == 0) {
						Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
						dialerIntent.setData(Uri.parse("tel:" + ((TextView)view.findViewById(R.id.tvcategory)).getText().toString()));
						startActivity(dialerIntent);
					} else if(position == 1) {
						Intent mapIntent = new Intent(SearchResultDetailsScreen.this, SearchLocationResultMapScreen.class);
						startActivity(mapIntent);
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
