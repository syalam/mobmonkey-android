package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.adapters.MMCategoriesAdapter;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.SharedPreferences;

public class SubCategoryScreen extends Activity {

	private static final String TAG = "TAG";
	SharedPreferences userPrefs;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_category);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		int categoryID = getIntent().getIntExtra("category_id", 0);	
		
		MMCategoriesAdapter.getSubCategoryListWithCategoryID(new SubCategoryCallBack(), categoryID, MMConstants.PARTNER_ID, userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	private class SubCategoryCallBack implements MMCallback
	{
		public void processCallback(Object arg0) 
		{
			if(arg0 == null)
				Log.d(TAG, TAG + " Object is null");
			else 
				Log.d(TAG, TAG + " Object is NOT null");
			
		}
		
	}
}
