package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMMyInterestsItem;
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
public class MMMyInterestsArrayAdapter extends ArrayAdapter<MMMyInterestsItem> {
	private Context context;
	private LayoutInflater layoutInflater;
	private int listRowLayout;
	private MMMyInterestsItem[] myInterestsItems;
	
	public MMMyInterestsArrayAdapter(Context context, int listRowLayout, MMMyInterestsItem[] myInterestsItems) {
		super(context, listRowLayout, myInterestsItems);
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.listRowLayout = listRowLayout;
		this.myInterestsItems = myInterestsItems;
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
			searchCategoryRow = inflater.inflate(listRowLayout, parent, false);
			
			vHolder = new ViewHolder();
			vHolder.ivCatIcon = (ImageView) searchCategoryRow.findViewById(R.id.ivicon);
			vHolder.tvCatName = (TextView) searchCategoryRow.findViewById(R.id.tvlabel);
			vHolder.ivCatIndicatorIcon = (ImageView) searchCategoryRow.findViewById(R.id.ivindicatoricon);
			searchCategoryRow.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) searchCategoryRow.getTag();
		}
		
		if(myInterestsItems[position].getInterestIconId() == 0) {
			vHolder.ivCatIcon.setVisibility(View.GONE);
		} else {
			vHolder.ivCatIcon.setImageResource(myInterestsItems[position].getInterestIconId());
		}
		vHolder.tvCatName.setText(myInterestsItems[position].getInterestName());
		vHolder.ivCatIndicatorIcon.setImageResource(myInterestsItems[position].getInterestIndicatorIconId());
		
        searchCategoryRow.setBackgroundColor(Color.TRANSPARENT);
		return searchCategoryRow;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public MMMyInterestsItem getItem(int position) {
		return myInterestsItems[position];
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
