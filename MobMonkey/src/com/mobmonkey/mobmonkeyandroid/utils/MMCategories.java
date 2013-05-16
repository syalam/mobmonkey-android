package com.mobmonkey.mobmonkeyandroid.utils;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MMCategories extends Activity {
	private static final String TAG = "MMCategories: ";
	
	private static SharedPreferences userPrefs;
	
	public static JSONArray getTopLevelCategories(Context context) {
		userPrefs = context.getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		String allCategories = userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMSDKConstants.DEFAULT_STRING_EMPTY);
		JSONArray topLevelCategories = new JSONArray();
		
		if(!allCategories.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			try {
				JSONObject allCats = new JSONObject(allCategories);
				JSONArray topLevelCatNames = allCats.names();
//				Log.d(TAG, TAG + "allCats: " + allCats.toString());
//				Log.d(TAG, TAG + "catNames: " + catNames.toString());
//				catNames.getString(i)
//				Iterator iterator = allCats.keys();
//				while(iterator.hasNext()) {
				for(int i = 0; i < topLevelCatNames.length(); i++) {
					JSONObject temp = new JSONObject();
					JSONArray tempCats = allCats.getJSONArray(topLevelCatNames.getString(i));
					temp.put(Locale.getDefault().getLanguage(), (String) topLevelCatNames.getString(i));
					temp.put(MMSDKConstants.JSON_KEY_CATEGORY_ID, tempCats.getJSONObject(0).getString(MMSDKConstants.JSON_KEY_CATEGORY_ID));
					temp.put((String) topLevelCatNames.getString(i), tempCats);
					topLevelCategories.put(temp);
				}
				Log.d(TAG, TAG + "topLevelCategories: " + topLevelCategories.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return topLevelCategories;
	}
	
	public static JSONArray getSubCategoriesWithCategoryName(Context context, String categoryName, JSONArray category){
		userPrefs = context.getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
		JSONArray subCategories = null;
		
		try {
			subCategories = new JSONArray();
			for(int i = 0; i < category.length(); i++) {
				if(categoryName.equals(category.getJSONObject(i).getString(Locale.getDefault().getLanguage()))) {
					subCategories = category.getJSONObject(i).getJSONArray(categoryName);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return subCategories;
	}
	
	public static void addCategory(String categoryId) {
		
	}
}
