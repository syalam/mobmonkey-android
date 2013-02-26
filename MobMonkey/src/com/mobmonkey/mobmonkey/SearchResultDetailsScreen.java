package com.mobmonkey.mobmonkey;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMExpandedListView;
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
	JSONObject jObj;
	
	TextView tvBookmark;
	
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
		MMExpandedListView elvLocDetails = (MMExpandedListView) findViewById(R.id.elvlocdetails);
		tvBookmark = (TextView) findViewById(R.id.tvbookmark);
		
		try {
			jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			tvLocNameTitle.setText(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			tvLocName.setText(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
			tvMembersFound.setText(jObj.getString(MMAPIConstants.JSON_KEY_MONKEYS) + MMAPIConstants.DEFAULT_SPACE + getString(R.string.tv_members_found));
			
			int[] icons = new int[]{R.drawable.cat_icon_telephone, R.drawable.cat_icon_map_pin, R.drawable.cat_icon_alarm_clock};
			int[] indicatorIcons = new int[]{R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator};
			String[] details = new String[3];
			details[0] = jObj.getString(MMAPIConstants.JSON_KEY_PHONENUMBER);
			details[1] = jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
						 jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE);
			details[2] = getString(R.string.tv_add_notifications);
			ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(SearchResultDetailsScreen.this, R.layout.mm_listview_row, icons, details, indicatorIcons, android.R.style.TextAppearance_Small, Typeface.DEFAULT, null);
			elvLocDetails.setAdapter(arrayAdapter);
			elvLocDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					if(position == 0) {
						Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
						dialerIntent.setData(Uri.parse("tel:" + ((TextView)view.findViewById(R.id.tvlabel)).getText().toString()));
						startActivity(dialerIntent);
					} else if(position == 1) {
						Intent mapIntent = new Intent(SearchResultDetailsScreen.this, SearchLocationResultMapScreen.class);
						mapIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, getIntent().getParcelableExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION));
						mapIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
						startActivity(mapIntent);
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.llmakerequest:
				startActivity(new Intent(SearchResultDetailsScreen.this, MakeARequestScreen.class));
				break;
			case R.id.llbookmark:
				bookmarkClicked();
				break;
		}
	}
	
	private void bookmarkClicked() {
		if(tvBookmark.getText().toString().equals(getString(R.string.tv_bookmark))) {
			tvBookmark.setText(R.string.tv_remove_bookmark);
		} else if(tvBookmark.getText().toString().equals(getString(R.string.tv_remove_bookmark))) {
			tvBookmark.setText(R.string.tv_bookmark);
		}
	}
}
