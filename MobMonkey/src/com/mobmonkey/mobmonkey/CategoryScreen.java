package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.SharedPreferences;

public class CategoryScreen extends Activity {

	private static final String TAG = "TAG";
	SharedPreferences userPrefs;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		
		int categoryID = getIntent().getIntExtra("category_id", 0);	
		
//		MMCategoriesAdapter.getSubCategoryListWithCategoryID(new SubCategoryCallBack(), categoryID, MMConstants.PARTNER_ID, userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	private class SubCategoryCallBack implements MMCallback {
		public void processCallback(Object obj) 
		{
			if(obj == null)
				Log.d(TAG, TAG + " Object is null");
			else 
				Log.d(TAG, TAG + " Object is NOT null");
			
		}
		
	}
}
