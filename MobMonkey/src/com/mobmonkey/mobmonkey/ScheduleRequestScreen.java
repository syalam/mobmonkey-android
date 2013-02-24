package com.mobmonkey.mobmonkey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class ScheduleRequestScreen extends Activity {
	private static final String TAG = "ScheduleRequestScreen: ";
	
	WheelView wvDay;
	WheelView wvHours;
	WheelView wvMins;
	WheelView wvAMPM;
	
	NumericWheelAdapter numericWheelAdapter;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_request_screen);
		
		wvDay = (WheelView) findViewById(R.id.wheelday);
		wvHours = (WheelView) findViewById(R.id.wheelhour);
		wvMins = (WheelView) findViewById(R.id.wheelmins);
		wvAMPM = (WheelView) findViewById(R.id.wheelampm);
		
		Calendar calendar = Calendar.getInstance();
		
		wvDay.setViewAdapter(new DayArrayAdapter(ScheduleRequestScreen.this, calendar));
		wvDay.setCurrentItem(MMAPIConstants.DAYS_PREVIOUS);
		
		numericWheelAdapter = new NumericWheelAdapter(ScheduleRequestScreen.this, 1, 12);
		numericWheelAdapter.setItemResource(R.layout.wheel_text_item);
		numericWheelAdapter.setItemTextResource(R.id.text);
		wvHours.setViewAdapter(numericWheelAdapter);
		wvHours.setCurrentItem(calendar.get(Calendar.HOUR));
		wvHours.setCyclic(true);
		
		numericWheelAdapter = new NumericWheelAdapter(ScheduleRequestScreen.this, 0, 59, "%02d");
		numericWheelAdapter.setItemResource(R.layout.wheel_text_item);
		numericWheelAdapter.setItemTextResource(R.id.text);
		wvMins.setViewAdapter(numericWheelAdapter);
		wvMins.setCurrentItem(calendar.get(Calendar.MINUTE));
		wvMins.setCyclic(true);
		
		ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(this, new String[] {"AM", "PM"});
        ampmAdapter.setItemResource(R.layout.wheel_text_item);
        ampmAdapter.setItemTextResource(R.id.text);
        wvAMPM.setViewAdapter(ampmAdapter);
        wvAMPM.setCurrentItem(calendar.get(Calendar.AM_PM));
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, TAG + "day: " + wvDay.getCurrentItem());
		Log.d(TAG, TAG + "hour: " + wvHours.getCurrentItem());
		Log.d(TAG, TAG + "mins: " + wvMins.getCurrentItem());
		Log.d(TAG, TAG + "am/pm: " + wvAMPM.getCurrentItem());
		
		super.onBackPressed();
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsetschedule:
				break;
		}
	}
	
	private class DayArrayAdapter extends AbstractWheelTextAdapter {
		private final int daysCount = 1000;
		
		Calendar calendar;
		
		protected DayArrayAdapter(Context context, Calendar calendar) {
			super(context, R.layout.time2_day, NO_RESOURCE);
			this.calendar = calendar;
			
			setItemTextResource(R.id.time2_monthday);
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			int day = -MMAPIConstants.DAYS_PREVIOUS + index;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.DAY_OF_YEAR, day);
			
			View view = super.getItem(index, convertView, parent);
            TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            
            if (day == 0) {
                weekday.setText(MMAPIConstants.DEFAULT_STRING);
                monthday.setText(R.string.tv_today);
                monthday.setTextColor(0xFF0000F0);
            } else {
                DateFormat dateFormat = new SimpleDateFormat("EEE");
                weekday.setText(dateFormat.format(newCalendar.getTime()));
                dateFormat = new SimpleDateFormat("MMM d");
                monthday.setText(dateFormat.format(newCalendar.getTime()));
                monthday.setTextColor(0xFF111111);
            }
            
			return view;
		}

		@Override
		public int getItemsCount() {
			return daysCount + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return MMAPIConstants.DEFAULT_STRING;
		}
	}
}
