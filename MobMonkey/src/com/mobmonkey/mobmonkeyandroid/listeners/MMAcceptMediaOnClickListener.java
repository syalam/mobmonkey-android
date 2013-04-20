package com.mobmonkey.mobmonkeyandroid.listeners;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

public class MMAcceptMediaOnClickListener implements OnClickListener{

	private static final String TAG = "MMAcceptMediaOnClickListener";
	private String requestId, mediaId, partnerId, emailAddress, password;
	private MMCallback mmCallback;
	
	public MMAcceptMediaOnClickListener(MMCallback mmCallback, 
										String requestId, 
										String mediaId,
										String partnerId,
										String emailAddress,
										String password) {
		this.requestId = requestId;
		this.mediaId = mediaId;
		this.mmCallback = mmCallback;
		this.partnerId = partnerId;
		this.emailAddress = emailAddress;
		this.password = password;
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, mediaId);
		MMMediaAdapter.acceptMedia(mmCallback, 
								   partnerId, 
								   emailAddress, 
								   password, 
								   requestId, 
								   mediaId);
	}
	
}
