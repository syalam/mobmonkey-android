package com.mobmonkey.mobmonkey;

import org.json.JSONArray;
import org.json.JSONException;

import com.mobmonkey.mobmonkey.utils.MMCategories;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
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
	
	JSONArray topLevelCats;
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor editPrefs;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_location_screen);
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		editPrefs = userPrefs.edit();
		editPrefs.remove(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
		editPrefs.commit();
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
				displayCategoriesSelected = "";
				for(int i=0; i < selectedCategoriesList.length(); i++)
				{
					displayCategoriesSelected = displayCategoriesSelected + ", " + selectedCategoriesList.getJSONObject(i).getString("en");
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
	    return;
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

	public void viewOnClick(View view) {
    	switch(view.getId()) {
	    	case R.id.btnaddlocation:
	    		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST))
	    	    {
	    	    	editPrefs.remove(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST);
	    	    }
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
