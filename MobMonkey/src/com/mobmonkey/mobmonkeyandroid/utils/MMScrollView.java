package com.mobmonkey.mobmonkeyandroid.utils;

import kankan.wheel.widget.WheelScroller.ScrollingListener;
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
	
	private MMScrollViewListener mmScrollViewListener = null;
	private boolean mIsDisable = false;
	
	public MMScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setScrollViewListener(MMScrollViewListener mmScrollViewListener) {
		this.mmScrollViewListener = mmScrollViewListener;
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
    
    /*
     * (non-Javadoc)
     * @see android.view.View#onScrollChanged(int, int, int, int)
     */
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
    	super.onScrollChanged(x, y, oldx, oldy);
    	if(mmScrollViewListener != null) {
    		mmScrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
    	}
    }
    
    public void setDisableStatus(boolean status) {
    	mIsDisable = status;
    }
}
