package com.mobmonkey.mobmonkeyandroid;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMSegmentedRadioGroup;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMSendRequestAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

/**
 * @author Dezapp, LLC
 *
 */
public class MakeARequestScreen extends Activity implements OnCheckedChangeListener, OnItemClickListener, View.OnClickListener {
	private static final String TAG = "MakeARequestScreen: ";
	
	private MMSegmentedRadioGroup rgRequests;
	private RadioGroup rgStayActive;
	private MMExpandedListView mmelvAddMessage;
	private MMExpandedListView mmelvScheduleRequest;
	private Button btnSendRequest;
	
	private Calendar requestCal;
	private String message;
	private String scheduleRequest;
	private String mediaType;
	
	private int[] icons;
	private String[] labels;
	private int[] indicatorIcons;
	private MMArrayAdapter mmArrayAdapter;
	
	private String scheduleDate;
	private int duration;
	private JSONObject jObj;
	private boolean repeat = true;
	private String repeatRate;
	private int radiusInYards = 50; //TODO: Remove hard-coded value for radius
	
	private SharedPreferences userPrefs;
	private String locationId;
	private String providerId;
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
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
					mediaType = MMAPIConstants.MEDIA_TYPE_VIDEO;
					break;
				case R.id.rbphotorequest:
					btnSendRequest.setText(R.string.btn_send_photo_request);
					mediaType = MMAPIConstants.MEDIA_TYPE_IMAGE;
					break;
				case R.id.rbtextrequest:
					btnSendRequest.setText(R.string.btn_send_text_request);
					mediaType = MMAPIConstants.MEDIA_TYPE_TEXT;
					break;
			}
		} else if(group == rgStayActive) {
			switch(checkedId) {
				case R.id.rbfifteenmin:
					duration = 15;
					break;
				case R.id.rbthirtymin:
					duration = 30;
					break;
				case R.id.rbonehour:
					duration = 60;
					break;
				case R.id.rbthreehour:
					duration = 180;
					break;
			}
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
				Intent scheduleRequestIntent = new Intent(MakeARequestScreen.this, ScheduleRequestScreen.class);
				scheduleRequestIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_MESSAGE, scheduleRequest);
				if(requestCal != null) {
					scheduleRequestIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME, requestCal);
				}
				scheduleRequestIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING, repeat);
				scheduleRequestIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING_RATE, repeatRate);
				startActivityForResult(scheduleRequestIntent, MMAPIConstants.REQUEST_CODE_SCHEDULE_REQUEST);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		// clicked on sendRequestButton
		if(view.getId() == R.id.btnsentrequest) {
			Log.d(TAG, "sent request");
			
			if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_TEXT) && message.equals(MMAPIConstants.DEFAULT_STRING_EMPTY)) {
				Toast.makeText(MakeARequestScreen.this, R.string.toast_no_message_detected, Toast.LENGTH_SHORT).show();
			} else {
				sendRequest();
			}
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
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
		
		message = MMAPIConstants.DEFAULT_STRING_EMPTY;
		scheduleRequest = MMAPIConstants.DEFAULT_STRING_EMPTY;
		mediaType = MMAPIConstants.MEDIA_TYPE_VIDEO;
		
		rgRequests.setOnCheckedChangeListener(MakeARequestScreen.this);
		rgStayActive.setOnCheckedChangeListener(MakeARequestScreen.this);
		
		setSingleItemAddMessage();
		mmelvAddMessage.setOnItemClickListener(MakeARequestScreen.this);
		
		setSingleScheduleRequest();
		mmelvScheduleRequest.setOnItemClickListener(MakeARequestScreen.this);
		
		switch(rgStayActive.getCheckedRadioButtonId()) {
			case R.id.rbfifteenmin:
				duration = 15;
				break;
			case R.id.rbthirtymin:
				duration = 30;
				break;
			case R.id.rbonehour:
				duration = 60;
				break;
			case R.id.rbthreehour:
				duration = 180;
				break;
		}
		try {
			jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			locationId = jObj.getString(MMAPIConstants.JSON_KEY_LOCATION_ID);
			providerId = jObj.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
	}

	/**
	 * 
	 */
	private void setSingleItemAddMessage() {
		icons = new int[] {android.R.drawable.ic_menu_edit};
		labels = new String[] {getString(R.string.tv_add_message)};
		indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
		mmelvAddMessage.setAdapter(mmArrayAdapter);
		mmelvAddMessage.invalidate();
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
			mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					message = MMAPIConstants.DEFAULT_STRING_EMPTY;
					setSingleItemAddMessage();
				}
			});
			mmelvAddMessage.setAdapter(mmArrayAdapter);
			mmelvAddMessage.invalidate();
		}
	}
	
	private void setSingleScheduleRequest() {
		requestCal = null;
		repeatRate = MMAPIConstants.REQUEST_REPEAT_RATE_NONE;
		scheduleRequest = MMAPIConstants.DEFAULT_STRING_EMPTY;
		icons = new int[] {android.R.drawable.ic_menu_today};
		labels = new String[] {getString(R.string.tv_schedule_request)};
		indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
		mmelvScheduleRequest.setAdapter(mmArrayAdapter);
		mmelvScheduleRequest.invalidate();
	}
	
	/**
	 * 
	 * @param resultCode
	 * @param data
	 */
	private void processScheduleRequestResult(int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			repeat = data.getBooleanExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING, true);
			repeatRate = data.getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING_RATE);
			Log.d(TAG, TAG + "repeatRate: " + repeatRate);
			
			requestCal = (Calendar) data.getSerializableExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME);
			scheduleDate = Long.toString(requestCal.getTimeInMillis());
			
			String scheduleMessage = MMUtility.getDate(requestCal.getTimeInMillis(), "KK:mm a") + 
					" on " + MMUtility.getDate(requestCal.getTimeInMillis(), "MM/dd/yyyy");
			
			icons = new int[] {android.R.drawable.ic_menu_today, android.R.drawable.ic_menu_today};
			labels = new String[] {getString(R.string.tv_schedule_request), scheduleMessage};
			indicatorIcons = new int[] {R.drawable.listview_accessory_indicator, android.R.drawable.ic_menu_close_clear_cancel};
			mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setSingleScheduleRequest();
				}
			});
			mmelvScheduleRequest.setAdapter(mmArrayAdapter);
			mmelvScheduleRequest.invalidate();
		}
	}
	
	private void sendRequest() {
		MMProgressDialog.displayDialog(MakeARequestScreen.this, MMAPIConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_sending_request));
		MMSendRequestAdapter.sendRequest(new SendRequestCallback(), 
										 message,
										 scheduleDate, 
										 providerId,
										 locationId,
										 duration,
										 radiusInYards,
										 repeatRate,
										 mediaType, 
										 MMConstants.PARTNER_ID,
										 userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
										 userPrefs.getString(MMAPIConstants.KEY_AUTH,MMAPIConstants.DEFAULT_STRING_EMPTY));
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class SendRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				try {
					JSONObject response = new JSONObject((String)obj);
					if(response.getString(MMAPIConstants.KEY_RESPONSE_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
						Toast.makeText(MakeARequestScreen.this, R.string.toast_request_successful, Toast.LENGTH_SHORT).show();
						finish();
						overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
					} else {
						Toast.makeText(MakeARequestScreen.this, response.getString(MMAPIConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}