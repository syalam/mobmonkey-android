package com.mobmonkey.mobmonkeyandroid.utils;

import com.mobmonkey.mobmonkeyandroid.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MMOpenedRequestsArrayAdapter extends ArrayAdapter<MMOpenedRequestsItem>{
	
	private Context context;
	private int layoutResourceId;
	private MMOpenedRequestsItem data[] = null;
    
    public MMOpenedRequestsArrayAdapter(Context context, int layoutResourceId, MMOpenedRequestsItem[] data) {
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
				vholder.tvTitle = (TextView) row.findViewById(R.id.tvopenrequests_title);
				vholder.tvDis = (TextView) row.findViewById(R.id.tvopenrequests_dist);
				vholder.tvMessage = (TextView) row.findViewById(R.id.tvopenrequests_message);
				vholder.tvTime = (TextView) row.findViewById(R.id.tvopenrequests_time);
				vholder.ivIcon = (ImageView) row.findViewById(R.id.ivopenrequests_icon);
			} catch (NullPointerException ex) {
				
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMOpenedRequestsItem item = data[position];
		vholder.tvTitle.setText(item.title);
		vholder.tvDis.setText(item.dis);
		vholder.tvMessage.setText(item.message);
		vholder.tvTime.setText(item.time);
		
		// image type
		if(item.mediaType == 1) {
			vholder.ivIcon.setImageResource(R.drawable.image_media_icon);
		} 
		// videow type
		else if(item.mediaType == 2) {
			vholder.ivIcon.setImageResource(R.drawable.video_media_icon);
		}
		
		return row;
	}
    
	private class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDis, tvMessage, tvTime;
    }
}
