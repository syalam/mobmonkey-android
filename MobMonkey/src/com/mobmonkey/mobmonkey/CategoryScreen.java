package com.mobmonkey.mobmonkey;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMCategoryAdapter;
import com.mobmonkey.mobmonkeyapi.adapters.MMSearchLocationAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

public class CategoryScreen extends Activity implements LocationListener {

	private static final String TAG = "Categories Screen ";
	final Context context = this;
	SharedPreferences userPrefs;
	
	LocationManager locationManager;
	Location location;
	double longitudeValue;
	double latitudeValue;
	
	ListView lvSubCategories;
	TextView tvNavigationBarText;
	
	ArrayList<String> subCategories = new ArrayList<String>();
	JSONArray categoriesArray;
	
	String searchCategory;
	String categoryId;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_screen);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		lvSubCategories = (ListView) findViewById(R.id.lvsubcategory);
		tvNavigationBarText = (TextView) findViewById(R.id.navtitle);
		tvNavigationBarText.setText(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		init();
	}
	
	private void init()
	{
		String categoryInformation = getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY);
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		getCurrentLocation();
		
		try 
		{
			categoriesArray = new JSONArray(categoryInformation);
			
			for(int i = 0; i < categoriesArray.length(); i++)
			{
				JSONObject category = categoriesArray.getJSONObject(i);
				subCategories.add(category.getString("en"));
			}
			
	        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, subCategories) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) 
				{
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
		
		lvSubCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) 
			{
				try 
				{
					JSONObject category = categoriesArray.getJSONObject(position);
					MMCategoryAdapter.getCategories(new CategoryCallback(), 
							category.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID), 
							userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
							userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
							MMConstants.PARTNER_ID);
					searchCategory = category.getString("en");
					categoryId = category.getString(MMAPIConstants.JSON_KEY_CATEGORY_ID);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private void getCurrentLocation() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, CategoryScreen.this);
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null) {
			longitudeValue = location.getLongitude();
			latitudeValue = location.getLatitude();
			DecimalFormat twoDForm = new DecimalFormat("#.######");
			latitudeValue = Double.valueOf(twoDForm.format(latitudeValue));
			longitudeValue = Double.valueOf(twoDForm.format(longitudeValue));
		}
	}
	
	private void displayAlertDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("MobMonkey");
		alertDialogBuilder.setMessage("No locations found");
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {				
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private class CategoryCallback implements MMCallback 
	{
		@Override
		public void processCallback(Object obj) 
		{
			if(obj == null)
			{
				Log.d(TAG, TAG + "CategoryCallback object is null");
			}
			else
			{
				try 
				{
					JSONArray subCategories = new JSONArray((String) obj);
				
					if(subCategories.isNull(0))
					{
						MMSearchLocationAdapter.searchLocationWithText(new SearchResultsCallback(), Double.toString(longitudeValue), Double.toString(latitudeValue), 1000, "", 
								userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID);
					}
					else
					{
						Intent subCategoriesIntent = new Intent(CategoryScreen.this, CategoryScreen.class);
						subCategoriesIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY, (String) obj);
						subCategoriesIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
						startActivity(subCategoriesIntent);
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class SearchResultsCallback implements MMCallback 
	{
		@Override
		public void processCallback(Object obj) 
		{
			if(obj == null)
				Log.d(TAG, TAG + "SearchResultsCallback is null");
			else
			{
				try {
					JSONArray searchResults = new JSONArray((String) obj);
					if(searchResults.isNull(0))
					{
						displayAlertDialog();
					}
					else
					{
						Intent searchResultsIntent = new Intent(CategoryScreen.this, SearchResultsScreen.class);
						searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_DISPLAY_MAP, true);
						searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
						searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
						searchResultsIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, (String) obj);
						startActivity(searchResultsIntent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
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
