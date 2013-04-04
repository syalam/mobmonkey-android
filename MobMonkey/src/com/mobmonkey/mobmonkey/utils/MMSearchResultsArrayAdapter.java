package com.mobmonkey.mobmonkey.utils;

import com.mobmonkey.mobmonkey.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMSearchResultsArrayAdapter extends ArrayAdapter<MMResultsLocation> {
	private LayoutInflater layoutInflater;
	private int listRowLayout;
	private MMResultsLocation[] locations;
	
	public MMSearchResultsArrayAdapter(Context context, int listRowLayout, MMResultsLocation[] locations) {
		super(context, listRowLayout, locations);
		layoutInflater = LayoutInflater.from(context);
		this.listRowLayout = listRowLayout;
		this.locations = locations;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if(convertView == null) {
            convertView = layoutInflater.inflate(listRowLayout, null);
            
            viewHolder = new ViewHolder();
            viewHolder.tvLocName = (TextView) convertView.findViewById(R.id.tvlocname);
            viewHolder.tvLocDist = (TextView) convertView.findViewById(R.id.tvlocdist);
            viewHolder.tvLocAddr = (TextView) convertView.findViewById(R.id.tvlocaddr);
            convertView.setTag(viewHolder);
            
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvLocName.setText(locations[position].getLocName());
        viewHolder.tvLocDist.setText(locations[position].getLocDist());
        viewHolder.tvLocAddr.setText(locations[position].getLocAddr());
        convertView.setBackgroundColor(Color.TRANSPARENT);
        return convertView;
	}

	private class ViewHolder {
		TextView tvLocName;
		TextView tvLocDist;
		TextView tvLocAddr;
	}
}
