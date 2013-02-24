package com.mobmonkey.mobmonkey.utils;

import com.mobmonkey.mobmonkey.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMArrayAdapter extends ArrayAdapter<Object> {
	Context context;
    private LayoutInflater layoutInflater;
    private int listRowLayout;
    private int[] icons;
    private String[] items;
    private int[] indicatorIcons;
    private int textAppearanceId;
    private Typeface textTypeface;
    private OnClickListener indicatorIconOnClickListener;
	
	public MMArrayAdapter(Context context, int listRowLayout, int[] icons, String[] items, int[] indicatorIcons, int textAppearanceId, Typeface textTypeface, OnClickListener indicatorIconOnClickListener) {
		super(context, listRowLayout, items);
		this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.listRowLayout = listRowLayout;
        this.icons = icons;
        this.items = items;
        this.indicatorIcons = indicatorIcons;
        this.textAppearanceId = textAppearanceId;
        this.textTypeface = textTypeface;
        this.indicatorIconOnClickListener = indicatorIconOnClickListener;
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
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivicon);
            viewHolder.tvLabel = (TextView) convertView.findViewById(R.id.tvlabel);
            viewHolder.ivIndicatorIcon = (ImageView) convertView.findViewById(R.id.ivindicatoricon);
	        viewHolder.tvLabel.setTextAppearance(context, textAppearanceId);
	        viewHolder.tvLabel.setTypeface(textTypeface);
            convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
        viewHolder.ivIcon.setImageResource(icons[position]);
        viewHolder.tvLabel.setText(items[position]);
        viewHolder.ivIndicatorIcon.setImageResource(indicatorIcons[position]);
        
        if(indicatorIconOnClickListener != null && position == 1) {
        	viewHolder.ivIndicatorIcon.setClickable(true);
        	viewHolder.ivIndicatorIcon.setOnClickListener(indicatorIconOnClickListener);
        }
        
        convertView.setBackgroundColor(Color.TRANSPARENT);
        return convertView;
	}
	
    /**
     * Private class that holds the views of the {@link ListView} row layout
     * @author Dezapp, LLC
     *
     */
    private class ViewHolder {
        ImageView ivIcon;
        TextView tvLabel;
        ImageView ivIndicatorIcon;
    }
}
