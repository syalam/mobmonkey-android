package com.mobmonkey.mobmonkeyandroid.listeners;

import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MMRejectMediaOnClickListener implements OnClickListener{

	private String requestId, mediaId, partnerId, emailAddress, password;
	private MMCallback mmCallback;
	private ImageButton ibtn;
	
	public MMRejectMediaOnClickListener(MMCallback mmCallback, 
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
		MMMediaAdapter.rejectMedia(mmCallback, 
								   partnerId, 
								   emailAddress, 
								   password, 
								   requestId, 
								   mediaId);
	}

	public ImageButton getIbtn() {
		return ibtn;
	}

	public void setIbtn(ImageButton ibtn) {
		this.ibtn = ibtn;
	}

	
}
