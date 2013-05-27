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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMNearbyLocationsArrayAdapter extends ArrayAdapter<JSONObject> implements Filterable {
	private static final String TAG = "MMNearbyLocationsArrayAdapter: ";
	
	private Location location;
	private Context context;
	private LayoutInflater layoutInflater;
	private int listRowLayout;
	private ArrayList<JSONObject> locations;
	private ArrayList<JSONObject> filteredLocations;
	private MMFilter mmFilter;
	
	public MMNearbyLocationsArrayAdapter(Context context, int listRowLayout, ArrayList<JSONObject> locations) {
		super(context, listRowLayout, locations);
		this.location = MMLocationManager.getGPSLocation();
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.listRowLayout = listRowLayout;
		this.locations = locations;
		this.filteredLocations = locations;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		Log.d(TAG, TAG + "count: " + getCount() + "   position: " + position);
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
			JSONObject jObj = filteredLocations.get(position);
			viewHolder.tvLocName.setText(jObj.getString(MMSDKConstants.JSON_KEY_NAME));
			viewHolder.tvLocDist.setText(MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + 
					context.getString(R.string.miles));
			
			String address = MMSDKConstants.DEFAULT_STRING_EMPTY;
			address += jObj.isNull(MMSDKConstants.JSON_KEY_ADDRESS) ? MMSDKConstants.DEFAULT_STRING_EMPTY : jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS);
			address += MMSDKConstants.DEFAULT_STRING_NEWLINE;
			
			String localityRegion = MMSDKConstants.DEFAULT_STRING_EMPTY;
			localityRegion += jObj.isNull(MMSDKConstants.JSON_KEY_LOCALITY) ? MMSDKConstants.DEFAULT_STRING_EMPTY : jObj.getString(MMSDKConstants.JSON_KEY_LOCALITY);
			localityRegion += jObj.isNull(MMSDKConstants.JSON_KEY_LOCALITY) || jObj.isNull(MMSDKConstants.JSON_KEY_REGION) ? MMSDKConstants.DEFAULT_STRING_EMPTY : MMSDKConstants.DEFAULT_STRING_COMMA_SPACE;
			localityRegion += jObj.isNull(MMSDKConstants.JSON_KEY_REGION) ? MMSDKConstants.DEFAULT_STRING_EMPTY : jObj.getString(MMSDKConstants.JSON_KEY_REGION);
			
			viewHolder.tvLocAddr.setText(address + localityRegion);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        convertView.setBackgroundColor(Color.TRANSPARENT);
        
        return convertView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return filteredLocations.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public Filter getFilter() {
		if(mmFilter == null) {
			mmFilter = new MMFilter();
		}
		return mmFilter;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public JSONObject getItem(int position) {
		return filteredLocations.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#notifyDataSetChanged()
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
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
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class MMFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			ArrayList<JSONObject> filteredLocations = new ArrayList<JSONObject>();
			
			for(int i = 0; i < locations.size(); i++) {
				JSONObject jObj = locations.get(i);
				if(constraint == null || constraint.length() == 0) {
					filteredLocations.add(jObj);
				} else {
					try {
						if(jObj.getString(MMSDKConstants.JSON_KEY_NAME).contains(constraint)) {
							filteredLocations.add(jObj);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			results.values = filteredLocations;
			results.count = filteredLocations.size();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filteredLocations = (ArrayList<JSONObject>) results.values;
			notifyDataSetChanged();
		}
	}
}
