package com.mobmonkey.mobmonkeysdk.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMDialog extends Dialog {
	private static MMDialog mmDialog;
	
	public MMDialog(Context context) {
		super(context);
	}

	public static void displayCustomDialog(Context context, View customProgressDialog) {
		mmDialog = new MMDialog(context);
		mmDialog.setCancelable(false);
		
		new MMCountDownTimer(2400, 100).start();
		
		mmDialog.show();
		mmDialog.setContentView(customProgressDialog);
	}
	
	public static void dismissDialog() {
		if(!isProgressDialogNull()) {
			mmDialog.dismiss();
		}
	}
	
	public static boolean isProgressDialogNull() {
		return mmDialog == null;
	}
}
