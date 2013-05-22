package com.mobmonkey.mobmonkeyandroid.fragments;

import com.mobmonkey.mobmonkeyandroid.R;

import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * @author Dezapp, LLC
 *
 */
public class AddNotificationsFragment extends MMFragment {

	private static final String TAG = "AddNotificationsFragment";
	private WheelView location;
	private String[] contentArray;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_notifications, container, false);
		contentArray = new String[]{"Today", "One Day", "One Week", "Always"};
		location = (WheelView) getActivity().findViewById(R.id.wheellocation);
		location.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), contentArray));
		location.setCurrentItem(0);
		
		return view;
	}

	@Override
	public void onFragmentBackPressed() {
		
	}
}
