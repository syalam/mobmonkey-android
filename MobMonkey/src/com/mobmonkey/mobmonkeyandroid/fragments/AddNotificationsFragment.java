package com.mobmonkey.mobmonkeyandroid.fragments;

import com.mobmonkey.mobmonkeyandroid.R;

import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * @author Dezapp, LLC
 *
 */
public class AddNotificationsFragment extends MMFragment implements OnClickListener,
																	OnWheelChangedListener {
	private static final String TAG = "AddNotificationsFragment: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private Button btnDone;
	private WheelView wvTimeFrame;
	private String[] timeFrame;
	
	private String user;
	private int timeFramePosition;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_add_notifications, container, false);
		btnDone = (Button) view.findViewById(R.id.btndone);
		wvTimeFrame = (WheelView) view.findViewById(R.id.wvtimeframe);
		timeFrame = getResources().getStringArray(R.array.add_notications_time_frame);
		
		user = userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY);
		timeFramePosition = userPrefs.getInt(user + MMSDKConstants.KEY_INTENT_EXTRA_TIME_FRAME_POSITION, MMSDKConstants.DEFAULT_INT_ZERO);
		
		btnDone.setOnClickListener(AddNotificationsFragment.this);
		wvTimeFrame.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), timeFrame));
		wvTimeFrame.setCurrentItem(timeFramePosition);
		wvTimeFrame.addChangingListener(AddNotificationsFragment.this);
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		Log.d(TAG, TAG + "onClick");
		switch(view.getId()) {
			case R.id.btndone:
				userPrefsEditor.putInt(user + MMSDKConstants.KEY_INTENT_EXTRA_TIME_FRAME_POSITION, timeFramePosition);
				userPrefsEditor.commit();
				getActivity().onBackPressed();
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see kankan.wheel.widget.OnWheelChangedListener#onChanged(kankan.wheel.widget.WheelView, int, int)
	 */
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		Log.d(TAG, TAG + "time frame: " + timeFrame[newValue]);
		timeFramePosition = newValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}
}
