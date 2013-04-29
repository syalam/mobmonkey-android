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
	
	public MMSearchCategoriesArrayAdapter(Context context, int layoutResourceId, MMSearchCategoriesItem[] mmSearchCategoryItems) {
		super(context, layoutResourceId, mmSearchCategoryItems);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.mmSearchCategoryItems = mmSearchCategoryItems;
	}

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
			searchCategoryRow.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) searchCategoryRow.getTag();
		}
		
		vHolder.ivCatIcon.setImageResource(mmSearchCategoryItems[position].getCatIconId());
		vHolder.tvCatName.setText(mmSearchCategoryItems[position].getCatName());
		
        if(!MMLocationManager.isGPSEnabled() || MMLocationManager.getGPSLocation(new MMLocationListener()) == null) {
        	vHolder.tvCatName.setTextColor(Color.GRAY);
        }
		
        searchCategoryRow.setBackgroundColor(Color.TRANSPARENT);
		return searchCategoryRow;
	}
	
	private class ViewHolder {
		ImageView ivCatIcon;
		TextView tvCatName;
	}
}
