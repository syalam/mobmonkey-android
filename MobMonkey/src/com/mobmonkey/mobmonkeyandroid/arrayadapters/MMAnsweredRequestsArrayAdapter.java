package com.mobmonkey.mobmonkeyandroid.arrayadapters;

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
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMMediaItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMAcceptMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMRejectMediaOnClickListener;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class MMAnsweredRequestsArrayAdapter extends ArrayAdapter<MMMediaItem>{
	
	private static final String TAG = "MMAnsweredRequestArrayAdapter";
	
	private Context context; 
	private int layoutResourceId;    
	private MMMediaItem data[] = null;
    
    public MMAnsweredRequestsArrayAdapter(Context context, int layoutResourceId, MMMediaItem[] data) {
    	super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View answeredRequestsListRow = convertView;
		ViewHolder vHolder = null;
		
		if(answeredRequestsListRow == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			answeredRequestsListRow = inflater.inflate(layoutResourceId, parent, false);
			
			vHolder = new ViewHolder();
			vHolder.tvLocName = (TextView) answeredRequestsListRow.findViewById(R.id.tvlocname);
			vHolder.ivtnMedia = (ImageView) answeredRequestsListRow.findViewById(R.id.ivtnmedia);
			vHolder.ibPlay = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibplay);
			vHolder.tvTime = (TextView) answeredRequestsListRow.findViewById(R.id.tvtime);
			vHolder.ibShareMedia = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibsharemedia);
			vHolder.ibAccept = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibaccept);
			vHolder.ibReject = (ImageButton) answeredRequestsListRow.findViewById(R.id.ibreject);
			
			answeredRequestsListRow.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) answeredRequestsListRow.getTag();
		}
		
		MMMediaItem mmMediaItem = data[position];
		vHolder.tvLocName.setText(mmMediaItem.getLocationName());
		vHolder.tvTime.setText(mmMediaItem.getExpiryDate());

//		if(mmMediaItem.getImageMedia() != null) {
		vHolder.ivtnMedia.setImageBitmap(mmMediaItem.getImageMedia());
			if(mmMediaItem.isImage()) {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				vHolder.ivtnMedia.setOnClickListener(mmMediaItem.getImageOnClickListener());
				vHolder.ibPlay.setVisibility(View.INVISIBLE);
			} else if(mmMediaItem.isVideo()) {
				vHolder.ibPlay.setVisibility(View.VISIBLE);
				vHolder.ibPlay.setOnClickListener(mmMediaItem.getPlayOnClickListener());
			}
//		} else {
			// TODO: handle null media
//		}

		if(mmMediaItem.isAccepted()) {
			vHolder.ibAccept.setVisibility(View.GONE);
			vHolder.ibReject.setVisibility(View.GONE);
		} else {
			vHolder.ibAccept.setVisibility(View.VISIBLE);
			vHolder.ibReject.setVisibility(View.VISIBLE);
			vHolder.ibAccept.setOnClickListener(mmMediaItem.getAcceptMediaOnClickListener());
			vHolder.ibReject.setOnClickListener(mmMediaItem.getRejectMediaOnClickListener());
		}
		
		vHolder.tvLocName.setOnClickListener(mmMediaItem.getLocationNameOnClickListener());
		vHolder.ibShareMedia.setOnClickListener(mmMediaItem.getShareMediaOnClickListener());
		
		return answeredRequestsListRow;
	}
    
	private class ViewHolder {
        ImageView ivtnMedia;
        ImageButton ibAccept, ibReject, ibPlay, ibShareMedia;
        TextView tvLocName, tvTime;
	}
}
