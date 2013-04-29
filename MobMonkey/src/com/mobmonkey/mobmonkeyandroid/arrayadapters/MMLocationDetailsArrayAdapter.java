package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMLocationDetailsItem;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
public class MMLocationDetailsArrayAdapter extends ArrayAdapter<MMLocationDetailsItem> {
	private static final String TAG = "MMLocationDetailsArrayAdapter";
	
	private Context context;
	private int layoutResourceId;
	private MMLocationDetailsItem[] mmLocationDetailsItems;
	
	public MMLocationDetailsArrayAdapter(Context context, int layoutResourceId, MMLocationDetailsItem[] mmLocationDetailsItems) {
		super(context, layoutResourceId, mmLocationDetailsItems);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.mmLocationDetailsItems = mmLocationDetailsItems;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View locationDetailsRow = convertView;
		ViewHolder vHolder = null;
		
		if(locationDetailsRow == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			locationDetailsRow = inflater.inflate(layoutResourceId, parent, false);
			
			vHolder = new ViewHolder();
			vHolder.ivCatIcon = (ImageView) locationDetailsRow.findViewById(R.id.ivicon);
			vHolder.tvCatName = (TextView) locationDetailsRow.findViewById(R.id.tvlabel);
			locationDetailsRow.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) locationDetailsRow.getTag();
		}
		
//		Log.d(TAG, "position: " + position);
		vHolder.ivCatIcon.setImageResource(mmLocationDetailsItems[position].getLocationDetailIconId());
		vHolder.tvCatName.setText(mmLocationDetailsItems[position].getLocationDetail());
//		isEnabled(position);
		return locationDetailsRow;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
    @Override
	public boolean isEnabled(int position) {
		if(mmLocationDetailsItems[position].getLocationDetail().equals(context.getString(R.string.tv_no_phone_number_available))) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class ViewHolder {
		ImageView ivCatIcon;
		TextView tvCatName;
	}
}
