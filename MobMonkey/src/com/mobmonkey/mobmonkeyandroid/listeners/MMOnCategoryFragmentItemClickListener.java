package com.mobmonkey.mobmonkeyandroid.listeners;

import org.json.JSONArray;

/**
 * @author Dezapp, LLC
 *
 */
public interface MMOnCategoryFragmentItemClickListener {
	public void onCategoryFragmentItemClick(String selectedCategory, JSONArray subCategories, boolean isTopLevel);
}
