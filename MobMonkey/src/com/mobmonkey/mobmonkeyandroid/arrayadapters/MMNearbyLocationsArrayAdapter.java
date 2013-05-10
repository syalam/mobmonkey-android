package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import java.util.ArrayList;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSearchResultsItem;

import android.content.Context;
import android.graphics.Color;
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
public class MMNearbyLocationsArrayAdapter extends ArrayAdapter<MMSearchResultsItem> implements Filterable {
	private LayoutInflater layoutInflater;
	private int listRowLayout;
	private ArrayList<MMSearchResultsItem> locations;
	private ArrayList<MMSearchResultsItem> filteredLocations;
	private MMFilter mmFilter;
	
	public MMNearbyLocationsArrayAdapter(Context context, int listRowLayout, ArrayList<MMSearchResultsItem> locations) {
		super(context, listRowLayout, locations);
		layoutInflater = LayoutInflater.from(context);
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
		
		viewHolder.tvLocName.setText(filteredLocations.get(position).getLocName());
        viewHolder.tvLocDist.setText(filteredLocations.get(position).getLocDist());
        viewHolder.tvLocAddr.setText(filteredLocations.get(position).getLocAddr());
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
	public MMSearchResultsItem getItem(int position) {
		return filteredLocations.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#notifyDataSetChanged()
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

//	public void setLocationsToDisplay(int num) {
//		locationsToDisplay = num;
//		filteredLocations = new ArrayList<MMSearchResultsItem>();
//		for(int i = 0; i < locationsToDisplay; i++) {
//			filteredLocations.add(locations.get(i));
//		}
//	}
	
	private class ViewHolder {
		TextView tvLocName;
		TextView tvLocDist;
		TextView tvLocAddr;
	}
	
	private class MMFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			filteredLocations = new ArrayList<MMSearchResultsItem>();
			
			for(int i = 0; i < locations.size(); i++) {
				MMSearchResultsItem item = locations.get(i);
				if(constraint == null || constraint.length() == 0) {
					filteredLocations.add(item);
				} else {
					if(item.getLocName().contains(constraint)) {
						filteredLocations.add(item);
					}
				}
			}
			
			results.values = filteredLocations;
			results.count = filteredLocations.size();
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			notifyDataSetChanged();
		}
	}
}
