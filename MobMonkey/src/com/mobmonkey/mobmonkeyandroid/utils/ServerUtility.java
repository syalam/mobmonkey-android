
package com.mobmonkey.mobmonkeyandroid.utils;

import com.google.android.gcm.GCMRegistrar;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.conn.ConnectTimeoutException;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtility {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    
    public static String TAG = "Server Utility";
    //private String SERVER_URL = GCMIntentService.SERVER_URL;

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    public static boolean register(final Context context, final String regId) {
        Log.d(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = GCMIntentService.SERVER_URL + regId;
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                
                // display messages
                
                return true;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        
        // display error messages
        
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context, final String regId) {
        Log.d(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = GCMIntentService.SERVER_URL + "/unregister" + regId;
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            //String message = context.getString(R.string.server_unregistered);
            //CommonUtilities.displayMessage(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            //String message = context.getString(R.string.server_unregister_error,
             //       e.getMessage());
            //CommonUtilities.displayMessage(context, message);
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        Log.d(TAG, TAG + "url: " + url.toString());
//        StringBuilder bodyBuilder = new StringBuilder();
//        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
//        while (iterator.hasNext()) {
//            Entry<String, String> param = iterator.next();
//            bodyBuilder.append(param.getKey()).append('=')
//                    .append(param.getValue());
//            if (iterator.hasNext()) {
//                bodyBuilder.append('&');
//            }
//        }
//        String body = bodyBuilder.toString();
//        Log.v(TAG, "Posting '" + body + "' to " + url);
//        byte[] bytes = body.getBytes();
//        HttpURLConnection conn = null;
        
		try {
//			URL imageUrl = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(MMSDKConstants.TIMEOUT_CONNECTION);
			conn.setReadTimeout(MMSDKConstants.TIMEOUT_SOCKET);
			conn.setDoInput(true);
			Log.d(TAG, TAG + "before connect");
			conn.connect();
			Log.d(TAG, TAG + "after connect");
			Log.d(TAG, TAG + "status: " + conn.getResponseCode());
			String response = "";
			if(conn.getContentLength() > 0) {
				
				InputStream is = conn.getInputStream();
//				image = BitmapFactory.decodeStream(is);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				String line = MMSDKConstants.DEFAULT_STRING_EMPTY;
				while((line = bufferedReader.readLine()) != null) {
					response += line + MMSDKConstants.DEFAULT_STRING_NEWLINE;
				}
				is.close();
			}
			
			Log.d(TAG, TAG + "response: " + response);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
//			return MMSDKConstants.CONNECTION_TIMED_OUT;
		} catch (SocketException e) {
			e.printStackTrace();
			if(e.getMessage().equals(MMSDKConstants.OPERATION_TIMED_OUT)) {
//				return MMSDKConstants.CONNECTION_TIMED_OUT;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//            conn.setFixedLengthStreamingMode(bytes.length);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            // post the request
//            OutputStream out = conn.getOutputStream();
//            out.write(bytes);
//            out.close();
//            // handle the response
//            int status = conn.getResponseCode();
//            if (status != 200) {
//              throw new IOException("Post failed with error code " + status);
//            }
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
      }
}