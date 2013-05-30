package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeysdk.adapters.MMGeocoderAdapter;
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
														   OnCheckedChangeListener,
														   OnKeyListener {
	private static final String TAG = "AddLocationScreen: ";
	
	private SharedPreferences userPrefs;
	private InputMethodManager inputMethodManager;
	
	private EditText etLocName;
	private EditText etCategories;
	private ToggleButton tbAddAddress;
	private LinearLayout llAddress;
	private EditText etStreet;
	private EditText etCity;
	private EditText etState;
	private EditText etZip;
	private EditText etPhone;
	private String categoriesIds;
	
	private ArrayList<String> selectedCategories;
	private ArrayList<String> selectedCategoriesIds;
	
	private boolean addAddress;
	private boolean useCurrentLocation;
	
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
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		addAddress = isChecked;
		if(isChecked) {
			llAddress.setVisibility(View.VISIBLE);
		} else {
			llAddress.setVisibility(View.GONE);
		}
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
		etLocName = (EditText) findViewById(R.id.etlocationname);
		etCategories = (EditText) findViewById(R.id.etcategories);
		tbAddAddress = (ToggleButton) findViewById(R.id.tbaddaddress);
		llAddress = (LinearLayout) findViewById(R.id.lladdress);
		etStreet = (EditText) findViewById(R.id.etstreet);
		etCity = (EditText) findViewById(R.id.etcity);
		etState = (EditText) findViewById(R.id.etstate);
		etZip = (EditText) findViewById(R.id.etzip);
		etPhone = (EditText) findViewById(R.id.etphone);
		
		etCategories.setOnTouchListener(AddLocationScreen.this);
		tbAddAddress.setOnCheckedChangeListener(AddLocationScreen.this);
		etPhone.setOnKeyListener(AddLocationScreen.this);
		
		selectedCategories = new ArrayList<String>();
		selectedCategoriesIds = new ArrayList<String>();
		
		setLocationInfo();
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
		if(addAddress) {
			if(checkAddress()) {
				validateAddress();
			} else {
				displayAlert(R.string.ad_title_no_location_found, R.string.ad_message_no_location_found);
			}
		} else {
			if(checkAddress()) {
				checkNameAndCategories();
			} else {
				displayNoAddressAlert();
			}
		}
	}
	
	/**
	 * 
	 */
	private void checkNameAndCategories() {
		if(checkName()) {
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
										  null);
			MMProgressDialog.displayDialog(AddLocationScreen.this,
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_adding_location));
		}
	}
	
	private void validateAddress() {
		String address = etStreet.getText().toString() +
				MMSDKConstants.DEFAULT_STRING_COMMA_SPACE +
				etCity.getText().toString() +
				MMSDKConstants.DEFAULT_STRING_COMMA_SPACE +
				etState.getText().toString() +
				MMSDKConstants.DEFAULT_STRING_SPACE +
				etZip.getText().toString();
		Log.d(TAG, TAG + "address: " + address);
		MMGeocoderAdapter.getFromLocationName(AddLocationScreen.this,
											  new ValidateAddressCallback(),
											  address);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkName() {
		if(!TextUtils.isEmpty(etLocName.getText().toString())) {
			return checkCategories();
		} else {
			displayAlert(R.string.app_name, R.string.ad_message_no_name);
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkCategories() {
		if(!TextUtils.isEmpty(etCategories.getText().toString())) {
			return true;
		} else {
			displayAlert(R.string.app_name, R.string.ad_message_no_categories);
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkAddress() {
		if(!TextUtils.isEmpty(etStreet.getText().toString())) {
			return checkCity();
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkCity() {
		if(!TextUtils.isEmpty(etCity.getText().toString())) {
			return checkState();
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkState() {
		if(!TextUtils.isEmpty(etState.getText().toString())) {
			return checkZip();
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkZip() {
		if(!TextUtils.isEmpty(etZip.getText().toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 */
	private void displayNoAddressAlert() {
		AlertDialog alertDialog = new AlertDialog.Builder(AddLocationScreen.this)
			.setTitle(R.string.ad_title_no_address)
			.setMessage(R.string.ad_message_no_address)
			.setPositiveButton(R.string.ad_btn_current_location, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					useCurrentLocation = true;
					checkNameAndCategories();
				}
			})
			.setNegativeButton(R.string.ad_btn_cancel, null)
			.show();
		
		TextView tvMessage = (TextView) alertDialog.findViewById(android.R.id.message);
		tvMessage.setGravity(Gravity.CENTER);
		alertDialog.show();
	}
	
	/**
	 * 
	 * @param messageId
	 */
	private void displayAlert(int titleId, int messageId) {
    	AlertDialog alertDialog = new AlertDialog.Builder(AddLocationScreen.this)
    		.setTitle(titleId)
    		.setMessage(messageId)
    		.setNeutralButton(android.R.string.ok, null)
    		.show();
    	
    	TextView tvMessage = (TextView) alertDialog.findViewById(android.R.id.message);
    	tvMessage.setGravity(Gravity.CENTER);
    	alertDialog.show();
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
	
	/**
	 * 
	 * @return
	 */
	private double getLatitude() {
		if(useCurrentLocation) {
			return MMLocationManager.getLocationLatitude();
		} else if(getIntent().hasExtra(MMSDKConstants.JSON_KEY_LATITUDE)) {
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
	
	/**
	 * 
	 * @return
	 */
	private double getLongitude() {
		if(useCurrentLocation) {
			return MMLocationManager.getLocationLongitude();
		} else if(getIntent().hasExtra(MMSDKConstants.JSON_KEY_LONGITUDE)) {
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
	
	private class ValidateAddressCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(AddLocationScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					} else if(((String) obj).equals(MMSDKConstants.SERVICE_NOT_AVAILABLE)) {
						Toast.makeText(AddLocationScreen.this, R.string.toast_service_not_available, Toast.LENGTH_LONG).show();
					}
				} else if(obj instanceof Address) {
					checkNameAndCategories();
				}
			} else {
				displayAlert(R.string.ad_title_no_location_found, R.string.ad_message_no_location_found);
			}
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
