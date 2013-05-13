package com.mobmonkey.mobmonkeyandroid.arrayadapters;

import java.util.LinkedList;
import java.util.List;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMMediaItem;

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
		ViewHolder vHolder;
		
		if(media == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			media = inflater.inflate(mediaLayoutId, parent, false);
			
			vHolder = new ViewHolder();
			vHolder.ivtnMedia = (ImageView) media.findViewById(R.id.ivtnmedia);
			vHolder.tvExpiryDate = (TextView) media.findViewById(R.id.tvexpirydate);
			vHolder.ibPlay = (ImageButton) media.findViewById(R.id.ibplay);
			vHolder.ibShareMedia = (ImageButton) media.findViewById(R.id.ibsharemedia);
			
			media.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) media.getTag();
		}
		
		MMMediaItem mmMediaItem = mmMediaItems.get(position);
		vHolder.ivtnMedia.setImageBitmap(mmMediaItem.getImageMedia());
		vHolder.tvExpiryDate.setText(mmMediaItem.getExpiryDate());
		vHolder.ibShareMedia.setOnClickListener(mmMediaItem.getShareMediaOnClickListener());
		
		if(mmMediaItem.isVideo()) {
			vHolder.ibPlay.setVisibility(View.VISIBLE);
			vHolder.ibPlay.setOnClickListener(mmMediaItem.getPlayOnClickListener());
		} else if(mmMediaItem.isImage()) {
			vHolder.ibPlay.setVisibility(View.INVISIBLE);
			vHolder.ivtnMedia.setOnClickListener(mmMediaItem.getImageOnClickListener());
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
