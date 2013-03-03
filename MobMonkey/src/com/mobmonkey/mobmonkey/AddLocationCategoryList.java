package com.mobmonkey.mobmonkey;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.utils.MMCategories;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;


public class AddLocationCategoryList extends Activity{

	protected static final String TAG = "AddLocationCategoryList ";
	ListView categoriesList;
	TextView navigationbarText;
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	ArrayList<String> categoriesArrayList = new ArrayList<String>();

	JSONArray categories;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_location_select_category_screen);
		categoriesList = (ListView) findViewById(R.id.categoryList);
		navigationbarText = (TextView) findViewById(R.id.navtitle);
		navigationbarText.setText(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		init();
	}
	
	private void init()
	{
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		try
		{
			categories = new JSONArray(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY));
			for(int i=0; i<categories.length(); i++)
			{
				JSONObject category = categories.getJSONObject(i);
				categoriesArrayList.add(category.getString("en"));	
			}
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, categoriesArrayList) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) 
				{
					View view = super.getView(position, convertView, parent);
					TextView eventText = (TextView) view.findViewById(android.R.id.text1);
					eventText.setTypeface(null, Typeface.BOLD);
					return view;
				}
	        };
	        categoriesList.setAdapter(arrayAdapter);
	        
	        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int row, long arg3) {
					
					try
					{
						String response = MMCategories.getSubCategoriesWithCategoriId(AddLocationCategoryList.this.getApplicationContext(), categories.getJSONObject(row).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID));
						JSONArray subCategoryData = new JSONArray(response);
						
						if(subCategoryData.isNull(0))
						{ 
							/* ADD A CHECK MARK
							 * if(cell has a checkmark)
							 * 		- remove the checkmark
							 * 		- remove json object from the SHARED_PREFS_KEY_CATEGORY_LIST
							 * else if(cell does not have a checkmark)
							 * 		- do the following below ...
							 */
							
							if(userPrefs.contains(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST))
							{
								JSONArray selectedCategoriesList = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST, MMAPIConstants.DEFAULT_STRING));

								Log.d(TAG, TAG + "Category List BEFORE: " + selectedCategoriesList);
								selectedCategoriesList.put(categories.getJSONObject(row));
								Log.d(TAG, TAG + "Category List BEFORE: " + selectedCategoriesList);
								
								userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST, selectedCategoriesList.toString());
								userPrefsEditor.commit();
							}
							else
							{
								JSONArray newSelectedCategoriesList = new JSONArray();
								newSelectedCategoriesList.put(categories.getJSONObject(row));
								userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST, newSelectedCategoriesList.toString());
								userPrefsEditor.commit();
							}	
						}
						else 
						{
							Intent categoryScreenIntent = new Intent(AddLocationCategoryList.this, AddLocationCategoryList.class);
							categoryScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY, response);
							categoryScreenIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, "Categories");
							startActivity(categoryScreenIntent);
						}
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}
					
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
