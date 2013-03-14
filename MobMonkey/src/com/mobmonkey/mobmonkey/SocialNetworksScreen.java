package com.mobmonkey.mobmonkey;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SocialNetworksScreen extends Activity{

	protected static final String TAG = "SocialNetworksScreen";
	
	private ListView lvSocialNetwork;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.social_networks_screen);
		
		lvSocialNetwork = (ListView) findViewById(R.id.lvsocialnetwork);
		
		init();
	}
	
	private void init() {
		ArrayAdapter<String> arrayAdapter 
			= new ArrayAdapter<String>(SocialNetworksScreen.this, R.layout.social_networks_listview_row, R.id.tvsocialnetworktext, getResources().getStringArray(R.array.social_networks_name));
		lvSocialNetwork.setAdapter(arrayAdapter);
	}
}