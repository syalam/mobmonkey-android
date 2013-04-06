package com.mobmonkey.mobmonkey.utils;

import com.mobmonkey.mobmonkey.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MMTopviewedArrayAdapter extends ArrayAdapter<MMTopviewedItem> {
	private Context context; 
	private  int layoutResourceId;    
	private MMTopviewedItem data[] = null;
    
    public MMTopviewedArrayAdapter(Context context, int layoutResourceId, MMTopviewedItem[] data) {
        
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
				vholder.tvLabel = (TextView) row.findViewById(R.id.topviewedimagetitle);
				vholder.ivIcon = (ImageView) row.findViewById(R.id.topviewedimage);
			} catch (NullPointerException ex) {
				
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMTopviewedItem item = data[position];
		vholder.tvLabel.setText(item.title);
		vholder.ivIcon.setImageBitmap(item.imageIcon);
		
		return row;
	}
    
	private class ViewHolder {
        ImageView ivIcon;
        TextView tvLabel;
    }
}
