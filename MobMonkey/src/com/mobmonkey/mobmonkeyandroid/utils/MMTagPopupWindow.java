package com.mobmonkey.mobmonkeyandroid.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.utils.MMPopupWindow;

/**
 * @author Dezapp, LLC
 *
 */
public class MMTagPopupWindow extends MMPopupWindow implements OnDismissListener {
	private LayoutInflater layoutInflater;
	private ImageView ivPopupArrowUp;
	private EditText etCityState;
	private EditText etZipCode;
	private OnDismissListener dismissListener;
	
	public MMTagPopupWindow(Context context) {
		super(context);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setRootView();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeysdk.utils.MMPopupWindow#onDismiss()
	 */
	@Override
	public void onDismiss() {
		if(dismissListener != null) {
			dismissListener.onDismiss();
		}
	}

	/**
	 * 
	 */
	private void setRootView() {
		mRootView = (ViewGroup) layoutInflater.inflate(R.layout.popup_tags, null);
		ivPopupArrowUp = (ImageView) mRootView.findViewById(R.id.ivpopuparrowup);
		etCityState = (EditText) mRootView.findViewById(R.id.etcitystate);
		etZipCode = (EditText) mRootView.findViewById(R.id.etzipcode);
		
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setContentView(mRootView);
	}
	
	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 */
	public void show (View anchor, int yOffset) {
		preShow();
		
		int[] location = new int[2];
	
		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());
		
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		showArrow(anchorRect.centerX() - anchorRect.left);
		
		mWindow.setAnimationStyle(R.style.AnimationPopupLeft);
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom - yOffset);
	}
	
	/**
	 * Show arrow
	 * 
	 * @param requestedX distance from left screen
	 */
	private void showArrow(int requestedX) {
		
        final int arrowWidth = ivPopupArrowUp.getMeasuredWidth();
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) ivPopupArrowUp.getLayoutParams();
       
        param.leftMargin = requestedX - arrowWidth / 2;
    }
	
	/**
	 * Set listener for window dismissed. This listener will only be fired if the quick action dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 */
	public void setOnDismissListener(OnDismissListener listener) {
		setOnDismissListener(this);
		dismissListener = listener;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCityState() {
		return etCityState.getText().toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getZipCode() {
		return etZipCode.getText().toString();
	}
	
	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
}
