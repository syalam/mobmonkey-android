package com.mobmonkey.mobmonkeyandroid.utils;

import java.util.LinkedList;

import com.mobmonkey.mobmonkeyandroid.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MMTopViewedArrayAdapter extends ArrayAdapter<MMTopViewedItem> {
	private Context context; 
	private  int layoutResourceId;    
	private LinkedList<MMTopViewedItem> data = null;
    
    public MMTopViewedArrayAdapter(Context context, int layoutResourceId, LinkedList<MMTopViewedItem> data) {
    	super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
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
				vholder.tvLabel = (TextView) row.findViewById(R.id.topviewedimagetitle);
				vholder.ivIcon = (ImageView) row.findViewById(R.id.topviewedimage);
			} catch (NullPointerException ex) {
				
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMTopViewedItem item = data.get(position);
		vholder.tvLabel.setText(item.getTitle());
		vholder.ivIcon.setImageBitmap(item.getImageMedia());
		
		return row;
	}
    
	private class ViewHolder {
        ImageView ivIcon;
        TextView tvLabel;
    }
}
