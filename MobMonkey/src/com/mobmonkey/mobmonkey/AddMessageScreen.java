package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkey.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class AddMessageScreen extends Activity implements OnItemClickListener {
	private static final String TAG = "AddMessageScreen: ";
	
	EditText etMessage;
	MMExpandedListView mmelvDefaultMessage;
	
	String[] defaultMessages;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_message_screen);
		init();
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		Intent messageIntent = new Intent();
		messageIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_MESSAGE, etMessage.getText().toString().trim());
		if(TextUtils.isEmpty(etMessage.getText()) || etMessage.getText().toString().trim().length() <= 0) {
			setResult(RESULT_CANCELED, messageIntent);
		} else {
			setResult(RESULT_OK, messageIntent);
		}
		super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		etMessage.setText(defaultMessages[position]);
	}
	
	/**
	 * 
	 */
	private void init() {
		etMessage = (EditText) findViewById(R.id.etmessage);
		mmelvDefaultMessage = (MMExpandedListView) findViewById(R.id.mmelvdefaultmessage);
		
		defaultMessages = getResources().getStringArray(R.array.tv_default_message);
		
		etMessage.setText(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_MESSAGE));
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddMessageScreen.this, R.layout.mm_simple_listview_row, R.id.tvlabel, defaultMessages);
		mmelvDefaultMessage.setAdapter(arrayAdapter);
		mmelvDefaultMessage.setOnItemClickListener(AddMessageScreen.this);
	}
}
