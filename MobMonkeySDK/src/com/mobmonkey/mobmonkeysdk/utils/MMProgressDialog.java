package com.mobmonkey.mobmonkeysdk.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.View;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMProgressDialog {
	private static ProgressDialog progressDialog;
	
	public static void displayDialog(Context context, String title, String message) {
		progressDialog = ProgressDialog.show(context, title, message, true, false);
	}
	
	public static void dismissDialog() {
		if(!isProgressDialogNull()) {
			progressDialog.dismiss();
		}
	}
	
	public static boolean isProgressDialogNull() {
		return progressDialog == null;
	}
	
	public static boolean isProgressDialogShowing() {
		if(!isProgressDialogNull()) {
			return progressDialog.isShowing();
		} else {
			return false;
		}
	}
}
