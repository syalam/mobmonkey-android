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
    	
		View row = convertView;
		ViewHolder vholder;
		
		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			vholder = new ViewHolder();
			try {
				vholder.tvTitle = (TextView) row.findViewById(R.id.tvansweredrequests_title);
				vholder.ivImage = (ImageView) row.findViewById(R.id.ivansweredrequests_media_image);
				vholder.btnAccept = (ImageButton) row.findViewById(R.id.ibtnansweredrequests_accept);
				vholder.btnReject = (ImageButton) row.findViewById(R.id.ibtnansweredrequests_reject);
				
				vholder.btnPlay = (ImageButton) row.findViewById(R.id.ibtvansweredrequest_play);
				vholder.tvExp = (TextView) row.findViewById(R.id.tvansweredrequest_expirydate);
				vholder.btnOverlay = (ImageButton) row.findViewById(R.id.ibtvansweredrequest_moreoverlay);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMMediaItem item = data[position];
		vholder.tvTitle.setText(item.getLocationName());
		
		// set time
		
		long expiretime = Long.parseLong(item.getExpiryDate()) - System.currentTimeMillis();
		expiretime = TimeUnit.MILLISECONDS.toMinutes(expiretime);
		vholder.tvExp.setText(MMSDKConstants.DEFAULT_STRING_SPACE + expiretime + "m");
		
		if(item.getImageMedia() != null) {
			// image type
			if(item.isImage()) {
				vholder.btnPlay.setVisibility(View.GONE);
				vholder.tvExp.setVisibility(View.VISIBLE);
				vholder.btnOverlay.setVisibility(View.VISIBLE);
				//Log.d(TAG, "imgURI: " + item.mediaUri.toString());
				
				Bitmap bm = item.getImageMedia();
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				
				vholder.ivImage.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, wm.getDefaultDisplay().getWidth(), 400));
				
				// set onclick listener
				vholder.btnAccept.setOnClickListener(item.getAcceptMediaOnClickListener());
				vholder.btnReject.setOnClickListener(item.getRejectMediaOnClickListener());
				vholder.tvTitle.setOnClickListener(item.getLocationNameOnClickListener());
				vholder.ivImage.setOnClickListener(item.getImageOnClickListener());
			} 
			// videow type
			else if(item.isVideo()) {
				vholder.tvExp.setVisibility(View.VISIBLE);
				vholder.btnOverlay.setVisibility(View.VISIBLE);
				vholder.btnPlay.setVisibility(View.VISIBLE);
				
				// set onclick listener
				vholder.btnAccept.setOnClickListener(item.getAcceptMediaOnClickListener());
				vholder.btnReject.setOnClickListener(item.getRejectMediaOnClickListener());
				vholder.tvTitle.setOnClickListener(item.getLocationNameOnClickListener());
//				
//				// create thumbnail for video
////				Bitmap bm = ThumbnailUtils.createVideoThumbnail(item.mediaUri.toString(), Video.Thumbnails.FULL_SCREEN_KIND);
//				InputStream is;
//				try {
//					is = context.getContentResolver().openInputStream(item.mediaUri);
//					Bitmap bm = BitmapFactory.decodeStream(is);
//					// set ivImage to thumbnail
//					WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//					vholder.ivImage.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, wm.getDefaultDisplay().getWidth(), 400));
//					is.close();
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
		}
		// if media is null
		else {
			// handle null media
		}
		
		// if fulfilled, hide buttons
		if(item.isAccepted()) {
			vholder.btnAccept.setVisibility(View.GONE);
			vholder.btnReject.setVisibility(View.GONE);
		}
		
		return row;
	}
    
	private class ViewHolder {
        ImageView ivImage;
        ImageButton btnAccept, btnReject, btnPlay, btnOverlay;
        TextView tvTitle, tvExp;
    }
}
