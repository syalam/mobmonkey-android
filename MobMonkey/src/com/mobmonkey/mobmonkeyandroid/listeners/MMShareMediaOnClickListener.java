package com.mobmonkey.mobmonkeyandroid.listeners;

import com.mobmonkey.mobmonkeyandroid.ShareMediaActionSheet;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Dezapp, LLC
 *
 */
public class MMShareMediaOnClickListener implements OnClickListener {
	private Context context;
	
	public MMShareMediaOnClickListener(Context context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		Intent shareMediaActionSheetIntent = new Intent(context, ShareMediaActionSheet.class);
		context.startActivity(shareMediaActionSheetIntent);
	}
}
