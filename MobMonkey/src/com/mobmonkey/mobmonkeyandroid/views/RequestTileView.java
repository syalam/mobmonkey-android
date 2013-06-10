package com.mobmonkey.mobmonkeyandroid.views;

import com.mobmonkey.mobmonkeyandroid.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class RequestTileView extends LinearLayout {

	public RequestTileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		layoutInflater.inflate(R.layout.linear_request_panel, this);
	}



}
