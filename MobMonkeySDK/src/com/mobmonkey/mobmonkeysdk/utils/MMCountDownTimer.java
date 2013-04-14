package com.mobmonkey.mobmonkeysdk.utils;

import android.os.CountDownTimer;

/**
 * @author Dezapp, LLC
 *
 */
public class MMCountDownTimer extends CountDownTimer {
	public MMCountDownTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {
		MMDialog.dismissDialog();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		
	}
}
