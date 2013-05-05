package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMAddLocationAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class AddLocationScreen extends Activity {
	private EditText etLocName;
	private EditText etCats;
	private EditText etStreet;
	private EditText etCity;
	private EditText etState;
	private EditText etZip;
	private EditText etPhone;
	private String name, categories, street, city, state, postalCode, phone;
	double latitude, longitude;
	
	String[] topLevelCats;
	
//	Location location;
	LocationManager locationManager;
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor editPrefs;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.add_location_screen);
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
		editPrefs = userPrefs.edit();
		editPrefs.remove(MMSDKConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
		editPrefs.commit();
//		location = new Location(LocationManager.NETWORK_PROVIDER);
		init();
	}
	
	protected void onResume() {
		super.onResume();
		
		
		
		Log.d("Test", "categories: "+categories);
//		if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_CATEGORY_LIST)) {
//			String displayCategoriesSelected = userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_CATEGORY_LIST, MMSDKConstants.DEFAULT_STRING_EMPTY);
//			try {
//				JSONArray selectedCategoriesList = new JSONArray(displayCategoriesSelected);
//				displayCategoriesSelected = null;
//				for(int i=0; i < selectedCategoriesList.length(); i++)
//				{
//					if(displayCategoriesSelected == null)
//						displayCategoriesSelected = selectedCategoriesList.getJSONObject(i).getString("en");
//					else
//						displayCategoriesSelected = displayCategoriesSelected + ", " + selectedCategoriesList.getJSONObject(i).getString("en");
//					
//					if(categories == null)
//						categories = selectedCategoriesList.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_CATEGORY_ID);
//					else
//						categories = categories + "," + selectedCategoriesList.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_CATEGORY_ID);
//				}
//				etCats.setText(displayCategoriesSelected);
//				
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			
//		}
	}
	
	@Override
	public void onBackPressed() {
	    if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_CATEGORY_LIST)) {
	    	editPrefs.remove(MMSDKConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
	    }
	    super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
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
		
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
		
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
					
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY, topLevelCats);
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, "Categories");
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_ADD_CATEGORY, new ArrayList<String>());
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_ADD_CATEGORY_IDS, new ArrayList<Integer>());
					startActivityForResult(categoryScreenIntent, MMSDKConstants.REQUEST_CODE_ADD_CATEGORY);
				}
				return false;
			}
		});
	}

	public void viewOnClick(View view) throws JSONException {
    	switch(view.getId()) {
	    	case R.id.btnaddlocation:
	    		if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_CATEGORY_LIST))
	    	    {
	    	     	editPrefs.remove(MMSDKConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
	    	    }
	    		addLocation();
	    		break;
    	}
    }

	private void addLocation() throws JSONException {
		if(checkValues()) {
			MMAddLocationAdapter.addLocation(new AddLocationCallback(), 
											 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
											 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY), 
											 MMConstants.PARTNER_ID, 
											 street, 
											 MMSDKConstants.DEFAULT_STRING_EMPTY, 
											 MMSDKConstants.DEFAULT_STRING_EMPTY, 
											 categories, 
											 "United States", 
											 latitude, 
											 city, 
											 longitude, 
											 name, 
											 MMSDKConstants.DEFAULT_STRING_EMPTY, 
											 phone, 
											 postalCode, 
											 MMSDKConstants.DEFAULT_STRING_EMPTY, 
											 state, 
											 MMSDKConstants.DEFAULT_STRING_EMPTY);
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
			phone = MMSDKConstants.DEFAULT_STRING_EMPTY;
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
			etStreet.setText(bundle.getString(MMSDKConstants.JSON_KEY_ADDRESS));
			etCity.setText(bundle.getString(MMSDKConstants.JSON_KEY_LOCALITY));
			etState.setText(bundle.getString(MMSDKConstants.JSON_KEY_REGION));
			etZip.setText(bundle.getString(MMSDKConstants.JSON_KEY_POSTCODE));
			latitude = bundle.getDouble(MMSDKConstants.JSON_KEY_LATITUDE);
			longitude = bundle.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE);
			
//			location.setLatitude(Double.parseDouble(bundle.getString(MMSDKConstants.JSON_KEY_LATITUDE)));
//			location.setLongitude(Double.parseDouble(bundle.getString(MMSDKConstants.JSON_KEY_LONGITUDE)));
		}
	}
	
	private class AddLocationCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			
			if(obj != null) {
				try {
//					Intent locationDetailsScreenIntent = new Intent(AddLocationScreen.this, SearchResultDetailsScreen.class);				
//					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, (String) obj);
//					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
//					startActivity(locationDetailsScreenIntent);
					finish();
					overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_CATEGORY) {
			if(resultCode == RESULT_OK) {
				ArrayList<String> selectedCategories = (ArrayList<String>)data.getSerializableExtra(MMSDKConstants.KEY_INTENT_EXTRA_ADD_CATEGORY);
				Log.d("AddLocationScreen", "Size: " + selectedCategories.size());
				
				if(selectedCategories.size() > 0) {
					String cats = MMSDKConstants.DEFAULT_STRING_EMPTY;
					etCats.setSingleLine(false);
					etCats.setLines(selectedCategories.size());
					for(int i = 0; i < selectedCategories.size(); i++) {
						if(i != selectedCategories.size() - 1) {
							cats += selectedCategories.get(i) + "\n";
						} else {
							cats += selectedCategories.get(i);
						}
					}
					etCats.setText(cats);
					
					// set categories
					categories = "";
					for(String catsName : selectedCategories) {
						try {
							categories += getCategoriesId(catsName) + ",";
	//						Log.d("Test", catsName + ": " + getCategoriesId(catsName));
						} catch (Exception ex) {
							ex.printStackTrace();
	//						Log.d("Test", "error at finding cats ids");
						}
					}
					categories = categories.substring(0, categories.length()-1);
					Log.d("Test", "categories id: " + categories);
				}
			}
		}
	}
	
	private int getCategoriesId(String catsName) throws JSONException {
		int id = -1;
		String name = catsName;
		String[] topCatsName = MMCategories.getTopLevelCategories(this);
		
		if(isTopCats(name)) {
			if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES)) {
				JSONObject cats = new JSONObject(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMSDKConstants.DEFAULT_STRING_EMPTY));
				JSONObject jObj = cats.getJSONArray(name).getJSONObject(0);
				id = Integer.parseInt(jObj.getString(MMSDKConstants.JSON_KEY_CATEGORY_ID));
			}
		} else {
			FindIDs:
			for(int i = 0; i < topCatsName.length; i++) {
				JSONArray jArr = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(this, topCatsName[i]));
				for(int j = 0; j < jArr.length(); j++) {
					JSONObject jObj = jArr.getJSONObject(j);
					if(jObj.getString("en").equals(name)) {
						id = Integer.parseInt(jObj.getString(MMSDKConstants.JSON_KEY_CATEGORY_ID));
						break FindIDs;
					}
				}
			}
		}
		
		return id;
	}
	
	private boolean isTopCats(String name) throws JSONException {
		String[] topCatsName = MMCategories.getTopLevelCategories(this);
		boolean flag = false;
		isTop:
		for(String str : topCatsName) {
			if(str.equals(name)) {
				flag = true;
				break isTop;
			}
		}
		return flag;
	}
}
