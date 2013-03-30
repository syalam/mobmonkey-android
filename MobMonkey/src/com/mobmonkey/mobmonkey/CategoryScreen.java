package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMCategories;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMSearchResultsCallback;
import com.mobmonkey.mobmonkeyapi.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

public class CategoryScreen extends Activity implements LocationListener {

	private static final String TAG = "Categories Screen ";
	SharedPreferences userPrefs;
	
	Location location;
	double longitudeValue;
	double latitudeValue;
	
	ListView lvSubCategories;
	TextView tvNavigationBarText;
	
	ArrayList<String> subCategories = new ArrayList<String>();
	JSONArray categoriesArray;
	
	String searchCategory;

	ProgressDialog progressDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_screen);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		lvSubCategories = (ListView) findViewById(R.id.lvsubcategory);
		tvNavigationBarText = (TextView) findViewById(R.id.navtitle);
		tvNavigationBarText.setText(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		init();
	}
	
	private void init() {
		String categoryInformation = getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		getCurrentLocation();
		
		try {
			categoriesArray = new JSONArray(categoryInformation);
			
			for(int i = 0; i < categoriesArray.length(); i++) {
				JSONObject category = categoriesArray.getJSONObject(i);
				subCategories.add(category.getString(Locale.getDefault().getLanguage()));
			}
			
	        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, subCategories) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					TextView eventText = (TextView) view.findViewById(android.R.id.text1);
					eventText.setTypeface(null, Typeface.BOLD);
					return view;
				}
	        };
	        
	        lvSubCategories.setAdapter(arrayAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		
		lvSubCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				try {
					JSONObject category = categoriesArray.getJSONObject(position);
					JSONArray subCategoriesArray = new JSONArray(MMCategories.getSubCategoriesWithCategoriId(CategoryScreen.this.getApplicationContext(), category.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID)));
					String categorySelected = category.getString(Locale.getDefault().getLanguage());
					
					if(!subCategoriesArray.isNull(0)) {
						Intent subCategoriesIntent = new Intent(CategoryScreen.this, CategoryScreen.class);
						subCategoriesIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY, subCategoriesArray.toString());
						subCategoriesIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, categorySelected);
						startActivity(subCategoriesIntent);
					} else {
						progressDialog = ProgressDialog.show(CategoryScreen.this, MMAPIConstants.DEFAULT_STRING, "Locating " + categorySelected);

						MMSearchLocationAdapter.searchLocationWithText(
								new MMSearchResultsCallback(CategoryScreen.this, progressDialog, categorySelected), 
								Double.toString(longitudeValue), 
								Double.toString(latitudeValue), 
								userPrefs.getInt(MMAPIConstants.SHARED_PREFS_KEY_SEARCH_RADIUS, MMAPIConstants.SEARCH_RADIUS_HALF_MILE), 
								MMAPIConstants.DEFAULT_STRING,
								category.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID),
								userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
								userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
								MMConstants.PARTNER_ID);
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private void getCurrentLocation() {
		if(location != null) {
			longitudeValue = location.getLongitude();
			latitudeValue = location.getLatitude();
			DecimalFormat decimalFormat = new DecimalFormat("#.######");
			latitudeValue = Double.valueOf(decimalFormat.format(latitudeValue));
			longitudeValue = Double.valueOf(decimalFormat.format(longitudeValue));
		}
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
