package com.mobmonkey.mobmonkeyandroid.utils;

import java.util.LinkedList;

import com.mobmonkey.mobmonkeyandroid.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MMTopViewedArrayAdapter extends ArrayAdapter<MMMediaItem> {
	private Context context; 
	private int layoutResourceId;    
	private MMMediaItem[] data;
    
    public MMTopViewedArrayAdapter(Context context, int layoutResourceId, MMMediaItem[] data) {
    	super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View topViewedRow = convertView;
		ViewHolder vholder;
		
		if(topViewedRow == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			topViewedRow = inflater.inflate(layoutResourceId, parent, false);
			
			vholder = new ViewHolder();
			vholder.tvLocName = (TextView) topViewedRow.findViewById(R.id.tvlocname);
			vholder.ivtnMedia = (ImageView) topViewedRow.findViewById(R.id.ivtnmedia);
			vholder.tvTime = (TextView) topViewedRow.findViewById(R.id.tvtime);
			vholder.ibPlay = (ImageButton) topViewedRow.findViewById(R.id.ibplay);
			vholder.ibShareMedia = (ImageButton) topViewedRow.findViewById(R.id.ibsharemedia);
			
			topViewedRow.setTag(vholder);
		} else {
			vholder = (ViewHolder) topViewedRow.getTag();
		}
		
		MMMediaItem mmMediaItem = data[position];
		vholder.tvLocName.setText(mmMediaItem.getLocationName());
		vholder.ivtnMedia.setImageBitmap(mmMediaItem.getImageMedia());
		vholder.tvTime.setText(mmMediaItem.getExpiryDate());
		vholder.ibShareMedia.setOnClickListener(mmMediaItem.getShareMediaOnClickListener());
		
		if(mmMediaItem.isVideo()) {
			vholder.ibPlay.setVisibility(View.VISIBLE);
			vholder.ibPlay.setOnClickListener(mmMediaItem.getPlayOnClickListener());
		} else if(mmMediaItem.isImage()) {
			vholder.ivtnMedia.setOnClickListener(mmMediaItem.getImageOnClickListener());
		}
		
		return topViewedRow;
	}
    
	private class ViewHolder {
        TextView tvLocName;
        ImageView ivtnMedia;
        TextView tvTime;
        ImageButton ibPlay;
        ImageButton ibShareMedia;
        
    }
}
