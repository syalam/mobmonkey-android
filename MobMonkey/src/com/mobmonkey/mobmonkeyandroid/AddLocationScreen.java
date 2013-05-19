package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMLocationAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class AddLocationScreen extends Activity implements OnTouchListener,
														   OnKeyListener {
	private static final String TAG = "AddLocationScreen: ";
	
	private SharedPreferences userPrefs;
	private InputMethodManager inputMethodManager;
	
	private EditText etLocName;
	private EditText etCategories;
	private EditText etStreet;
	private EditText etCity;
	private EditText etState;
	private EditText etZip;
	private EditText etPhone;
	private String categoriesIds;
	
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
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, TAG + "categories: " + categoriesIds);
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
	
	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
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
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
			inputMethodManager.hideSoftInputFromWindow(etPhone.getWindowToken(), 0);
			return true;
		}
		return false;
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
		
		etPhone.setOnKeyListener(AddLocationScreen.this);
		
		selectedCategories = new ArrayList<String>();
		selectedCategoriesIds = new ArrayList<String>();
		
		// check for bundle (location)
		setLocationInfo();
		
		// open category list from editText touch event
		etCategories.setOnTouchListener(AddLocationScreen.this);
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
			MMLocationAdapter.addLocation(new AddLocationCallback(),
										  etStreet.getText().toString(),
										  null,
										  null,
										  categoriesIds,
										  getIntent().getStringExtra(MMSDKConstants.JSON_KEY_COUNTRY_CODE),
										  getLatitude(),
										  etCity.getText().toString(),
										  getLongitude(),
										  etLocName.getText().toString(),
										  MMSDKConstants.DEFAULT_STRING_EMPTY,
										  etPhone.getText().toString(),
										  etZip.getText().toString(),
										  etState.getText().toString(),
										  MMConstants.PROVIDER_ID,
										  "www.mobmonkey.com", // TODO: hardcoded
										  MMConstants.PARTNER_ID,
										  userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										  userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			MMProgressDialog.displayDialog(AddLocationScreen.this,
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_adding_location));
		}
	}
	
	private boolean checkValues() {
		if(!TextUtils.isEmpty(etLocName.getText().toString())) {
			return checkCategories();
		} else {
			displayAlert(R.string.ad_no_name);
			return false;
		}
	}
	
	private boolean checkCategories() {
		if(!TextUtils.isEmpty(etCategories.getText().toString())) {
			return checkStreet();
		} else {
			displayAlert(R.string.ad_no_categories);
			return false;
		}
	}
	
	private boolean checkStreet() {
		if(!TextUtils.isEmpty(etStreet.getText().toString())) {
			return checkCity();
		} else {
			displayAlert(R.string.ad_no_street);
			return false;
		}
	}
	
	private boolean checkCity() {
		if(!TextUtils.isEmpty(etCity.getText().toString())) {
			return checkState();
		} else {
			displayAlert(R.string.ad_no_city);
			return false;
		}
	}
	
	private boolean checkState() {
		if(!TextUtils.isEmpty(etState.getText().toString())) {
			return checkZip();
		} else {
			displayAlert(R.string.ad_no_state);
			return false;
		}
	}
	
	private boolean checkZip() {
		if(!TextUtils.isEmpty(etZip.getText().toString())) {
			return true;
		} else {
			displayAlert(R.string.ad_no_zip);
			return false;
		}
	}
	
	/**
	 * 
	 * @param messageId
	 */
	private void displayAlert(int messageId) {
    	new AlertDialog.Builder(AddLocationScreen.this)
    		.setTitle(R.string.app_name)
    		.setMessage(messageId)
    		.setNeutralButton(android.R.string.ok, null)
    		.show();
    }
	
	/**
	 * 
	 */
	private void setLocationInfo() {
		etStreet.setText(getIntent().getStringExtra(MMSDKConstants.JSON_KEY_ADDRESS));
		etCity.setText(getIntent().getStringExtra(MMSDKConstants.JSON_KEY_LOCALITY));
		etState.setText(getIntent().getStringExtra(MMSDKConstants.JSON_KEY_REGION));
		etZip.setText(getIntent().getStringExtra(MMSDKConstants.JSON_KEY_POSTCODE));
	}
	
	private double getLatitude() {
		if(getIntent().hasExtra(MMSDKConstants.JSON_KEY_LATITUDE)) {
			double latitude = getIntent().getDoubleExtra(MMSDKConstants.JSON_KEY_LATITUDE, MMSDKConstants.DEFAULT_DOUBLE); 
			if(latitude >= MMSDKConstants.DEFAULT_DOUBLE_ZERO) {
				return latitude;
			} else {
				return MMLocationManager.getLocationLatitude();
			}
		} else {
			return MMLocationManager.getLocationLatitude();
		}
	}
	
	private double getLongitude() {
		if(getIntent().hasExtra(MMSDKConstants.JSON_KEY_LONGITUDE)) {
			double longitude = getIntent().getDoubleExtra(MMSDKConstants.JSON_KEY_LONGITUDE, MMSDKConstants.DEFAULT_DOUBLE); 
			if(longitude >= MMSDKConstants.DEFAULT_DOUBLE_ZERO) {
				return longitude;
			} else {
				return MMLocationManager.getLocationLongitude();
			}
		} else {
			return MMLocationManager.getLocationLongitude();
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class AddLocationCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "add location: " + (String) obj);
				try {
//					Intent locationDetailsScreenIntent = new Intent(AddLocationScreen.this, SearchResultDetailsScreen.class);				
//					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, (String) obj);
//					locationDetailsScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION, location);
//					startActivity(locationDetailsScreenIntent);
					String response = (String) obj;
					
					if(response.equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(AddLocationScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jObj = new JSONObject((String) obj);
						Intent intent = new Intent();
						Log.d(TAG, TAG + "requestCode: " + getIntent().getIntExtra(MMSDKConstants.REQUEST_CODE, MMSDKConstants.DEFAULT_INT));
						intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
						setResult(RESULT_OK, intent);
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
