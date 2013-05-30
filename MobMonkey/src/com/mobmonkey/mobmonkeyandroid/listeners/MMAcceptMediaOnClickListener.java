package com.mobmonkey.mobmonkeyandroid.listeners;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * 
 * @author Dezapp, LLC
 *
 */
public class MMAcceptMediaOnClickListener implements OnClickListener{
	private static final String TAG = "MMAcceptMediaOnClickListener";
	
	private Context context;
	private String requestId;
	private String mediaId;
	private MMCallback mmCallback;
	
	/**
	 * 
	 * @param context
	 * @param mmCallback
	 * @param requestId
	 * @param mediaId
	 */
	public MMAcceptMediaOnClickListener(Context context,
										MMCallback mmCallback,
										String requestId,
										String mediaId) {
		this.context = context;
		this.requestId = requestId;
		this.mediaId = mediaId;
		this.mmCallback = mmCallback;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, mediaId);
		MMMediaAdapter.acceptMedia(mmCallback,
								   requestId,
								   mediaId);
		MMProgressDialog.displayDialog(context,
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   context.getString(R.string.pd_accepting_answered_request));
	}
}
