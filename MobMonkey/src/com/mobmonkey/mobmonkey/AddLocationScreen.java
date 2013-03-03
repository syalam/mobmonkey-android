package com.mobmonkey.mobmonkey;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMAddLocationAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
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
	SharedPreferences userPrefs;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_location_screen);
		init();
	}
	
	private void init() {
		
		//Initialize all of the text fields
		etLocName = (EditText)findViewById(R.id.etlocationname);
		etCats = (EditText)findViewById(R.id.etcategories);
		etStreet = (EditText)findViewById(R.id.etstreet);
		etCity = (EditText)findViewById(R.id.etcity);
		etState = (EditText)findViewById(R.id.etstate);
		etZip = (EditText)findViewById(R.id.etzip);
		etPhone = (EditText)findViewById(R.id.etphone);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		// check for bundle (location)
		checkLocationInfo();
		
	}

	public void viewOnClick(View view) {
    	switch(view.getId()) {
	    	case R.id.btnaddlocation:
	    		addLocation();
	    		break;
	    	case R.id.etcategories:
	    		loadCategory();
	    		break;
    	}
    }

	private void loadCategory() {
		Intent intent = new Intent(this, CategoryListScreen.class);
		startActivityForResult(intent, RESULT_OK);
		
	}

	private void addLocation() {
		if(checkValues())
		{
			// dummy categories
			categories = "342";
			String a = userPrefs.getString(MMAPIConstants.KEY_USER, null); 
			String b = userPrefs.getString(MMAPIConstants.KEY_AUTH, null);
			MMAddLocationAdapter.addLocation(new AddLocationCallback(), userPrefs.getString(MMAPIConstants.KEY_USER, null), 
					userPrefs.getString(MMAPIConstants.KEY_AUTH, null), MMConstants.PARTNER_ID, street, "", "", 
					categories, "US", latitude, city, longitude, name, "", 
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
		categories = etCats.getText().toString();
		street = etStreet.getText().toString();
		city = etCity.getText().toString();
		state = etState.getText().toString();
		postalCode = etZip.getText().toString();
		if(!etPhone.getText().toString().isEmpty())
		{
			phone =  etPhone.getText().toString();
		}
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
		}
	}
	
	private class AddLocationCallback implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			/*
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			*/
			
			if(obj != null) {
				try {
					JSONObject response = new JSONObject((String) obj);
					
					if(response.getString(MMAPIConstants.KEY_RESPONSE_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
						Toast.makeText(AddLocationScreen.this, "Location Added.", Toast.LENGTH_SHORT).show();
						finish();
					}
					else {
						Toast.makeText(AddLocationScreen.this, "FAIL.", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
}
