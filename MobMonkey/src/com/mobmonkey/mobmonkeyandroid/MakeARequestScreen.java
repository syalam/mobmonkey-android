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
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMSegmentedRadioGroup;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMMakeARequestAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
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
//	private int radiusInYards = 50; //TODO: Remove hard-coded value for radius
	
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
					mediaType = MMSDKConstants.MEDIA_VIDEO;
					break;
				case R.id.rbphotorequest:
					btnSendRequest.setText(R.string.btn_send_photo_request);
					mediaType = MMSDKConstants.MEDIA_IMAGE;
					break;
				case R.id.rbtextrequest:
					btnSendRequest.setText(R.string.btn_send_text_request);
					mediaType = MMSDKConstants.MEDIA_TEXT;
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
				addMessageIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE, message);
				startActivityForResult(addMessageIntent, MMSDKConstants.REQUEST_CODE_ADD_MESSAGE);
			}
		} else if (adapterView == mmelvScheduleRequest) {
			if(position == 0) {
				Intent scheduleRequestIntent = new Intent(MakeARequestScreen.this, ScheduleRequestScreen.class);
				scheduleRequestIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE, scheduleRequest);
				if(requestCal != null) {
					scheduleRequestIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME, requestCal);
				}
				scheduleRequestIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING, repeat);
				scheduleRequestIntent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING_RATE, repeatRate);
				startActivityForResult(scheduleRequestIntent, MMSDKConstants.REQUEST_CODE_SCHEDULE_REQUEST);
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
			
			if(mediaType.equals(MMSDKConstants.MEDIA_TEXT) && message.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				Toast.makeText(MakeARequestScreen.this, R.string.toast_no_message_detected, Toast.LENGTH_SHORT).show();
			} else {
				makeARequest();
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
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_MESSAGE) {
			processAddMessageResult(resultCode, data);
		} else if(requestCode == MMSDKConstants.REQUEST_CODE_SCHEDULE_REQUEST) {
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
		
		message = MMSDKConstants.DEFAULT_STRING_EMPTY;
		scheduleRequest = MMSDKConstants.DEFAULT_STRING_EMPTY;
		mediaType = MMSDKConstants.MEDIA_VIDEO;
		
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
			jObj = new JSONObject(getIntent().getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			locationId = jObj.getString(MMSDKConstants.JSON_KEY_LOCATION_ID);
			providerId = jObj.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS, MODE_PRIVATE);
	}

	/**
	 * 
	 */
	private void setSingleItemAddMessage() {
		icons = new int[] {R.drawable.icon_clipboard};
		labels = new String[] {getString(R.string.tv_add_message)};
		indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
		mmelvAddMessage.setAdapter(mmArrayAdapter);
		mmelvAddMessage.invalidate();
	}
	
	/**
	 * 
	 * @param resultCode
	 * @param data
	 */
	private void processAddMessageResult(int resultCode, Intent data) {
		message = data.getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE);
		
		if(resultCode == RESULT_CANCELED) {
			setSingleItemAddMessage();
		} else if(resultCode == RESULT_OK) {
			icons = new int[] {R.drawable.icon_clipboard, R.drawable.icon_clipboard};
			labels = new String[] {getString(R.string.tv_add_message), message};
			indicatorIcons = new int[] {R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator_close};
			mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					message = MMSDKConstants.DEFAULT_STRING_EMPTY;
					setSingleItemAddMessage();
				}
			});
			mmelvAddMessage.setAdapter(mmArrayAdapter);
			mmelvAddMessage.invalidate();
		}
	}
	
	/**
	 * 
	 */
	private void setSingleScheduleRequest() {
		requestCal = null;
		repeatRate = MMSDKConstants.REQUEST_REPEAT_RATE_NONE;
		scheduleRequest = MMSDKConstants.DEFAULT_STRING_EMPTY;
		icons = new int[] {R.drawable.icon_calendar};
		labels = new String[] {getString(R.string.tv_schedule_request)};
		indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, null);
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
			repeat = data.getBooleanExtra(MMSDKConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING, true);
			repeatRate = data.getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING_RATE);
			Log.d(TAG, TAG + "repeatRate: " + repeatRate);
			
			requestCal = (Calendar) data.getSerializableExtra(MMSDKConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME);
			scheduleDate = Long.toString(requestCal.getTimeInMillis());
			
			String scheduleMessage = MMUtility.getDate(requestCal.getTimeInMillis(), "KK:mm a") + 
					" on " + MMUtility.getDate(requestCal.getTimeInMillis(), "MM/dd/yyyy");
			
			icons = new int[] {R.drawable.icon_calendar, R.drawable.icon_calendar};
			labels = new String[] {getString(R.string.tv_schedule_request), scheduleMessage};
			indicatorIcons = new int[] {R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator_close};
			mmArrayAdapter = new MMArrayAdapter(MakeARequestScreen.this, R.layout.listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setSingleScheduleRequest();
				}
			});
			mmelvScheduleRequest.setAdapter(mmArrayAdapter);
			mmelvScheduleRequest.invalidate();
		}
	}
	
	/**
	 * 
	 */
	private void makeARequest() {
		MMMakeARequestAdapter.makeARequest(new SendRequestCallback(),
										   message,
										   scheduleDate,
										   providerId,
										   locationId,
										   duration,
										   repeatRate,
										   mediaType,
										   MMConstants.PARTNER_ID,
										   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										   userPrefs.getString(MMSDKConstants.KEY_AUTH,MMSDKConstants.DEFAULT_STRING_EMPTY));
		MMProgressDialog.displayDialog(MakeARequestScreen.this,
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_sending_request));
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
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MakeARequestScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject response = new JSONObject((String)obj);
						
						if(response.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							Toast.makeText(MakeARequestScreen.this, R.string.toast_request_successful, Toast.LENGTH_SHORT).show();
							finish();
							overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
						} else {
							Toast.makeText(MakeARequestScreen.this, response.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
}
