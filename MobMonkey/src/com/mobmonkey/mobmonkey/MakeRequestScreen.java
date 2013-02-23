package com.mobmonkey.mobmonkey;

import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMSegmentedRadioGroup;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * @author Dezapp, LLC
 *
 */
public class MakeRequestScreen extends Activity implements OnCheckedChangeListener {
	MMSegmentedRadioGroup rgRequests;
	RadioGroup rgStayActive;
	MMExpandedListView mmelvAddMessage;
	MMExpandedListView mmelvScheduleRequest;
	Button btnSendRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_request_screen);
		
		rgRequests = (MMSegmentedRadioGroup) findViewById(R.id.rgrequests);
		rgStayActive = (RadioGroup) findViewById(R.id.rgstayactivefor);
		mmelvAddMessage = (MMExpandedListView) findViewById(R.id.mmelvaddmessage);
		mmelvScheduleRequest = (MMExpandedListView) findViewById(R.id.mmelvschedulerequest);
		btnSendRequest = (Button) findViewById(R.id.btnsentrequest);
		
		rgRequests.setOnCheckedChangeListener(MakeRequestScreen.this);
		rgStayActive.setOnCheckedChangeListener(MakeRequestScreen.this);
		
		int[] icons = new int[] {android.R.drawable.ic_menu_edit};
		String[] labels = new String[] {getString(R.string.tv_add_message)};
		int[] indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		
		MMArrayAdapter arrayAdapter = new MMArrayAdapter(MakeRequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD);
		mmelvAddMessage.setAdapter(arrayAdapter);
		
		icons = new int[] {android.R.drawable.ic_menu_today};
		labels = new String[] {getString(R.string.tv_schedule_request)};
		indicatorIcons = new int[] {R.drawable.listview_accessory_indicator};
		
		arrayAdapter = new MMArrayAdapter(MakeRequestScreen.this, R.layout.mm_listview_row, icons, labels, indicatorIcons, android.R.style.TextAppearance_Medium, Typeface.DEFAULT_BOLD);
		mmelvScheduleRequest.setAdapter(arrayAdapter);
	}

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
}
