package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMAddLocationAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class AddLocationScreen extends Activity {
	private static final String TAG = "AddLocationScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor editPrefs;
	
	private EditText etLocName;
	private EditText etCategories;
	private EditText etStreet;
	private EditText etCity;
	private EditText etState;
	private EditText etZip;
	private EditText etPhone;
	private String name, categoriesIds, street, city, state, postalCode, phone;
	private double latitude, longitude;
	
	private ArrayList<String> selectedCategories;
	private ArrayList<String> selectedCategoriesIds;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.add_location_screen);
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
		editPrefs = userPrefs.edit();
		editPrefs.commit();
		init();
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("Test", "categories: " + categoriesIds);
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
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_CATEGORY) {
			if(resultCode == RESULT_OK) {
				selectedCategories = data.getStringArrayListExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES);
				selectedCategoriesIds = data.getStringArrayListExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES_IDS);
				Log.d("AddLocationScreen", "Size: " + selectedCategories.size());
				
				if(selectedCategories.size() > 0) {
					String categories = MMSDKConstants.DEFAULT_STRING_EMPTY;
					categoriesIds = MMSDKConstants.DEFAULT_STRING_EMPTY;
					
					etCategories.setSingleLine(false);
					etCategories.setLines(selectedCategories.size());
					for(int i = 0; i < selectedCategories.size(); i++) {
						if(i != selectedCategories.size() - 1) {
							categories += selectedCategories.get(i) + MMSDKConstants.DEFAULT_STRING_COMMA_NEWLINE;
							categoriesIds += selectedCategoriesIds.get(i) + MMSDKConstants.DEFAULT_STRING_COMMA;
						} else {
							categories += selectedCategories.get(i);
							categoriesIds += selectedCategoriesIds.get(i);
						}
					}
					etCategories.setText(categories);
				} else {
					etCategories.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
					etCategories.setSingleLine();
					categoriesIds = MMSDKConstants.DEFAULT_STRING_EMPTY;
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}
	
	/**
	 * 
	 */
	private void init(){
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
		
		//Initialize all of the text fields
		etLocName = (EditText)findViewById(R.id.etlocationname);
		etCategories = (EditText)findViewById(R.id.etcategories);
		etStreet = (EditText)findViewById(R.id.etstreet);
		etCity = (EditText)findViewById(R.id.etcity);
		etState = (EditText)findViewById(R.id.etstate);
		etZip = (EditText)findViewById(R.id.etzip);
		etPhone = (EditText)findViewById(R.id.etphone);
		
		etCategories.setInputType(InputType.TYPE_NULL);
		
		selectedCategories = new ArrayList<String>();
		selectedCategoriesIds = new ArrayList<String>();
		
		// check for bundle (location)
		checkLocationInfo();
		
		// open category list from editText touch event
		etCategories.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					Intent categoryScreenIntent = new Intent(AddLocationScreen.this, AddLocationCategoryScreen.class);
					JSONArray categories = MMCategories.getTopLevelCategories(AddLocationScreen.this);
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY_TITLE, getString(R.string.tv_title_categories));
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORIES, categories.toString());
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES, selectedCategories);
					categoryScreenIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SELECTED_CATEGORIES_IDS, selectedCategoriesIds);
					startActivityForResult(categoryScreenIntent, MMSDKConstants.REQUEST_CODE_ADD_CATEGORY);
				}
				return false;
			}
		});
	}

	/**
	 * 
	 * @param view
	 * @throws JSONException
	 */
	public void viewOnClick(View view) throws JSONException {
    	switch(view.getId()) {
	    	case R.id.btnaddlocation:
	    		addLocation();
	    		break;
    	}
    }

	/**
	 * 
	 * @throws JSONException
	 */
	private void addLocation() throws JSONException {
		if(checkValues()) {
			MMAddLocationAdapter.addLocation(new AddLocationCallback(), 
											 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
											 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY), 
											 MMConstants.PARTNER_ID, 
											 street, 
											 MMSDKConstants.DEFAULT_STRING_EMPTY, 
											 MMSDKConstants.DEFAULT_STRING_EMPTY, 
											 categoriesIds, 
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
		else if(etCategories.getText().toString().isEmpty())
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
		}
	}
	
//	private int getCategoryId(String categoryName) throws JSONException {
//		int id = -1;
//		String name = catsName;
//		JSONArray topLevelCategories = MMCategories.getTopLevelCategories(AddLocationScreen.this);
//		if(isTopCats(name)) {
//			if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES)) {
//				JSONObject cats = new JSONObject(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMSDKConstants.DEFAULT_STRING_EMPTY));
//				JSONObject jObj = cats.getJSONArray(name).getJSONObject(0);
//				id = Integer.parseInt(jObj.getString(MMSDKConstants.JSON_KEY_CATEGORY_ID));
//			}
//		} else {
//			FindIDs:
//			for(int i = 0; i < topCatsName.length; i++) {
//				JSONArray jArr = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(this, topCatsName[i]));
//				for(int j = 0; j < jArr.length(); j++) {
//					JSONObject jObj = jArr.getJSONObject(j);
//					if(jObj.getString(Locale.getDefault().getLanguage()).equals(name)) {
//						id = Integer.parseInt(jObj.getString(MMSDKConstants.JSON_KEY_CATEGORY_ID));
//						break FindIDs;
//					}
//				}
//			}
//		}
//		
//		return id;
//	}
	
//	private boolean isTopCats(String name) throws JSONException {
//		JSONArray topCatsName = MMCategories.getTopLevelCategories(AddLocationScreen.this);
//		for(int i = 0; i < topCatsName.length(); i++) {
//			if(topCatsName.getJSONObject(i).getString(Locale.getDefault().getLanguage()).equals(name)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	private class AddLocationCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				try {
//					Intent locationDetailsScreenIntent = new Intent(AddLocationScreen.this, SearchResultDetailsScreen.class);				
//					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, (String) obj);
//					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
//					startActivity(locationDetailsScreenIntent);
					String response = (String) obj;
					
					if(response.equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(AddLocationScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					} else {
						finish();
						overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
}
