package com.mobmonkey.mobmonkeyandroid.fragments;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.MMAcceptMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMImageOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMLocationNameOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMRejectMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.OnLocationNameClickFragmentListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMAnsweredRequestArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMAnsweredRequestItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMMediaItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMInboxAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * Android {@link Fragment} to display Answered Requests.
 * @author Dezapp, LLC
 *
 */

public class AnsweredRequestsFragment extends MMFragment {
	private static final String TAG = "AnsweredRequestsFragment";
	private ListView lvAnsweredRequests;
	private JSONArray answeredRequests;
	private SharedPreferences userPrefs;
	Location location;
	private MMAnsweredRequestArrayAdapter arrayAdapter;
	private OnLocationNameClickFragmentListener locationNameClickListener;
	private Bitmap[] bms;
	
	// TODO: After user reject request, after app retrieve updated requests, screen is not getting refreshed, i.e. the answered requests aren't getting updated.
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_answeredrequests_screen, container, false);
		lvAnsweredRequests = (ListView) view.findViewById(R.id.lvAnsweredrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		// get all the assigned request, and then update the badge counter
		MMInboxAdapter.getAnsweredRequests(new AnsweredRequestCallback(), 
										   MMConstants.PARTNER_ID, 
										   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
										   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		return view;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnLocationNameClickFragmentListener) {
			locationNameClickListener = (OnLocationNameClickFragmentListener) activity;
		}
	}
	
	@Override
	public void onFragmentBackPressed() {

	}

	/**
	 * function that generate an array of {@link MMAnsweredRequestItem} and returns it.
	 * @return {@link MMAnsweredRequestItem[]}
	 * @throws JSONException
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	
	private MMMediaItem[] getAnsweredRequestItems() throws JSONException, NumberFormatException, ParseException {
		MMMediaItem[] answeredRequestItems = new MMMediaItem[answeredRequests.length()];

		for(int i = 0; i < answeredRequests.length(); i++) {
			JSONObject jObj = answeredRequests.getJSONObject(i);
			
			MMMediaItem item = new MMMediaItem();
			
			// media file can be null
			if(jObj.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA).length() > 0) {
				JSONObject media = jObj.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA).getJSONObject(0);
				
				item.setLocationName(jObj.getString(MMSDKConstants.JSON_KEY_NAME_OF_LOCATION));
				item.setImageMedia(bms[i]);
				if(jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE) == 1) {
					item.setIsImage(true);
				} else {
					item.setIsVideo(true);
				}
				
				item.setAccepted(media.getBoolean(MMSDKConstants.JSON_KEY_ACCEPTED));
				item.setExpiryDate(MMUtility.getDate(System.currentTimeMillis() - media.getLong(MMSDKConstants.JSON_KEY_EXPIRY_DATE), "m"));
				
				item.setAcceptMediaOnClickListener(new MMAcceptMediaOnClickListener(new MMAcceptedRequestCallback(), 
																					jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_ID), 
																					media.getString(MMSDKConstants.JSON_KEY_MEDIA_ID), 
																					MMConstants.PARTNER_ID, 
																					userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
																					userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY)));
				
				item.setRejectMediaOnClickListener(new MMRejectMediaOnClickListener(new MMRejectRequestCallback(), 
																					jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_ID), 
																					media.getString(MMSDKConstants.JSON_KEY_MEDIA_ID), 
																					MMConstants.PARTNER_ID, 
																					userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
																					userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY)));
				
				item.setLocationNameOnClickListener(new MMLocationNameOnClickListener(locationNameClickListener, jObj));
				item.setImageOnClickListener(new MMImageOnClickListener(getActivity(), item.getImageMedia()));
			}
			// if no data for media, ignore it and prints out rest of the data
			else {
				item.setLocationName(jObj.getString(MMSDKConstants.JSON_KEY_NAME_OF_LOCATION));
				item.setImageMedia(bms[i]);
				if(jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE) == 1) {
					item.setIsImage(true);
				}
				else {
					item.setIsVideo(true);
				}
			}
			answeredRequestItems[i] = item;
		}
		
		return answeredRequestItems;
	}
	
	/**
	 * The {@link OnItemClickListener} for {@link ListView} in AnsweredRequestsFragment.
	 *
	 */
	private class onAnsweredRequestsClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			
		}
		
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class AnsweredRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String) obj);
			if(obj != null) {
				try {
					answeredRequests = new JSONArray((String) obj);
					bms = new Bitmap[answeredRequests.length()];
					
					// get all media
					for(int i = 0; i < bms.length; i++) {
						JSONObject media = answeredRequests.getJSONObject(i).getJSONArray(MMSDKConstants.JSON_KEY_MEDIA).getJSONObject(0);
						String imageUrl = media.getString(MMSDKConstants.JSON_KEY_MEDIA_URL);
						MMImageLoaderAdapter.loadImage(new ImageCallback(i), imageUrl);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class ImageCallback implements MMCallback {
		private int position;
		
		public ImageCallback(int position) {
			this.position = position;
		}
		
		@Override
		public void processCallback(Object obj) {
			Bitmap bm = (Bitmap) obj;
			bms[position] = bm;
			
			try {
//				item.setImageOnClickListener(new MMImageOnClickListener(getActivity(), (Bitmap) obj));
				arrayAdapter = new MMAnsweredRequestArrayAdapter(getActivity(), R.layout.answeredrequests_listview_row, getAnsweredRequestItems());
				lvAnsweredRequests.setAdapter(arrayAdapter);
				lvAnsweredRequests.setOnItemClickListener(new onAnsweredRequestsClick());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
//		
//		private boolean ready() {
//			return callbackCounter == bms.length? true:false;
//		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class MMAcceptedRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String)obj);
			try {
				JSONObject jObj = new JSONObject((String) obj);
				MMInboxAdapter.getAnsweredRequests(new AnsweredRequestCallback(), 
						   MMConstants.PARTNER_ID, 
						   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
						   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				
				Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.KEY_RESPONSE_DESC), Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class MMRejectRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String)obj);
			try {
				JSONObject jObj = new JSONObject((String) obj);
				MMInboxAdapter.getAnsweredRequests(new AnsweredRequestCallback(), 
						   MMConstants.PARTNER_ID, 
						   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
						   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				
				Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.KEY_RESPONSE_DESC), Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
