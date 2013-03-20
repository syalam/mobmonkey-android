package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkey.utils.MMMyInfoArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMMyinfoItem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MyInfoScreen extends Activity{

	private ListView lvMyInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_info_screen);
		
		init();
	}
	
	private void init() {
		lvMyInfo = (ListView) findViewById(R.id.lvmyinfo);
		
		MMMyinfoItem[] data = new MMMyinfoItem[getResources().getStringArray(R.array.my_info_item).length];
		
		for(int i = 0; i < data.length; i++) {
			MMMyinfoItem item = new MMMyinfoItem();
			item.hint = getResources().getStringArray(R.array.my_info_item)[i];
			data[i] = item; 
		}
		
		MMMyInfoArrayAdapter arrayAdapter
			= new MMMyInfoArrayAdapter(this, R.layout.my_info_listview_row, data);
		
		lvMyInfo.setAdapter(arrayAdapter);
	}
	
}
