package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMHistoryArrayAdapter extends ArrayAdapter<JSONObject> {
	private Location location;
	private Context context;
	private LayoutInflater layoutInflater;
	private int listRowLayout;
	private ArrayList<JSONObject> locations;
	
	public MMHistoryArrayAdapter(Context context, int listRowLayout, ArrayList<JSONObject> locations) {
		super(context, listRowLayout, locations);
		this.location = MMLocationManager.getGPSLocation(new MMLocationListener());
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
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
		
		try {
			JSONObject jObj = locations.get(position);
			viewHolder.tvLocName.setText(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
			viewHolder.tvLocDist.setText(MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + 
					context.getString(R.string.miles));
			viewHolder.tvLocAddr.setText(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS) + MMSDKConstants.DEFAULT_STRING_NEWLINE + jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY) + MMSDKConstants.DEFAULT_STRING_COMMA_SPACE + 
					jObj.getString(MMSDKConstants.JSON_KEY_REGION));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        convertView.setBackgroundColor(Color.TRANSPARENT);
        return convertView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public JSONObject getItem(int position) {
		return locations.get(position);
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class ViewHolder {
		TextView tvLocName;
		TextView tvLocDist;
		TextView tvLocAddr;
	}
}
