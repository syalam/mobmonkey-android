package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMImageLoaderTask;

public class MMAnsweredRequestArrayAdapter extends ArrayAdapter<MMAnsweredRequestItem>{
	
	private static final String TAG = "MMAnsweredRequestArrayAdapter";
	
	private Context context; 
	private int layoutResourceId;    
	private MMAnsweredRequestItem data[] = null;
    
    public MMAnsweredRequestArrayAdapter(Context context, int layoutResourceId, MMAnsweredRequestItem[] data) {
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
				vholder.vvVideo = (VideoView) row.findViewById(R.id.vvansweredrequests_media_video);
				vholder.btnAccept = (ImageButton) row.findViewById(R.id.ibtnansweredrequests_accept);
				vholder.btnReject = (ImageButton) row.findViewById(R.id.ibtnansweredrequests_reject);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
			
			row.setTag(vholder);
		} else {
			vholder = (ViewHolder) row.getTag();
		}
		
		MMAnsweredRequestItem item = data[position];
		vholder.tvTitle.setText(item.title);
		
		// image type
		if(item.mediaType == 1) {
			vholder.ivImage.setVisibility(View.VISIBLE);

			Log.d(TAG, "imgURI: " + item.mediaUri.toString());
			vholder.ivImage.setImageBitmap(loadBitmap(item.mediaUri.toString()));
		} 
		// videow type
		else if(item.mediaType == 2) {
			vholder.vvVideo.setVisibility(View.VISIBLE);
			
			if(item.mediaUri != null)
				vholder.vvVideo.setVideoURI(item.mediaUri);
		}
		
		// if fulfilled, hide buttons
		if(item.isFulfilled) {
			vholder.btnAccept.setVisibility(View.GONE);
			vholder.btnReject.setVisibility(View.GONE);
		}
		
		return row;
	}
    
	private class ViewHolder {
        ImageView ivImage;
        VideoView vvVideo;
        ImageButton btnAccept, btnReject;
        TextView tvTitle;
    }
	
	public static Bitmap loadBitmap(String url) {
		Bitmap image = null;
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			
			if(conn.getContentLength() > 0) {
				InputStream is = conn.getInputStream();
				image = BitmapFactory.decodeStream(is);
				is.close();
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
}
