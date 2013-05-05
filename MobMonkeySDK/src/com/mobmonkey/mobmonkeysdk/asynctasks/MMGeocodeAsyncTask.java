package com.mobmonkey.mobmonkeysdk.asynctasks;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

import org.apache.http.conn.ConnectTimeoutException;

import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

/**
 * @author Dezapp, LLC
 *
 */
public class MMGeocodeAsyncTask extends AsyncTask<Object, Void, Address> {
	Context context;
	MMCallback mmCallback;
	
	public MMGeocodeAsyncTask(Context context, MMCallback mmCallback) {
		this.context = context;
		this.mmCallback = mmCallback;
	}
	
	@Override
	protected Address doInBackground(Object... params) {
        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
		try {
			if(params[0] instanceof String) {
				addresses = gc.getFromLocationName((String) params[0], 1);
			} else if(params[0] instanceof Double) {
				addresses = gc.getFromLocation((Double) params[0], (Double) params[1], 1);
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        if (addresses != null && addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
	}

	@Override
	protected void onPostExecute(Address result) {
		super.onPostExecute(result);
		mmCallback.processCallback(result);
	}
}
