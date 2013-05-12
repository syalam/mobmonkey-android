package com.mobmonkey.mobmonkeyandroid.utils;

import com.mobmonkey.mobmonkeyandroid.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMExpandedListView extends ListView {
    private static final String TAG = "ExpandedListView: ";
    private ViewGroup.LayoutParams params;
    private int old_count = 0;
	
	public MMExpandedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
        if(getCount() != old_count) {
			old_count = getCount();
	        params = getLayoutParams();
	        int height = 0;
	        
	        ListAdapter adapter = getAdapter();
	        if(adapter == null) {
                height = 0;
	        } else {
                int desiredWidth = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
                for(int i = 0; i < adapter.getCount(); i++) {
                    View view = adapter.getView(i, null, this);
                    view.measure(desiredWidth, MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.UNSPECIFIED));
                    height += view.getMeasuredHeight();
                }
	        }
	        
	        params.height = height + getDividerHeight() * (old_count - 1);
	        setLayoutParams(params);
        }
		super.onDraw(canvas);
	}
}
