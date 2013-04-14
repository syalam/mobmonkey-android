package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

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
		elvCategories.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		elvCategories.setItemsCanFocus(false);
	}
	
	
}
