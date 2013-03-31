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

public class MMAssignedRequestsArrayAdapter extends ArrayAdapter<MMAssignedRequestsItem>{
	
	Context context; 
    int layoutResourceId;    
    MMAssignedRequestsItem data[] = null;
    
    public MMAssignedRequestsArrayAdapter(Context context, int layoutResourceId, MMAssignedRequestsItem[] data) {
        
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
				vholder.tvTitle = (TextView) row.findViewById(R.id.tvassignedrequests_title);
				vholder.tvDis = (TextView) row.findViewById(R.id.tvassignedrequests_dist);
				vholder.tvMessage = (TextView) row.findViewById(R.id.tvassignedrequests_message);
				vholder.tvTime = (TextView) row.findViewById(R.id.tvassignedrequests_time);
				vholder.ivIcon = (ImageView) row.findViewById(R.id.ivassignedrequests_icon);
			} catch (NullPointerException ex) {
				
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMAssignedRequestsItem item = data[position];
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
