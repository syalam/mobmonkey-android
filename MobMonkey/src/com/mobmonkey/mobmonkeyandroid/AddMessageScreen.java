package com.mobmonkey.mobmonkeyandroid;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Dezapp, LLC
 *
 */
public class AddMessageScreen extends Activity implements OnClickListener,
														  OnItemClickListener {
	private static final String TAG = "AddMessageScreen: ";
	
	private Button btnDone;
	private EditText etMessage;
	private Button btnClearMessage;
	private MMExpandedListView mmelvDefaultMessage;
	
	private String[] defaultMessages;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_hold);
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
		messageIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE, MMSDKConstants.DEFAULT_STRING_EMPTY);
		setResult(RESULT_CANCELED, messageIntent);
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btndone:
				Intent messageIntent = new Intent();
				messageIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE, etMessage.getText().toString().trim());
				if(TextUtils.isEmpty(etMessage.getText()) || etMessage.getText().toString().trim().length() <= 0) {
					setResult(RESULT_CANCELED, messageIntent);
				} else {
					setResult(RESULT_OK, messageIntent);
				}
				finish();
				overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
				break;
			case R.id.btnclearmessage:
				etMessage.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
				break;
		}
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
		btnDone = (Button) findViewById(R.id.btndone);
		etMessage = (EditText) findViewById(R.id.etmessage);
		btnClearMessage = (Button) findViewById(R.id.btnclearmessage);
		mmelvDefaultMessage = (MMExpandedListView) findViewById(R.id.mmelvdefaultmessage);
		
		defaultMessages = getResources().getStringArray(R.array.tv_default_message);
		
		etMessage.setText(getIntent().getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE));
		
		btnDone.setOnClickListener(AddMessageScreen.this);
		btnClearMessage.setOnClickListener(AddMessageScreen.this);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddMessageScreen.this, R.layout.listview_row_simple, R.id.tvlabel, defaultMessages);
		mmelvDefaultMessage.setAdapter(arrayAdapter);
		mmelvDefaultMessage.setOnItemClickListener(AddMessageScreen.this);
	}
}
