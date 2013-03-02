package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkey.utils.MMExpandedListView;

import android.app.ListActivity;
import android.os.Bundle;

public class CategoryListScreen extends ListActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list_screen);
		init();
	}

	private void init() {
		MMExpandedListView elvCategories = (MMExpandedListView) findViewById(R.id.elvcategorylist);
		
	}
	
	
}
