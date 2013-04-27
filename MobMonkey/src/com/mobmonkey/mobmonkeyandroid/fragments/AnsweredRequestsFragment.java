package com.mobmonkey.mobmonkeyandroid.fragments;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.MMAcceptMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMImageOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMLocationNameOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMRejectMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMShareMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMVideoPlayOnClickListener;
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
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * Android {@link Fragment} to display Answered Requests.
 * @author Dezapp, LLC
 *
 */

public class AnsweredRequestsFragment extends MMFragment {
	private static final String TAG = "AnsweredRequestsFragment";
	private ListView lvAnsweredRequests;
//	private JSONArray answeredRequests;
	MMMediaItem[] answeredRequestItems;
	private SharedPreferences userPrefs;
	Location location;
	private MMAnsweredRequestArrayAdapter adapter;
	private OnLocationNameClickFragmentListener locationNameClickListener;
//	private Bitmap[] bms;
	
	// TODO: After user reject request, after app retrieve updated requests, screen is not getting refreshed, i.e. the answered requests aren't getting updated.
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_answeredrequests_screen, container, false);
		lvAnsweredRequests = (ListView) view.findViewById(R.id.lvansweredrequests);
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
	
	private void getAnsweredRequestItems(String result) throws JSONException, NumberFormatException, ParseException {
		JSONArray answeredRequests = new JSONArray(result);
		answeredRequestItems = new MMMediaItem[answeredRequests.length()];
		
		for(int i = 0; i < answeredRequests.length(); i++) {
			JSONObject answeredRequest = answeredRequests.getJSONObject(i);
			MMMediaItem answeredRequestItem = new MMMediaItem();
			
			if(answeredRequest.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA).length() > 0) {
				JSONObject media = answeredRequest.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA).getJSONObject(MMSDKConstants.DEFAULT_INT_ZERO);
				
				answeredRequestItem.setLocationName(answeredRequest.getString(MMSDKConstants.JSON_KEY_NAME_OF_LOCATION));
				answeredRequestItem.setExpiryDate(MMUtility.getExpiryDate(System.currentTimeMillis() - answeredRequest.getLong("fulfilledDate")));
//				item.setExpiryDate(String.valueOf(media.getLong(MMSDKConstants.JSON_KEY_EXPIRY_DATE) - System.currentTimeMillis()));
//				item.setImageMedia(bms[i]);
				int mediaType = answeredRequest.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE);
				Log.d(TAG, TAG + "mediaType: " + mediaType);
				if(mediaType == MMSDKConstants.MEDIA_TYPE_IMAGE) {
					MMImageLoaderAdapter.loadImage(new LoadImageCallback(i), media.getString(MMSDKConstants.JSON_KEY_MEDIA_URL));
					answeredRequestItem.setIsImage(true);
//					item.setImageOnClickListener(new MMImageOnClickListener(getActivity(), item.getImageMedia()));
				} else if(mediaType == MMSDKConstants.MEDIA_TYPE_VIDEO) {
					answeredRequestItem.setIsVideo(true);
					answeredRequestItem.setPlayOnClickListener(new MMVideoPlayOnClickListener(getActivity(), media.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
				}
				
				answeredRequestItem.setAccepted(media.getBoolean(MMSDKConstants.JSON_KEY_ACCEPTED));
				
				
				// locationDetails
//				JSONObject locationDetails = new JSONObject();
//				locationDetails.put(MMSDKConstants.JSON_KEY_LOCATION_ID, answeredRequest.getString(MMSDKConstants.JSON_KEY_LOCATION_ID));
//				locationDetails.put(MMSDKConstants.JSON_KEY_PROVIDER_ID, answeredRequest.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID));
				answeredRequestItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(getActivity()));
				answeredRequestItem.setLocationNameOnClickListener(new MMLocationNameOnClickListener(locationNameClickListener, answeredRequest));
				answeredRequestItem.setAcceptMediaOnClickListener(new MMAcceptMediaOnClickListener(new MMAcceptedRequestCallback(), 
																					media.getString(MMSDKConstants.JSON_KEY_REQUEST_ID), 
																					media.getString(MMSDKConstants.JSON_KEY_MEDIA_ID), 
																					MMConstants.PARTNER_ID, 
																					userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
																					userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY), 
																					getActivity()));
				answeredRequestItem.setRejectMediaOnClickListener(new MMRejectMediaOnClickListener(new MMRejectRequestCallback(i), 
																					media.getString(MMSDKConstants.JSON_KEY_REQUEST_ID), 
																					media.getString(MMSDKConstants.JSON_KEY_MEDIA_ID), 
																					MMConstants.PARTNER_ID, 
																					userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
																					userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY),
																					getActivity()));
				
			}
			
			answeredRequestItems[i] = answeredRequestItem;
		}
		
//		tempItem = answeredRequestItems;
		
		adapter = new MMAnsweredRequestArrayAdapter(getActivity(), R.layout.answeredrequests_listview_row, answeredRequestItems);
		lvAnsweredRequests.setAdapter(adapter);
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class AnsweredRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, "AnsweredRequestCallback: "+(String) obj);
			try {
				getAnsweredRequestItems((String) obj);
//				answeredRequests = new JSONArray((String) obj);
//				JSONArray jArr = answeredRequests;
//				bms = new Bitmap[jArr.length()];
//				for(int i = 0; i < jArr.length(); i++) {
//					JSONObject jObj = jArr.getJSONObject(i);
					// if media array contains some media
//					if(jObj.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA).length() > 0) {
//						JSONObject media = jObj.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA).getJSONObject(0);
//						int mediaType = jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE);
//						switch(mediaType) {
//						case 0:
//							break;
//						case 1:
//							String imageUrl = media.getString(MMSDKConstants.JSON_KEY_MEDIA_URL);
//							MMImageLoaderAdapter.loadImage(new ImageCallback(i), imageUrl);
//							break;
//						case 2:
//							break;
//						}
//						
//						 is image
//						if(jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE) == 1) {
//
//						} 
//						 is video
//						else if(jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE) == 2){
//							 temp image
//							bms[i] = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
//							arrayAdapter = new MMAnsweredRequestArrayAdapter(getActivity(), R.layout.answeredrequests_listview_row, getAnsweredRequestItems());
//							lvAnsweredRequests.setAdapter(arrayAdapter);
//						}
//					}
//					
//				}
				
			} catch (JSONException ex) {
				Log.e(TAG, "JSONException occured!");
				ex.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class LoadImageCallback implements MMCallback {
		private int position;
		
		public LoadImageCallback(int position) {
			this.position = position;
		}
		
		@Override
		public void processCallback(Object obj) {
//			Bitmap bm = (Bitmap) obj;
			answeredRequestItems[position].setImageMedia((Bitmap) obj);
			answeredRequestItems[position].setImageOnClickListener(new MMImageOnClickListener(getActivity(), (Bitmap) obj));
//			bms[position] = bm;
			adapter.notifyDataSetChanged();
		}
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
				MMProgressDialog.dismissDialog();
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
		
		private int position;
		
		public MMRejectRequestCallback(int position) {
			this.position = position;
		}
		
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
				MMProgressDialog.dismissDialog();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
//	private boolean bmsHasNull() {
//		boolean flag = false;
//		
//		FindNull:
//		for(int i = 0; i < bms.length; i++) {
//			if(bms[i] == null) {
//				flag = true;
//				break FindNull;
//			}
//		}
//		
//		return flag;
//	}
}
