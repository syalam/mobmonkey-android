package com.mobmonkey.mobmonkeyandroid.utils;

import com.mobmonkey.mobmonkeyandroid.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MMInboxArrayAdapter extends ArrayAdapter<MMInboxItem>{

	private Context context; 
	private int layoutResourceId;    
	private MMInboxItem data[] = null;
    
    public MMInboxArrayAdapter(Context context, int layoutResourceId, MMInboxItem[] data) {
    	super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

	@Override
	public boolean isEnabled(int position) {
		if(data[position].containCounter > 0) {
			return true;
		} else {
			return false;
		}
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
				vholder.tvLabel = (TextView) row.findViewById(R.id.tvinbox);
				vholder.tvCounter = (TextView) row.findViewById(R.id.inboxBadgeCounter);
			} catch (NullPointerException ex) {
				
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMInboxItem item = data[position];
		vholder.tvLabel.setText(item.title);
		if(item.containCounter > 0) {
			vholder.tvLabel.setTextColor(Color.BLACK);
		} else {
			vholder.tvLabel.setTextColor(Color.GRAY);
		}
		vholder.tvCounter.setText(item.counter);
		
		return row;
	}
    
	private class ViewHolder {
        TextView tvLabel;
        TextView tvCounter;
    }
}
