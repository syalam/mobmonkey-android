package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;


public class AddLocationCategoryList extends Activity{
	
	int[] categoryIcons;
	int[] categoryIndicatorIcons;
	String[] topLevelCategories;
	ArrayAdapter<Object> arrayAdapter;
	
	protected static final String TAG = "AddLocationCategoryList ";
	ListView categoriesList;
	TextView navigationbarText;
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	ArrayList<String> categoriesArrayList = new ArrayList<String>();

	JSONArray categories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_hold);
		setContentView(R.layout.add_location_select_category_screen);
		categoriesList = (ListView) findViewById(R.id.categoryList);
		navigationbarText = (TextView) findViewById(R.id.navtitle);
		navigationbarText.setText(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		init();
	}

    /* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}
	
	private void init()
	{
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		try
		{
			topLevelCategories = MMCategories.getTopLevelCategories(AddLocationCategoryList.this.getApplicationContext());
			categories = new JSONArray(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_CATEGORY));
			String[] cats = new String[categories.length()];
			for(int i=0; i<categories.length(); i++)
			{
				JSONObject category = categories.getJSONObject(i);
				categoriesArrayList.add(category.getString("en"));
				cats[i] = categories.getJSONObject(i).getString("en");
			}
			
//			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, categoriesArrayList) {
//				@Override
//				public View getView(int position, View convertView, ViewGroup parent) 
//				{
//					View view = super.getView(position, convertView, parent);
//					TextView eventText = (TextView) view.findViewById(android.R.id.text1);
//					eventText.setTypeface(null, Typeface.BOLD);
//					return view;
//				}
//	        };
			
			if(cats[0].equals("Automotive"))
				getSearchCategoryIcons();
			else
				getSubCategoryIcons(categories);
			
			arrayAdapter = new MMArrayAdapter(AddLocationCategoryList.this, R.layout.mm_listview_row, categoryIcons, 
					cats, categoryIndicatorIcons, android.R.style.TextAppearance_Medium, 
					Typeface.DEFAULT_BOLD, null);
			categoriesList.setAdapter(arrayAdapter);
	        
	        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int row, long arg3) {
					
					try
					{
						String response = MMCategories.getSubCategoriesWithCategoriId(AddLocationCategoryList.this.getApplicationContext(), categories.getJSONObject(row).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID));
						JSONArray subCategoryData = new JSONArray(response);
						//String[] subCategories = subCategoryData.toString().substring(1,subCategoryData.toString().length()-1).replaceAll("\"", "").split(",");
						
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
								boolean remove = false;
								JSONArray selectedCategoriesList = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_CATEGORY_LIST, MMAPIConstants.DEFAULT_STRING_EMPTY));
								
								ArrayList<JSONObject> temp = new ArrayList<JSONObject>();
								for(int i=0; i<selectedCategoriesList.length(); i++)
								{
									temp.add((JSONObject)selectedCategoriesList.get(i));
								}
								for(int j=0; j<temp.size(); j++)
									if(temp.get(j).equals(categories.getJSONObject(row)))
									{
										temp.remove(j);
										categoryIndicatorIcons[row] = android.R.drawable.checkbox_off_background;
										remove = true;
										arrayAdapter.notifyDataSetChanged();
									}
								if(!remove)
								{
									temp.add(categories.getJSONObject(row));
									categoryIndicatorIcons[row] = android.R.drawable.checkbox_on_background;
									arrayAdapter.notifyDataSetChanged();
								}
								
								selectedCategoriesList = new JSONArray(temp);
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
	private void getSearchCategoryIcons() {
		categoryIcons = new int[] {
			R.drawable.cat_icon_automotive, 
			R.drawable.cat_icon_travel, 
			R.drawable.cat_icon_sports, 
			R.drawable.cat_icon_healthcare, 
			R.drawable.cat_icon_landmarks, 
			R.drawable.cat_icon_social, 
			R.drawable.cat_icon_community_government, 
			R.drawable.cat_icon_retail, 
			R.drawable.cat_icon_services_supplies, 
			R.drawable.cat_icon_transportation
		};
		categoryIndicatorIcons = new int[] {
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator,
			R.drawable.listview_accessory_indicator
		};
	}
	
	private void getSubCategoryIcons(JSONArray categories) throws JSONException {
		categoryIcons = new int[] {
		};
		categoryIndicatorIcons = new int[categories.length()];
		for(int i=0; i<categories.length(); i++)
		{
			JSONArray newArray = new JSONArray(MMCategories.getSubCategoriesWithCategoriId(this.getApplicationContext(), categories.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_CATEGORY_ID)));
			if(!newArray.isNull(0))
			{
				categoryIndicatorIcons[i]=R.drawable.listview_accessory_indicator;
			}
			else
			{
				categoryIndicatorIcons[i]= android.R.drawable.checkbox_off_background;
			}
			
		}
	}
}
