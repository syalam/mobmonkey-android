package com.mobmonkey.mobmonkey;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * @author Dezapp, LLC
 *
 */
public class ScheduleRequestScreen extends Activity implements OnWheelChangedListener, OnCheckedChangeListener {
	private static final String TAG = "ScheduleRequestScreen: ";
	
	WheelView wvDay;
	WheelView wvHours;
	WheelView wvMins;
	WheelView wvAMPM;
	ToggleButton tbRepeating;
	RadioGroup rgRepeating;
	
	NumericWheelAdapter numericWheelAdapter;
	
	String repeatRate;
	
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
		tbRepeating = (ToggleButton) findViewById(R.id.tbrepeating);
		rgRepeating = (RadioGroup) findViewById(R.id.rgrepeating);
		
		repeatRate = ((RadioButton) findViewById(R.id.rbdaily)).getText().toString();
		
		Calendar calendar = Calendar.getInstance();
		
		wvDay.setViewAdapter(new DayArrayAdapter(ScheduleRequestScreen.this, calendar));
		wvDay.setCurrentItem(MMAPIConstants.DAYS_PREVIOUS);
		
		numericWheelAdapter = new NumericWheelAdapter(ScheduleRequestScreen.this, 1, 12);
		numericWheelAdapter.setItemResource(R.layout.wheel_text_item);
		numericWheelAdapter.setItemTextResource(R.id.text);
		wvHours.setViewAdapter(numericWheelAdapter);
		if(calendar.get(Calendar.HOUR) == 0) {
			wvHours.setCurrentItem(11);
		} else {
			wvHours.setCurrentItem(calendar.get(Calendar.HOUR) - 1);
		}
		wvHours.setCyclic(true);
		wvHours.addChangingListener(ScheduleRequestScreen.this);
		
		numericWheelAdapter = new NumericWheelAdapter(ScheduleRequestScreen.this, 0, 59, "%02d");
		numericWheelAdapter.setItemResource(R.layout.wheel_text_item);
		numericWheelAdapter.setItemTextResource(R.id.text);
		wvMins.setViewAdapter(numericWheelAdapter);
		wvMins.setCurrentItem(calendar.get(Calendar.MINUTE));
		wvMins.setCyclic(true);
		wvMins.addChangingListener(ScheduleRequestScreen.this);
		
		ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(this, new String[] {"AM", "PM"});
        ampmAdapter.setItemResource(R.layout.wheel_text_item);
        ampmAdapter.setItemTextResource(R.id.text);
        wvAMPM.setViewAdapter(ampmAdapter);
        wvAMPM.setCurrentItem(calendar.get(Calendar.AM_PM));
	}
	
	/*
	 * (non-Javadoc)
	 * @see kankan.wheel.widget.OnWheelChangedListener#onChanged(kankan.wheel.widget.WheelView, int, int)
	 */
	/**
	 * Changes the hours wheels if the mins wheel rollover from 59 to 0 and changes days/ampm wheel if hour roll over. 
	 */
	// TODO: clean up mess of working code
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if(wheel == wvHours) {
			if(oldValue == 10 && newValue == 11) {
				if(wvAMPM.getCurrentItem() == 0) {
					wvAMPM.setCurrentItem(1);
				} else if(wvAMPM.getCurrentItem() == 1) {
					wvDay.setCurrentItem(wvDay.getCurrentItem() + 1);
					wvAMPM.setCurrentItem(0);
				}
			} else if(oldValue == 11 && newValue == 10) {
				if(wvAMPM.getCurrentItem() == 0) {
					wvDay.setCurrentItem(wvDay.getCurrentItem() - 1);
					wvAMPM.setCurrentItem(1);
				} else if(wvAMPM.getCurrentItem() == 1) {
					wvAMPM.setCurrentItem(0);
				}
			}
		} else if (wheel == wvMins) {
			if(oldValue == 59 && newValue == 0) {
				wvHours.setCurrentItem(wvHours.getCurrentItem() + 1);
			} else if(oldValue == 0 && newValue == 59) {
				wvHours.setCurrentItem(wvHours.getCurrentItem() - 1);
			}
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
			case R.id.rbdaily:
				repeatRate = ((RadioButton) findViewById(R.id.rbdaily)).getText().toString();
				break;
			case R.id.rbweekly:
				repeatRate = ((RadioButton) findViewById(R.id.rbweekly)).getText().toString();
				break;
			case R.id.rbmonthly:
				repeatRate = ((RadioButton) findViewById(R.id.rbmonthly)).getText().toString();
				break;
			}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnsetschedule:
				getDateAndTime();
				break;
		}
	}
	
	private void getDateAndTime() {
		Log.d(TAG, TAG + "day: " + (wvDay.getCurrentItem() - MMAPIConstants.DAYS_PREVIOUS));
		Log.d(TAG, TAG + "hour: " + (wvHours.getCurrentItem() + 1));
		Log.d(TAG, TAG + "mins: " + wvMins.getCurrentItem());
		Log.d(TAG, TAG + "am/pm: " + wvAMPM.getCurrentItem());
		
		int day = wvDay.getCurrentItem() - MMAPIConstants.DAYS_PREVIOUS;
		int hour = wvHours.getCurrentItem() + 1;
		int min = wvMins.getCurrentItem();
		int ampm = wvAMPM.getCurrentItem();
		
		Calendar requestCal = Calendar.getInstance(TimeZone.getDefault());
		
		requestCal.set(Calendar.HOUR, hour);
		requestCal.set(Calendar.MINUTE, min);
		
		if(hour == 12) {
			requestCal.add(Calendar.HOUR, 12);
			day -= 1;
		}
		
		if(ampm == 0) {
			requestCal.set(Calendar.AM_PM, Calendar.AM);
		} else if (ampm == 1) {
			requestCal.set(Calendar.AM_PM, Calendar.PM);
		}
		
		requestCal.add(Calendar.DAY_OF_YEAR, day);
		
		Intent scheduleRequestIntent = new Intent();
		
		Log.d(TAG, TAG + "requestCal time: " + requestCal.getTimeInMillis());
		Log.d(TAG, TAG + "current time: " + System.currentTimeMillis());
		
		if(requestCal.getTimeInMillis() <= System.currentTimeMillis()) {
			Toast.makeText(ScheduleRequestScreen.this, R.string.toast_current_or_past_current_time, Toast.LENGTH_LONG).show();
		} else {
			scheduleRequestIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME, requestCal);
			scheduleRequestIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING, tbRepeating.isChecked());
			if(tbRepeating.isChecked()) {
				// TODO: Might have to change this depends on the server call and what it accepts
				scheduleRequestIntent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING_RATE, repeatRate);
			}
			
			setResult(RESULT_OK, scheduleRequestIntent);
			finish();
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
