package com.mobmonkey.mobmonkeyandroid.utils;

import java.util.Iterator;
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
	
//	public static String[] getTopLevelCategories(Context context) throws JSONException {
//		userPrefs = context.getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
//		
//		String[] topLevelCategoriesList;
//		
//		if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES)) {
//			JSONObject cats = new JSONObject(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMSDKConstants.DEFAULT_STRING_EMPTY));
//			topLevelCategoriesList = getKeys(cats);
////			JSONArray allCategories = cats.toJSONArray(cats.names());
////			JSONObject category = null;
////			for(int i=0; i < allCategories.length(); i++)
////			{
////				category = allCategories.getJSONArray(i).getJSONObject(0);
////				Log.d("Cat Parents:", category.getString("parents"));
////				if(category.getString("parents").compareTo("1") == 0)
////					topLevelCategoriesList.put(category);
////			}
//		}
//		else
//			return new String[0];
//		
//		return topLevelCategoriesList;
//	}
	
//	private static String[] getKeys(JSONObject cats) {
//		int length = cats.length();
//	    if (length == 0) {
//	        return new String[0];
//	    }
//	    Iterator iterator = cats.keys();
//	    String[] names = new String[length];
//	    int i = 0;
//	    while (iterator.hasNext()) {
//	        names[i] = (String)iterator.next();
//	        i += 1;
//	    }
//	    return names;
//	}
	
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
	
//	public static JSONArray getSubCategoriesWithCategoriId(Context context, JSONArray categoryArray) throws JSONException {
////		userPrefs = context.getSharedPreferences(MMSDKConstants.USER_PREFS, 0);
//		care
//		JSONArray subCategories = null;
//
////		if(userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES)) {
//		subCategories = new JSONArray();
//		JSONObject cats = new JSONObject(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMSDKConstants.DEFAULT_STRING_EMPTY));
//		JSONArray allCategories = cats.toJSONArray(cats.names());
//		JSONObject subCat = null;
//		for(int i=0; i < allCategories.length(); i++) {
//			//subCat = new JSONObject(allCategories.getString(i));
//			subCat = allCategories.getJSONArray(i).getJSONObject(0);
//
//			if(subCat.getString("parents").compareTo(categoryId) == 0)
//				subCategories.put(subCat);
//		}
////		}
////		else
////			return null;
//
//		return subCategories;
//	}
	
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
