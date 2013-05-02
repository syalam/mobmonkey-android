package com.mobmonkey.mobmonkeyandroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;


public class AddLocationCategoryList extends Activity{
	
	private int[] categoryIcons;
	private int[] categoryIndicatorIcons;
	private String[] topLevelCategories;
	private ArrayAdapter<Object> arrayAdapter;
	
	protected static final String TAG = "AddLocationCategoryList ";
	private ListView categoriesList;
	private TextView navigationbarText;
	
	private String[] categories;
	
	private ArrayList<String> selectedCategories;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_hold);
		setContentView(R.layout.add_location_select_category_screen);
		context = this;
		selectedCategories = getIntent().getStringArrayListExtra(MMSDKConstants.KEY_INTENT_EXTRA_ADD_CATEGORY);
		
		categoriesList = (ListView) findViewById(R.id.categoryList);
		navigationbarText = (TextView) findViewById(R.id.navtitle);
		navigationbarText.setText(getIntent().getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE));
		init();
	}

    /* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		try {
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable(MMSDKConstants.KEY_INTENT_EXTRA_ADD_CATEGORY, selectedCategories);
			data.putExtras(bundle);
			
			if (getParent() == null) {
			    setResult(Activity.RESULT_OK, data);
			    finish();
			}
			else {
			    getParent().setResult(Activity.RESULT_OK, data);
			    finish();
			}
			//setResult(RESULT_OK, data);
		} catch (Exception ex) {
			Log.d(TAG, "onBackPressed: " + ex.toString());
		}
		
		
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}
	
	private void init()
	{	
		try {
			topLevelCategories = MMCategories.getTopLevelCategories(AddLocationCategoryList.this.getApplicationContext());
			categories = ((String[])getIntent().getExtras().get(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY));
			
			refreshList();
	        
	        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					try {
						// check if there are any subcategories
						//String subCateString = MMCategories.getSubCategoriesWithCategoryName(getApplicationContext(), categories[position]);
						JSONArray subCateArray = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(context, categories[position]));
						
						// if there are subcategories, start a new categorylist
						if(subCateArray.length() > 0) {
							
							String[] subCateStringArray = new String[subCateArray.length()];
							for(int i = 0; i < subCateStringArray.length; i++) {
								subCateStringArray[i] = subCateArray.getJSONObject(i).getString("en");
							}
							
							Intent categoryList = new Intent(AddLocationCategoryList.this, AddLocationCategoryList.class);
							categoryList.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY, subCateStringArray);
							categoryList.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, "Categories");
							Log.d(TAG, selectedCategories.size()+"");
							categoryList.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_ADD_CATEGORY, selectedCategories);
							startActivityForResult(categoryList, MMSDKConstants.REQUEST_CODE_ADD_CATEGORY);
						} else {
							if(!selectedCategories.contains(categories[position])) {
								selectedCategories.add(categories[position]);
							} else {
								selectedCategories.remove(categories[position]);
							}
							
							// refresh list
							refreshList();
						}
					} catch (JSONException e) {
						// if there are no more subcategories, add category name to the selectedCategories
						e.printStackTrace();
						if(!selectedCategories.contains(categories[position])) {
							selectedCategories.add(categories[position]);
						} else {
							selectedCategories.remove(categories[position]);
						}
						
						// refresh list
						refreshList();
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
				R.drawable.cat_icon_coffee_shops, 
				R.drawable.cat_icon_schools, 
				R.drawable.cat_icon_beaches, 
				R.drawable.cat_icon_supermarkets, 
				R.drawable.cat_icon_conferences, 
				R.drawable.cat_icon_restaurants, 
				R.drawable.cat_icon_hotels, 
				R.drawable.cat_icon_night_clubs,
				R.drawable.cat_icon_dog_parks,
				R.drawable.cat_icon_pubs, 
				R.drawable.cat_icon_stadiums,
				R.drawable.cat_icon_health_clubs,
				R.drawable.cat_icon_cinemas
			};
		
		categoryIndicatorIcons = new int[categories.length];
		for(int i = 0; i < categories.length; i++) {
			try {
				JSONArray newArray = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(this, 
						 						   												 categories[i]));
				Log.d(TAG, "newArrayLength: " + newArray.length());
				if(newArray.length() > 0) {
					categoryIndicatorIcons[i] = R.drawable.listview_accessory_indicator;
				} else {
					if(selectedCategories.contains(categories[i])) {
						categoryIndicatorIcons[i]= android.R.drawable.checkbox_on_background;
					} else {
						categoryIndicatorIcons[i]= android.R.drawable.checkbox_off_background;
					}
				}
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void getSubCategoryIcons(String[] categories) {
		categoryIcons = new int[] {};
		categoryIndicatorIcons = new int[categories.length];
		for(int i = 0; i < categories.length; i++) {
			try {
				JSONArray newArray = new JSONArray(MMCategories.getSubCategoriesWithCategoryName(this, 
						 categories[i]));
				if(!newArray.isNull(0)) {
					categoryIndicatorIcons[i] = R.drawable.listview_accessory_indicator;
				}
			} catch (JSONException ex) {
				try {
					// check if category is already selected
					if(selectedCategories.contains(categories[i])) {
						categoryIndicatorIcons[i]= android.R.drawable.checkbox_on_background;
					} else {
						categoryIndicatorIcons[i]= android.R.drawable.checkbox_off_background;
					}
				} catch (NullPointerException e) {
					categoryIndicatorIcons[i]= android.R.drawable.checkbox_off_background;
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_CATEGORY) {
			if(resultCode == RESULT_OK) {
				selectedCategories = (ArrayList<String>)data.getSerializableExtra(MMSDKConstants.KEY_INTENT_EXTRA_ADD_CATEGORY);
			} else if (resultCode == RESULT_CANCELED) {
				Log.d(TAG, "Cancel");
				
			}
		}
	}
	
	private void refreshList() {
		if(categories[0].equals(topLevelCategories[0]))
			getSearchCategoryIcons();
		else
			getSubCategoryIcons(categories);
		
		arrayAdapter = new MMArrayAdapter(AddLocationCategoryList.this, 
										  R.layout.listview_row, 
										  categoryIcons, 
										  categories, 
										  categoryIndicatorIcons, 
										  android.R.style.TextAppearance_Medium, 
										  Typeface.DEFAULT_BOLD, 
										  null);
		
		categoriesList.setAdapter(arrayAdapter);
	}
}
