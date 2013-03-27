package com.mobmonkey.mobmonkey.utils;

import com.mobmonkey.mobmonkey.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MMInboxArrayAdapter extends ArrayAdapter<MMInboxItem>{

	Context context; 
    int layoutResourceId;    
    MMInboxItem data[] = null;
    int textColor;
    
    public MMInboxArrayAdapter(Context context, int layoutResourceId, MMInboxItem[] data, int textColor) {
    	super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.textColor = textColor;
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
		vholder.tvLabel.setTextColor(textColor);
		vholder.tvCounter.setText(item.counter);
		
		return row;
	}
    
	private class ViewHolder {
        TextView tvLabel;
        TextView tvCounter;
    }
}
