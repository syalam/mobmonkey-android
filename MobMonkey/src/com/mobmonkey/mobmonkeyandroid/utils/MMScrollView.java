package com.mobmonkey.mobmonkeyandroid.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMScrollView extends ScrollView {
	private static final String TAG = "MMScrollView: ";
	
	private boolean mIsDisable = false;
	
	public MMScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.ScrollView#onTouchEvent(android.view.MotionEvent)
	 */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // no more touch events for this ScrollView
    	Log.d(TAG, TAG + "MotionEvent: " + ev.getAction());
        if (mIsDisable) {
            return false;
        }       
        return super.onTouchEvent(ev);
    }
    
    public void setDisableStatus(boolean status) {
    	mIsDisable = status;
    }
}
