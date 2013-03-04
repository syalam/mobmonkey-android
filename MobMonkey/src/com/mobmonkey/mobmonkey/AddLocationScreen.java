package com.mobmonkey.mobmonkey;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
<<<<<<< HEAD
=======
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
>>>>>>> Added adapter to categorieslistview

import com.mobmonkey.mobmonkey.utils.MMCategories;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMAddLocationAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

/**
 * @author Dezapp, LLC
 *
 */
public class AddLocationScreen extends Activity {
	
	EditText etLocName;
	EditText etCats;
	EditText etStreet;
	EditText etCity;
	EditText etState;
	EditText etZip;
	EditText etPhone;
	String name, categories, street, city, state, postalCode, phone, latitude, longitude;
	
	JSONArray topLevelCats;
	
	Location location;
	LocationManager locationManager;
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor editPrefs;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_location_screen);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		editPrefs = userPrefs.edit();
		editPrefs.remove(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
		editPrefs.commit();
		location = new Location(LocationManager.NETWORK_PROVIDER);
		init();
	}
	
	protected void onResume()
	{
		super.onResume();
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST))
		{
			String displayCategoriesSelected = userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST, MMAPIConstants.DEFAULT_STRING);
			try {
				JSONArray selectedCategoriesList = new JSONArray(displayCategoriesSelected);
				displayCategoriesSelected = null;
				for(int i=0; i < selectedCategoriesList.length(); i++)
				{
					if(displayCategoriesSelected == null)
						displayCategoriesSelected = selectedCategoriesList.getJSONObject(i).getString("en");
					else
						displayCategoriesSelected = displayCategoriesSelected + ", " + selectedCategoriesList.getJSONObject(i).getString("en");
					
					if(categories == null)
						categories = selectedCategoriesList.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID);
					else
						categories = categories + "," + selectedCategoriesList.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID);
				}
				etCats.setText(displayCategoriesSelected);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void onBackPressed() {
	    if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST))
	    {
	    	editPrefs.remove(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
	    }
	    super.onBackPressed();
	}
	
	private void init(){
		
		//Initialize all of the text fields
		etLocName = (EditText)findViewById(R.id.etlocationname);
		etCats = (EditText)findViewById(R.id.etcategories);
		etStreet = (EditText)findViewById(R.id.etstreet);
		etCity = (EditText)findViewById(R.id.etcity);
		etState = (EditText)findViewById(R.id.etstate);
		etZip = (EditText)findViewById(R.id.etzip);
		etPhone = (EditText)findViewById(R.id.etphone);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		etCats.setInputType(InputType.TYPE_NULL);
		// check for bundle (location)
		checkLocationInfo();
		
		// open category list from editText touch event
		etCats.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Intent categoryScreenIntent = new Intent(AddLocationScreen.this, AddLocationCategoryList.class);
					try
					{
						topLevelCats = MMCategories.getTopLevelCategories(AddLocationScreen.this.getApplicationContext());
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}					
					categoryScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY, topLevelCats.toString());
					categoryScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, "Categories");
					startActivity(categoryScreenIntent);
				}
				return false;
			}
		});
	}

	public void viewOnClick(View view) throws JSONException {
    	switch(view.getId()) {
	    	case R.id.btnaddlocation:
	    		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST))
	    	    {
	    	     	editPrefs.remove(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
	    	    }
	    		addLocation();
	    		break;
    	}
    }

	private void addLocation() throws JSONException {
		if(checkValues())
		{
			MMAddLocationAdapter.addLocation(new AddLocationCallback(), userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
					userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID, street, "", "", 
					categories, "United States", latitude, city, longitude, name, "", 
					phone, postalCode, "", state, "");
		}
	}
	
	private boolean checkValues() {
		if(etLocName.getText().toString().isEmpty())
		{
			displayAlert(R.string.ad_no_name);
			return false;
		}
		else if(etCats.getText().toString().isEmpty())
		{
			displayAlert(R.string.ad_no_categories);
			return false;
		}
		else if(etStreet.getText().toString().isEmpty())
		{
			displayAlert(R.string.ad_no_street);
			return false;
		}
		else if(etCity.getText().toString().isEmpty())
		{
			displayAlert(R.string.ad_no_city);
			return false;
		}
		if(etState.getText().toString().isEmpty())
		{
			displayAlert(R.string.ad_no_state);
			return false;
		}
		if(etZip.getText().toString().isEmpty())
		{
			displayAlert(R.string.ad_no_zip);
			return false;
		}
		name = etLocName.getText().toString();
		street = etStreet.getText().toString();
		city = etCity.getText().toString();
		state = etState.getText().toString();
		postalCode = etZip.getText().toString();
		if(!etPhone.getText().toString().isEmpty())
			phone =  etPhone.getText().toString();
		else
			phone = "";
		return true;
	}
	
	private void displayAlert(int messageId) {
    	new AlertDialog.Builder(AddLocationScreen.this)
    		.setTitle(R.string.app_name)
    		.setMessage(messageId)
    		.setNeutralButton(android.R.string.ok, null)
    		.show();
    }
	
	private void checkLocationInfo() {
		if(this.getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			etStreet.setText(bundle.getString(MMAPIConstants.JSON_KEY_ADDRESS));
			etCity.setText(bundle.getString(MMAPIConstants.JSON_KEY_LOCALITY));
			etState.setText(bundle.getString(MMAPIConstants.JSON_KEY_REGION));
			etZip.setText(bundle.getString(MMAPIConstants.JSON_KEY_POSTCODE));
			latitude = bundle.getString(MMAPIConstants.JSON_KEY_LATITUDE);
			longitude = bundle.getString(MMAPIConstants.JSON_KEY_LONGITUDE);
			
			location.setLatitude(Double.parseDouble(bundle.getString(MMAPIConstants.JSON_KEY_LATITUDE)));
			location.setLongitude(Double.parseDouble(bundle.getString(MMAPIConstants.JSON_KEY_LONGITUDE)));
		}
	}
	
	private class AddLocationCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			
			if(obj != null) {
				try {
					Intent locationDetailsScreenIntent = new Intent(AddLocationScreen.this, SearchResultDetailsScreen.class);				
					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, (String) obj);
					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
					startActivity(locationDetailsScreenIntent);
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
}
