package com.mobmonkey.mobmonkeyandroid.listeners;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsFragment;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

/**
 * @author Dezapp, LLC
 *
 */
public class MMShareMediaOnClickListener implements OnClickListener {
	private Context context;
	private Dialog shareMediaActionSheet;
	private Button btnSaveToCameraRoll;
	private Button btnFlagForReview;
	private Button btnCancel;
	
	public MMShareMediaOnClickListener(Context context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		shareMediaActionSheet = new Dialog(context);
		shareMediaActionSheet.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		shareMediaActionSheet.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		shareMediaActionSheet.getWindow().getAttributes().windowAnimations = R.style.ActionSheetAnimation;
		shareMediaActionSheet.getWindow().getAttributes().gravity = Gravity.BOTTOM;
		shareMediaActionSheet.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
		shareMediaActionSheet.getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
		shareMediaActionSheet.setCancelable(false);
		shareMediaActionSheet.setCanceledOnTouchOutside(false);
		shareMediaActionSheet.setContentView(R.layout.share_media_action_sheet);
		
		btnSaveToCameraRoll = (Button) shareMediaActionSheet.findViewById(R.id.btnsavetocameraroll);
		btnFlagForReview = (Button) shareMediaActionSheet.findViewById(R.id.btnflagforreview);
		btnCancel = (Button) shareMediaActionSheet.findViewById(R.id.btncancel);
		
//		btnSaveToCameraRoll.setOnClickListener(LocationDetailsFragment.this);
//		btnFlagForReview.setOnClickListener(LocationDetailsFragment.this);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(shareMediaActionSheet != null) {
					shareMediaActionSheet.dismiss();
				}
			}
		});
		
		shareMediaActionSheet.show();
	}
}
