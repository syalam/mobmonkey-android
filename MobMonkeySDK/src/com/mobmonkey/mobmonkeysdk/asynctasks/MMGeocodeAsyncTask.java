package com.mobmonkey.mobmonkeysdk.asynctasks;

import java.io.IOException;
import java.net.SocketException;

import java.util.List;
import java.util.Locale;

import org.apache.http.conn.ConnectTimeoutException;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Dezapp, LLC
 *
 */
public class MMGeocodeAsyncTask extends AsyncTask<Object, Void, Object> {
	private static final String TAG = "MMGeocodeAsyncTask: ";
	
	private Context context;
	private MMCallback mmCallback;
	
	public MMGeocodeAsyncTask(Context context, MMCallback mmCallback) {
		this.context = context;
		this.mmCallback = mmCallback;
	}
	
	@Override
	protected Object doInBackground(Object... params) {
        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        Log.d(TAG, TAG + "geocoder present: " + Geocoder.isPresent());
        if(Geocoder.isPresent()) {
			try {
				if(params[0] instanceof String) {
					addresses = gc.getFromLocationName((String) params[0], 1);
				} else if(params[0] instanceof Double) {
					addresses = gc.getFromLocation((Double) params[0], (Double) params[1], 1);
				}
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				return MMSDKConstants.CONNECTION_TIMED_OUT;
			} catch (SocketException e) {
				e.printStackTrace();
				if(e.getMessage().equals(MMSDKConstants.OPERATION_TIMED_OUT)) {
					return MMSDKConstants.CONNECTION_TIMED_OUT;
				}
			} catch (IOException e) {
				e.printStackTrace();
				if(e.getMessage().equals(MMSDKConstants.SERVICE_NOT_AVAILABLE)) {
					return MMSDKConstants.SERVICE_NOT_AVAILABLE;
				}
			}
	
	        if (addresses != null && addresses.size() == 1) {
	            return addresses.get(0);
	        } else {
	            return null;
	        }
        } else {        	
        	return MMSDKConstants.NO_GEOCODER_PRESENT;
        }
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		mmCallback.processCallback(result);
	}
}
