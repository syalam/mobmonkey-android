package com.mobmonkey.mobmonkey;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMSegmentedRadioGroup;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * @author Dezapp, LLC
 *
 */
public class MakeARequestScreen extends Activity implements OnCheckedChangeListener, OnItemClickListener, View.OnClickListener {
	private static final String TAG = "MakeARequestScreen: ";
	
	MMSegmentedRadioGroup rgRequests;
	RadioGroup rgStayActive;
	MMExpandedListView mmelvAddMessage;
	MMExpandedListView mmelvScheduleRequest;
	Button btnSendRequest;
	
	String message;
	String scheduleRequest;
	
	int[] icons;
	String[] labels;
	int[] indicatorIcons;
	MMArrayAdapter mmArrayAdapter;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_a_request_screen);
		
		init();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(group == rgRequests) {
			switch(checkedId) {
				case R.id.rbvideorequest:
					btnSendRequest.setText(R.string.btn_send_video_request);
					break;
				case R.id.rbphotorequest:
					btnSendRequest.setText(R.string.btn_send_photo_request);
					break;
				case R.id.rbtextrequest:
					btnSendRequest.setText(R.string.btn_send_text_request);
					break;
			}
		} else if(group == rgStayActive) {
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		if(adapterView == mmelvAddMessage) {
			if(position == 0) {
				Intent addMessageIntent = new Intent(MakeARequestScreen.this, AddMessageScreen.class);
				addMessageIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_MESSAGE, message);
				startActivityForResult(addMessageIntent, MMAPIConstants.REQUEST_CODE_ADD_MESSAGE);
			}
		} else if (adapterView == mmelvScheduleRequest) {
			if(position == 0) {
				Intent addMessageIntent = new Intent(MakeARequestScreen.this, ScheduleRequestScreen.class);
				addMessageIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_MESSAGE, scheduleRequest);
				startActivityForResult(addMessageIntent, MMAPIConstants.REQUEST_CODE_SCHEDULE_REQUEST);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.ivindicatoricon) {
			message = MMAPIConstants.DEFAULT_STRING;
			setSingleItemAddMessage();
			mmelvAddMessage.setAdapter(mmArrayAdapter);
			mmelvAddMessage.invalidate();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMAPIConstants.REQUEST_CODE_ADD_MESSAGE) {
			processAddMessageResult(resultCode, data);
		} else if(requestCode == MMAPIConstants.REQUEST_CODE_SCHEDULE_REQUEST) {
			processScheduleRequestResult(resultCode, data);
		}
	}
	
	/**
	 * 
	 */
	private void init() {
		rgRequests = (MMSegmentedRadioGroup) findViewById(R.id.rgrequests);
		rgStayActive = (RadioGroup) findViewById(R.id.rgstayactivefor);
		mmelvAddMessage = (MMExpandedListView) findViewById(R.id.mmelvaddmessage);
		mmelvScheduleRequest = (MMExpandedListView) findViewById(R.id.mmelvschedulerequest);
		btnSendRequest = (Button) findViewById(R.id.btnsentrequest);
		
		message = MMAPIConstants.DEFAULT_STRING;
		scheduleRequest = MMAPIConstants.DEFAULT_STRING;
		
		rgRequests.setOnCheckedChangeListener(MakeARequestScreen.this);
		rgStayActive.setOnCheckedChangeListener(MakeARequestScreen.this);
		
		setSingleItemAddMessage();
		mmelvAddMessage.setAdapter(mmArrayAdapter);
		mmelvAddMessage.setOnItemClickListener(MakeARequestScreen.this);
		
		icons = new int[] {android.R.drawable.ic_menu_today};
		labels = new String[] {getString(R.string.tv_schedule_request)};
		indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		
		mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
		mmelvScheduleRequest.setAdapter(mmArrayAdapter);
		mmelvScheduleRequest.setOnItemClickListener(MakeARequestScreen.this);
	}
	
	/**
	 * 
	 */
	private void setSingleItemAddMessage() {
		icons = new int[] {android.R.drawable.ic_menu_edit};
		labels = new String[] {getString(R.string.tv_add_message)};
		indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
	}
	
	/**
	 * 
	 * @param resultCode
	 * @param data
	 */
	private void processAddMessageResult(int resultCode, Intent data) {
		message = data.getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_MESSAGE);
		
		if(resultCode == RESULT_CANCELED) {
			setSingleItemAddMessage();
		} else if(resultCode == RESULT_OK) {
			icons = new int[] {android.R.drawable.ic_menu_edit, android.R.drawable.ic_menu_edit};
			labels = new String[] {getString(R.string.tv_add_message), message};
			indicatorIcons = new int[] {R.drawable.listview_accessory_indicator, android.R.drawable.ic_menu_close_clear_cancel};
			mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, MakeARequestScreen.this);
		}
		mmelvAddMessage.setAdapter(mmArrayAdapter);
		mmelvAddMessage.invalidate();
	}
	
	/**
	 * 
	 * @param resultCode
	 * @param data
	 */
	private void processScheduleRequestResult(int resultCode, Intent data) {
		if(resultCode == RESULT_CANCELED) {
			
		} else if(resultCode == RESULT_OK) {
			SimpleDateFormat sdfTime = new SimpleDateFormat("KK:mm a");
			SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
			
			Calendar requestCal = (Calendar) data.getSerializableExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME);
			
			String scheduleMessage = sdfTime.format(requestCal.getTime()) + " on " + sdfDate.format(requestCal.getTime());
			
			icons = new int[] {android.R.drawable.ic_menu_today, android.R.drawable.ic_menu_today};
			labels = new String[] {getString(R.string.tv_schedule_request), scheduleMessage};
			indicatorIcons = new int[] {R.drawable.listview_accessory_indicator, android.R.drawable.ic_menu_close_clear_cancel};
			mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, MakeARequestScreen.this);
		}
		
		mmelvScheduleRequest.setAdapter(mmArrayAdapter);
		mmelvScheduleRequest.invalidate();
	}
}
