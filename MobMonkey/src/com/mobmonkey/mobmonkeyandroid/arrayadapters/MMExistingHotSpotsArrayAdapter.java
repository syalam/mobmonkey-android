package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMExistingHotSpotsArrayAdapter extends ArrayAdapter<JSONObject> {
	private Context context;
	private LayoutInflater layoutInflater;
	private int resourceLayoutId;
	private ArrayList<JSONObject> subLocations;
	
	public MMExistingHotSpotsArrayAdapter(Context context, int resourceLayoutId, ArrayList<JSONObject> subLocations) {
		super(context, resourceLayoutId, subLocations);
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.resourceLayoutId = resourceLayoutId;
		this.subLocations = subLocations;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if(convertView == null) {
			convertView = layoutInflater.inflate(resourceLayoutId, null);
			viewHolder = new ViewHolder();
			viewHolder.tvHotSpotName = (TextView) convertView.findViewById(R.id.tvlabel);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		try {
			viewHolder.tvHotSpotName.setText(subLocations.get(position).getString(MMSDKConstants.JSON_KEY_NAME));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public JSONObject getItem(int position) {
		return subLocations.get(position);
	}
	
	private class ViewHolder {
		TextView tvHotSpotName;
	}
}
