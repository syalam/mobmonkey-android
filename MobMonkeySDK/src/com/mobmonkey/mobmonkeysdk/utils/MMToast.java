package com.mobmonkey.mobmonkeysdk.utils;

import com.mobmonkey.mobmonkeysdk.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class MMToast extends Toast {
	
	public MMToast(Context context) {
		super(context);
	}

	public static MMToast makeToastWithImage(Context context, Drawable image, String text) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View toastView = inflater.inflate(R.layout.mmtoast, null);
		ImageView ivToastImage = (ImageView)toastView.findViewById(R.id.ivtoastimage);
		ivToastImage.setImageDrawable(image);
		
		TextView ivToastText = (TextView)toastView.findViewById(R.id.tvtoasttext);
		ivToastText.setText(text);
		
		MMToast mmToast = new MMToast(context);
		mmToast.setGravity(Gravity.BOTTOM, 0, 120);
		mmToast.setDuration(Toast.LENGTH_LONG);
		mmToast.setView(toastView);
		
		return mmToast;
	}
}