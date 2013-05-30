package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchCategoriesItem;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

import android.app.Activity;
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
public class MMSearchCategoriesArrayAdapter extends ArrayAdapter<MMSearchCategoriesItem> {
	private Context context;
	private int layoutResourceId;
	private MMSearchCategoriesItem[] mmSearchCategoryItems;
	
	/**
	 * 
	 * @param context
	 * @param layoutResourceId
	 * @param mmSearchCategoryItems
	 */
	public MMSearchCategoriesArrayAdapter(Context context, int layoutResourceId, MMSearchCategoriesItem[] mmSearchCategoryItems) {
		super(context, layoutResourceId, mmSearchCategoryItems);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.mmSearchCategoryItems = mmSearchCategoryItems;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View searchCategoryRow = convertView;
		ViewHolder vHolder = null;
		
		if(searchCategoryRow == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			searchCategoryRow = inflater.inflate(layoutResourceId, parent, false);
			
			vHolder = new ViewHolder();
			vHolder.ivCatIcon = (ImageView) searchCategoryRow.findViewById(R.id.ivicon);
			vHolder.tvCatName = (TextView) searchCategoryRow.findViewById(R.id.tvlabel);
			vHolder.ivCatIndicatorIcon = (ImageView) searchCategoryRow.findViewById(R.id.ivindicatoricon);
			searchCategoryRow.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) searchCategoryRow.getTag();
		}
		
		if(mmSearchCategoryItems[position].getCatIconId() == 0) {
			vHolder.ivCatIcon.setVisibility(View.GONE);
		} else {
			vHolder.ivCatIcon.setImageResource(mmSearchCategoryItems[position].getCatIconId());
		}
		vHolder.tvCatName.setText(mmSearchCategoryItems[position].getCatName());
		vHolder.ivCatIndicatorIcon.setImageResource(mmSearchCategoryItems[position].getCatIndicatorIconId());
		
        if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation() == null) {
        	vHolder.tvCatName.setTextColor(Color.GRAY);
        }
		
        searchCategoryRow.setBackgroundColor(Color.TRANSPARENT);
		return searchCategoryRow;
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class ViewHolder {
		ImageView ivCatIcon;
		TextView tvCatName;
		ImageView ivCatIndicatorIcon;
	}
}
