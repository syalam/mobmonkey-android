package com.mobmonkey.mobmonkey.utils;

import com.mobmonkey.mobmonkey.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MMSocialNetworkArrayAdapter extends ArrayAdapter<MMSocialNetworkItem>{

	private Context context; 
	private int layoutResourceId;    
	private MMSocialNetworkItem[] data = null;
	
	public MMSocialNetworkArrayAdapter(Context context, int layoutResourceId, MMSocialNetworkItem[] data) {
        
    	super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
	
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		ViewHolder vholder;
		
		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			vholder = new ViewHolder();
			try {
				vholder.tvTitle = (TextView) row.findViewById(R.id.tvsocialnetworktext);
				vholder.tbSocialNetwork = (ToggleButton) row.findViewById(R.id.togglesocialnetwork);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMSocialNetworkItem item = data[position];
		vholder.tvTitle.setText(item.title);
		vholder.tbSocialNetwork.setChecked(item.isOn);
		
		return row;
	}
    
	private class ViewHolder {
        TextView tvTitle;
        ToggleButton tbSocialNetwork;
	}
}
