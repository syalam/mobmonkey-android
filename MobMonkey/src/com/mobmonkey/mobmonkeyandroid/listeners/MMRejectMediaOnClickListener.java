package com.mobmonkey.mobmonkeyandroid.listeners;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

public class MMRejectMediaOnClickListener implements OnClickListener{

	private String requestId, mediaId, partnerId, emailAddress, password;
	private MMCallback mmCallback;
	private Context context;
	
	public MMRejectMediaOnClickListener(MMCallback mmCallback, 
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
		MMProgressDialog.displayDialog(context, null, "Rejecting request...");
		MMMediaAdapter.rejectMedia(mmCallback, 
								   partnerId, 
								   emailAddress, 
								   password, 
								   requestId, 
								   mediaId);
	}
	
}
