/**
 * 
 */
package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMResultsLocation;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsArrayAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchResultsScreen extends Activity {
	JSONArray searchResults;
	MMResultsLocation[] locations;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results_screen);
		
		TextView tvSearchResultsTitle = (TextView) findViewById(R.id.tvsearchresultstitle);
		ListView lvSearchResults = (ListView) findViewById(R.id.lvsearchresults);
		
		tvSearchResultsTitle.setText(getIntent().getStringExtra(MMAPIConstants.INTENT_EXTRA_SEARCH_RESULT_TITLE));
		try {
			searchResults = new JSONArray(getIntent().getStringExtra(MMAPIConstants.INTENT_EXTRA_SEARCH_RESULTS));
			getLocations();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ArrayAdapter<MMResultsLocation> arrayAdapter = new MMSearchResultsArrayAdapter(SearchResultsScreen.this, R.layout.search_result_list_row, locations);
		lvSearchResults.setAdapter(arrayAdapter);
		lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				try {
					Intent locDetailsIntent = new Intent(SearchResultsScreen.this, SearchResultDetailsScreen.class);
					locDetailsIntent.putExtra(MMAPIConstants.INTENT_EXTRA_LOCATION_DETAILS, searchResults.getJSONObject(position).toString());
					startActivity(locDetailsIntent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void getLocations() throws JSONException {
			locations = new MMResultsLocation[searchResults.length()];
			for(int i = 0; i < searchResults.length(); i++) {
				JSONObject jObj = searchResults.getJSONObject(i);
				locations[i] = new MMResultsLocation();
				locations[i].setLocName(jObj.getString(MMAPIConstants.JSON_KEY_NAME));
				locations[i].setLocDist(convertMetersToMiles(jObj.getString(MMAPIConstants.JSON_KEY_DISTANCE)) + getString(R.string.miles));
				locations[i].setLocAddr(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + jObj.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
										jObj.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + jObj.getString(MMAPIConstants.JSON_KEY_POSTCODE));
			}
	}
	
	/**
	 * 
	 * @param distance
	 * @return
	 */
	private String convertMetersToMiles(String distance) {
		double dist = Double.valueOf(distance);
		
		dist = dist * 0.000621371f;
		
		return new DecimalFormat("#.##").format(dist) + " ";
	}
}
