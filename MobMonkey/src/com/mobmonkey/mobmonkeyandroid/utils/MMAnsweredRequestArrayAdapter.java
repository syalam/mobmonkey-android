package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.MMAcceptMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMRejectMediaOnClickListener;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class MMAnsweredRequestArrayAdapter extends ArrayAdapter<MMMediaItem>{
	
	private static final String TAG = "MMAnsweredRequestArrayAdapter";
	
	private Context context; 
	private int layoutResourceId;    
	private MMMediaItem data[] = null;
    
    public MMAnsweredRequestArrayAdapter(Context context, int layoutResourceId, MMMediaItem[] data) {
    	super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	
		View answeredRequestsListRow = convertView;
		ViewHolder vholder = null;
		
		if(answeredRequestsListRow == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			answeredRequestsListRow = inflater.inflate(layoutResourceId, parent, false);
			
			vholder = new ViewHolder();
			try {
				vholder.tvLocName = (TextView) answeredRequestsListRow.findViewById(R.id.tvlocname);
				vholder.ivtnMedia = (ImageView) answeredRequestsListRow.findViewById(R.id.ivtnmedia);
				vholder.ibPlay = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibplay);
				vholder.tvTime = (TextView) answeredRequestsListRow.findViewById(R.id.tvtime);
				vholder.ibShareMedia = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibsharemedia);
				vholder.ibAccept = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibaccept);
				vholder.ibReject = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibreject);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
			
			answeredRequestsListRow.setTag(vholder);
		} else {
			vholder = (ViewHolder) answeredRequestsListRow.getTag();
		}
		
		MMMediaItem mmMediaItem = data[position];
		vholder.tvLocName.setText(mmMediaItem.getLocationName());
		vholder.tvTime.setText(mmMediaItem.getExpiryDate());

//		if(mmMediaItem.getImageMedia() != null) {
			if(mmMediaItem.isImage()) {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				
				vholder.ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(mmMediaItem.getImageMedia(), wm.getDefaultDisplay().getWidth(), 400));
				vholder.ivtnMedia.setOnClickListener(mmMediaItem.getImageOnClickListener());
			} else if(mmMediaItem.isVideo()) {
				vholder.ibPlay.setVisibility(View.VISIBLE);
				vholder.ibPlay.setOnClickListener(mmMediaItem.getPlayOnClickListener());
//				vholder.ivtnMedia.setImageBitmap(mmMediaItem.getImageMedia());
			}
//		} else {
			// TODO: handle null media
//		}

		if(mmMediaItem.isAccepted()) {
			vholder.ibAccept.setVisibility(View.GONE);
			vholder.ibReject.setVisibility(View.GONE);
		} else {
			vholder.ibAccept.setVisibility(View.VISIBLE);
			vholder.ibReject.setVisibility(View.VISIBLE);
			vholder.ibAccept.setOnClickListener(mmMediaItem.getAcceptMediaOnClickListener());
			vholder.ibReject.setOnClickListener(mmMediaItem.getRejectMediaOnClickListener());
		}
		
		vholder.tvLocName.setOnClickListener(mmMediaItem.getLocationNameOnClickListener());
		vholder.ibShareMedia.setOnClickListener(mmMediaItem.getShareMediaOnClickListener());
		
		return answeredRequestsListRow;
	}
    
	private class ViewHolder {
        ImageView ivtnMedia;
        ImageButton ibAccept, ibReject, ibPlay, ibShareMedia;
        TextView tvLocName, tvTime;
	}
}
