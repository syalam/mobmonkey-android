package com.mobmonkey.mobmonkey.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MMCategories extends Activity{
	
	private static SharedPreferences userPrefs;
	private static SharedPreferences.Editor editPrefs;
	Context context;
	
	public static JSONArray getTopLevelCategories(Context context) throws JSONException
	{
		userPrefs = context.getSharedPreferences(MMAPIConstants.USER_PREFS, 0);
		JSONArray topLevelCategoriesList = new JSONArray();
		
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES))
		{
			JSONArray allCategories = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
			JSONObject category = null;
			for(int i=0; i < allCategories.length(); i++)
			{
				category = new JSONObject(allCategories.getString(i));
				
				if(category.getString("parents").compareTo("1") == 0)
					topLevelCategoriesList.put(category);
			}
		}
		else
			return null;
		
		return topLevelCategoriesList;
	}
	
	public static String getSubCategoriesWithCategoriId(Context context, String categoryId) throws JSONException
	{
		userPrefs = context.getSharedPreferences(MMAPIConstants.USER_PREFS, 0);
		JSONArray subCategoriesList = null;
		
		if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES))
		{
			subCategoriesList = new JSONArray();
			JSONArray allCategories = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_ALL_CATEGORIES, MMAPIConstants.DEFAULT_STRING));
			JSONObject subCat = null;
			for(int i=0; i < allCategories.length(); i++)
			{
				subCat = new JSONObject(allCategories.getString(i));
				int num;
				if(subCat.getString("en").compareTo("null")==0)
					num = 2;
				if(subCat.getString("parents").compareTo(categoryId)==0)
					subCategoriesList.put(subCat);
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
