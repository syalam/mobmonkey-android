package com.mobmonkey.mobmonkeyandroid.utils;

import java.util.LinkedList;
import java.util.List;

import com.mobmonkey.mobmonkeyandroid.R;

import android.app.Activity;
import android.content.Context;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMediaArrayAdapter extends ArrayAdapter<MMMediaItem> {
	private static final String TAG = "MMMediaArrayAdapter";
	
	private Context context;
	private int mediaLayoutId;
	private LinkedList<MMMediaItem> mmMediaItems;
	
	public MMMediaArrayAdapter(Context context, int mediaLayoutId, LinkedList<MMMediaItem> mmMediaItems) {
		super(context, mediaLayoutId, mmMediaItems);
		this.context = context;
		this.mediaLayoutId = mediaLayoutId;
		this.mmMediaItems = mmMediaItems;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		View media = convertView;
		ViewHolder vholder;
		
		if(media == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			media = inflater.inflate(mediaLayoutId, parent, false);
			
			vholder = new ViewHolder();
			vholder.ivtnMedia = (ImageView) media.findViewById(R.id.ivtnmedia);
			vholder.tvExpiryDate = (TextView) media.findViewById(R.id.tvexpirydate);
			vholder.ibPlay = (ImageButton) media.findViewById(R.id.ibplay);
			vholder.ibShareMedia = (ImageButton) media.findViewById(R.id.ibsharemedia);
			
			media.setTag(vholder);
		} else {
			vholder = (ViewHolder) media.getTag();
		}
		
		MMMediaItem mmMediaItem = mmMediaItems.get(position);
		vholder.ivtnMedia.setImageBitmap(mmMediaItem.getImageMedia());
		vholder.tvExpiryDate.setVisibility(View.VISIBLE);
		vholder.tvExpiryDate.setText(mmMediaItem.getExpiryDate());
		vholder.ibShareMedia.setOnClickListener(mmMediaItem.getShareMediaOnClickListener());
		
		if(mmMediaItem.isVideo()) {
			vholder.ibPlay.setVisibility(View.VISIBLE);
			vholder.ibPlay.setOnClickListener(mmMediaItem.getPlayOnClickListener());
		} else if(mmMediaItem.isImage()) {
			vholder.ivtnMedia.setOnClickListener(mmMediaItem.getImageOnClickListener());
		}
		
		return media;
	}
	
	private class ViewHolder {
        ImageView ivtnMedia;
        TextView tvExpiryDate;
        ImageButton ibPlay;
        ImageButton ibShareMedia;
    }
}
