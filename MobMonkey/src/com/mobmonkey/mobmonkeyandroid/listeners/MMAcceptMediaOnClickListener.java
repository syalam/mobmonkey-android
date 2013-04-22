package com.mobmonkey.mobmonkeyandroid.listeners;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

public class MMAcceptMediaOnClickListener implements OnClickListener{

	private static final String TAG = "MMAcceptMediaOnClickListener";
	private String requestId, mediaId, partnerId, emailAddress, password;
	private MMCallback mmCallback;
	private Context context;
	
	public MMAcceptMediaOnClickListener(MMCallback mmCallback, 
										String requestId, 
										String mediaId,
										String partnerId,
										String emailAddress,
										String password,
										Context context) {
		this.requestId = requestId;
		this.mediaId = mediaId;
		this.mmCallback = mmCallback;
		this.partnerId = partnerId;
		this.emailAddress = emailAddress;
		this.password = password;
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, mediaId);
		MMProgressDialog.displayDialog(context, null, "Accepting request...");
		MMMediaAdapter.acceptMedia(mmCallback, 
								   partnerId, 
								   emailAddress, 
								   password, 
								   requestId, 
								   mediaId);
	}
	
}
