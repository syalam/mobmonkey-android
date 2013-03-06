package com.mobmonkey.mobmonkey;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchResultDetailsScreen extends Activity {
	JSONObject jObj;
	
	TextView tvBookmark;
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
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
			tvBookmark.setText(jObj.getBoolean("bookmark")? getString(R.string.tv_remove_bookmark):getString(R.string.tv_bookmark));
			 
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
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
	}

	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	}
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.llmakerequest:
				//startActivity(new Intent());
				Intent intent = new Intent(SearchResultDetailsScreen.this, MakeARequestScreen.class);
				intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
				startActivity(intent);
				break;
			case R.id.llbookmark:
				bookmarkClicked();
				break;
		}
	}
	
	private void bookmarkClicked() {
		ArrayList<JSONObject> bookmarkList = new ArrayList<JSONObject>();
		
		// create a arraylist from the existing bookmark list
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS)) {
			try {
				
				// add existing bookmark list to arraylist "bookmarkList.
				JSONArray bookmark = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING));
				for(int i = 0; i < bookmark.length(); i++) {
					bookmarkList.add((JSONObject) bookmark.get(i));
				}
				
				
				
			} catch(JSONException ex) {
				ex.printStackTrace();
			}
			
		}
		
		// TODO: check how to set the bookmark textview
		// add current location information to bookmark
		if(tvBookmark.getText().toString().equals(getString(R.string.tv_bookmark))) {
			tvBookmark.setText(R.string.tv_remove_bookmark);
			
			// add to share preferences 
			try {
				JSONObject jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
				bookmarkList.add(jObj);
				
				// update bookmark list in share preference 
				JSONArray newBookMark = new JSONArray(bookmarkList);
				userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, newBookMark.toString());
				userPrefsEditor.commit();
				
				// set "bookmark" to true
				JSONObject newLocationInfo = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
				newLocationInfo.put("bookmark", true);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		// remove current location information from bookmark
		} else if(tvBookmark.getText().toString().equals(getString(R.string.tv_remove_bookmark))) {
			tvBookmark.setText(R.string.tv_bookmark);
			
			// remove to share preferences 
			try {
				JSONObject jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
				
				// location id is always different! need to use name to remove bookmarks
				FindjObj:
				for(JSONObject j : bookmarkList) {
					if(j.getString("name").compareTo(jObj.getString("name")) == 0) {
						bookmarkList.remove(j);
						
						break FindjObj;
					}
				}
				
				// update bookmark list in share preference 
				JSONArray newBookMark = new JSONArray(bookmarkList);
				userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, newBookMark.toString());
				userPrefsEditor.commit();
				
				// set "bookmark" to false
				JSONObject newLocationInfo = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
				newLocationInfo.put("bookmark", false);
				getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// log
		try {
			JSONArray jar = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, ""));
			Log.d("SearchResultDetailsScreen", jar.length()+"");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
