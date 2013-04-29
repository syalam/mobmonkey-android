package com.mobmonkey.mobmonkeyandroid.listeners;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

public class MMRejectMediaOnClickListener implements OnClickListener{
	private String requestId, mediaId, partnerId, user, auth;
	private MMCallback mmCallback;
	private Context context;
	
	public MMRejectMediaOnClickListener(MMCallback mmCallback, 
										String requestId, 
										String mediaId,
										String partnerId,
										String user,
										String auth,
										Context context) {
		this.requestId = requestId;
		this.mediaId = mediaId;
		this.mmCallback = mmCallback;
		this.partnerId = partnerId;
		this.user = user;
		this.auth = auth;
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		MMMediaAdapter.rejectMedia(mmCallback,
								   requestId,
				   				   mediaId,
								   partnerId,
								   user,
								   auth);
		MMProgressDialog.displayDialog(context,
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   context.getString(R.string.pd_rejecting_answered_request));
	}
	
}
