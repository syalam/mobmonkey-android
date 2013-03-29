package com.mobmonkey.mobmonkey.utils;

import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MMCategories extends Activity{
	
	private static SharedPreferences userPrefs;
	private static SharedPreferences.Editor editPrefs;
	Context context;
	
	public static String[] getTopLevelCategories(Context context) throws JSONException
	{
		userPrefs = context.getSharedPreferences(MMAPIConstants.USER_PREFS, 0);
		
		String[] topLevelCategoriesList;
		
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES))
		{
			JSONObject cats = new JSONObject(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
			topLevelCategoriesList = getKeys(cats);
//			JSONArray allCategories = cats.toJSONArray(cats.names());
//			JSONObject category = null;
//			for(int i=0; i < allCategories.length(); i++)
//			{
//				category = allCategories.getJSONArray(i).getJSONObject(0);
//				Log.d("Cat Parents:", category.getString("parents"));
//				if(category.getString("parents").compareTo("1") == 0)
//					topLevelCategoriesList.put(category);
//			}
		}
		else
			return null;
		
		return topLevelCategoriesList;
	}
	
	private static String[] getKeys(JSONObject cats) {
		int length = cats.length();
	    if (length == 0) {
	        return null;
	    }
	    Iterator iterator = cats.keys();
	    String[] names = new String[length];
	    int i = 0;
	    while (iterator.hasNext()) {
	        names[i] = (String)iterator.next();
	        i += 1;
	    }
	    return names;
	}

	public static String getSubCategoriesWithCategoriId(Context context, String categoryId) throws JSONException
	{
		userPrefs = context.getSharedPreferences(MMAPIConstants.USER_PREFS, 0);
		JSONArray subCategoriesList = null;
		
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES))
		{
			subCategoriesList = new JSONArray();
			JSONObject cats = new JSONObject(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
			JSONArray allCategories = cats.toJSONArray(cats.names());
			JSONObject subCat = null;
			for(int i=0; i < allCategories.length(); i++)
			{
				//subCat = new JSONObject(allCategories.getString(i));
				subCat = allCategories.getJSONArray(i).getJSONObject(0);
				
				if(subCat.getString("parents").compareTo(categoryId) == 0)
					subCategoriesList.put(subCat);
			}
		}
		else
			return null;
		
		return subCategoriesList.toString();
	}
	
	public static String getSubCategoriesWithCategoryName(Context context, String categoryName) throws JSONException
	{
		userPrefs = context.getSharedPreferences(MMAPIConstants.USER_PREFS, 0);
		JSONArray subCategoriesList = null;
		
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES))
		{
			subCategoriesList = new JSONArray();
			JSONObject cats = new JSONObject(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
			JSONArray topCategory = cats.getJSONArray(categoryName);
			for(int i=0; i < topCategory.length(); i++)
			{
				if(!topCategory.getJSONObject(i).get(Locale.getDefault().getLanguage()).toString().equals(categoryName))
					subCategoriesList.put(topCategory.getJSONObject(i));
			}
		}
		else
			return null;
		
		return subCategoriesList.toString();
	}
	
	public static void addCategory(String categoryId)
	{
		
	}
}
