package com.mobmonkey.mobmonkey.utils;

import com.mobmonkey.mobmonkey.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class MMMyInfoArrayAdapter extends ArrayAdapter<MMMyinfoItem>{
	
	Context context; 
    int layoutResourceId;    
    MMMyinfoItem data[] = null;
	
	public MMMyInfoArrayAdapter(Context context, int layoutResourceId, MMMyinfoItem[] data) {
        
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
				vholder.eText = (EditText) row.findViewById(R.id.my_info_editText);
			} catch (NullPointerException ex) {
				
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMMyinfoItem item = data[position];
		vholder.eText.setHint(item.hint);
		
		return row;
	}
	
	private class ViewHolder {
		EditText eText;
	}
}
