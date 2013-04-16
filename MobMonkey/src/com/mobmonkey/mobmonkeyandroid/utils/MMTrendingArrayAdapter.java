package com.mobmonkey.mobmonkeyandroid.utils;

import com.mobmonkey.mobmonkeyandroid.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MMTrendingArrayAdapter extends ArrayAdapter<MMTrendingItem> {
	private static final String TAG = "MMTrendingAdapter: ";
	
	private Context context; 
	private int layoutResourceId;    
	private MMTrendingItem data[] = null;
    
    public MMTrendingArrayAdapter(Context context, int layoutResourceId, MMTrendingItem[] data) {
        
    	super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		ViewHolder vholder;
		
		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			vholder = new ViewHolder();
			try {
				vholder.tvLabel = (TextView) row.findViewById(R.id.tvtrending);
				vholder.tvCounter = (TextView) row.findViewById(R.id.trendingBadgeCounter);
			} catch (NullPointerException ex) {
				
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMTrendingItem item = data[position];
		vholder.tvLabel.setText(item.title);
		if(item.counter > 0) {
			vholder.tvLabel.setTextColor(Color.BLACK);
		} else {
			vholder.tvLabel.setTextColor(Color.GRAY);
		}
		vholder.tvCounter.setText(Integer.toString(item.counter));
		
		return row;
	}
    
	@Override
	public boolean isEnabled(int position) {
		if(data[position].counter > 0) {
			return true;
		} else {
			return false;
		}
	}

	private class ViewHolder {
        TextView tvLabel;
        TextView tvCounter;
    }
}
