package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
		
		
	}
	
	private void checkLocationInfo() {
		if(this.getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			etStreet.setText(bundle.getString(MMAPIConstants.JSON_KEY_ADDRESS));
			etCity.setText(bundle.getString(MMAPIConstants.JSON_KEY_LOCALITY));
			etState.setText(bundle.getString(MMAPIConstants.JSON_KEY_REGION));
			etZip.setText(bundle.getString(MMAPIConstants.JSON_KEY_POSTCODE));
		}
	}
}
