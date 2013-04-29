package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSocialNetworksItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MMSocialNetworksArrayAdapter extends ArrayAdapter<MMSocialNetworksItem>{

	private Context context; 
	private int layoutResourceId;    
	private MMSocialNetworksItem[] data = null;
	
	public MMSocialNetworksArrayAdapter(Context context, int layoutResourceId, MMSocialNetworksItem[] data) {
        
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
		
		MMSocialNetworksItem item = data[position];
		vholder.tvTitle.setText(item.title);
		vholder.tbSocialNetwork.setChecked(item.isOn);
		
		return row;
	}
    
	private class ViewHolder {
        TextView tvTitle;
        ToggleButton tbSocialNetwork;
	}
}
