package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMOpenRequestsItem;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MMOpenRequestsArrayAdapter extends ArrayAdapter<MMOpenRequestsItem>{
	
	private Context context;
	private int layoutResourceId;
	private MMOpenRequestsItem[] openRequestsItems = null;
    
    public MMOpenRequestsArrayAdapter(Context context, int layoutResourceId, MMOpenRequestsItem[] openRequestsItems) {
    	super(context, layoutResourceId, openRequestsItems);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.openRequestsItems = openRequestsItems;
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View openRequestsRow = convertView;
		ViewHolder vHolder;
		
		if(openRequestsRow == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			openRequestsRow = inflater.inflate(layoutResourceId, parent, false);
			
			vHolder = new ViewHolder();
			vHolder.tvTitle = (TextView) openRequestsRow.findViewById(R.id.tvopenrequests_title);
			vHolder.tvDis = (TextView) openRequestsRow.findViewById(R.id.tvopenrequests_dist);
			vHolder.tvMessage = (TextView) openRequestsRow.findViewById(R.id.tvopenrequests_message);
			vHolder.tvTime = (TextView) openRequestsRow.findViewById(R.id.tvopenrequests_time);
			vHolder.ivIcon = (ImageView) openRequestsRow.findViewById(R.id.ivopenrequests_icon);
			
			openRequestsRow.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) openRequestsRow.getTag();
		}
		
		MMOpenRequestsItem openRequestsItem = openRequestsItems[position];
		vHolder.tvTitle.setText(openRequestsItem.title);
		vHolder.tvDis.setText(openRequestsItem.dis);
		vHolder.tvMessage.setText(openRequestsItem.message);
		vHolder.tvTime.setText(openRequestsItem.time);

		switch(openRequestsItem.mediaType) {
			case MMSDKConstants.MEDIA_TYPE_IMAGE:
				vHolder.ivIcon.setImageResource(R.drawable.media_icon_image);
				break;
			case MMSDKConstants.MEDIA_TYPE_VIDEO:
				vHolder.ivIcon.setImageResource(R.drawable.media_icon_video);
				break;
			default:
				vHolder.ivIcon.setVisibility(View.INVISIBLE);
		}
		
		return openRequestsRow;
	}
    
	private class ViewHolder {
        private ImageView ivIcon;
        private TextView tvTitle, tvDis, tvMessage, tvTime;
    }
}
