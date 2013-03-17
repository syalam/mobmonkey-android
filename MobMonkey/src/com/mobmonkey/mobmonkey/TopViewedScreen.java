package com.mobmonkey.mobmonkey;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.utils.MMTopviewedArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMTopviewedItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

public class TopViewedScreen extends Activity{
	public static final String TAG = "TopViewedScreen"; 
	
	private ListView lvtopviewed;
	JSONArray jObj;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top_viewed_screen);
		init();
	}

	private void init() {
		lvtopviewed = (ListView) findViewById(R.id.lvtopviewed);
		
		try {
			jObj = new JSONArray(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED));
			//Log.d(TAG, jObj.length()+"");
			
			MMTopviewedItem data[] = new MMTopviewedItem[jObj.length()];
			try {
				for(int i = 0; i < data.length; i++) {
					//Log.d(TAG, jObj.getJSONObject(i).getString("media"));
					if(jObj.getJSONObject(i).getString("media").compareTo("null") != 0) {
						JSONObject media = new JSONObject(jObj.getJSONObject(i).getString("media"));
						data[i] = new MMTopviewedItem(jObj.getJSONObject(i).getString("name"), 
													  media.getString("mediaURL"));
					} else {
						data[i] = new MMTopviewedItem(jObj.getJSONObject(i).getString("name"), 
								  					  null);
					}
					
				}
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
			MMTopviewedArrayAdapter adapter = new MMTopviewedArrayAdapter(this, R.layout.top_viewed_listview_row, data);
			lvtopviewed.setAdapter(adapter);
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}
}
