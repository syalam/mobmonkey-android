package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.File;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMAnsweredRequestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMAnsweredRequestItem;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMMediaItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMAcceptMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMImageOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMLocationNameOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMRejectMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMShareMediaOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMVideoPlayOnClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.OnLocationNameClickFragmentListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMDownloadVideoAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMRequestAdapter;
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
	MMMediaItem[] answeredRequestItems;
	private SharedPreferences userPrefs;
	Location location;
	private MMAnsweredRequestsArrayAdapter adapter;
	private OnLocationNameClickFragmentListener locationNameClickListener;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		MMRequestAdapter.getAnsweredRequests(new AnsweredRequestCallback(), 
											 MMConstants.PARTNER_ID,
											 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
											 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		
		View view = inflater.inflate(R.layout.fragment_answeredrequests_screen, container, false);
		lvAnsweredRequests = (ListView) view.findViewById(R.id.lvansweredrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
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
	
	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}

	/**
	 * Function that generate an array of {@link MMAnsweredRequestItem}.
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
				answeredRequestItem.setExpiryDate(MMUtility.getExpiryDate(System.currentTimeMillis() - answeredRequest.getLong(MMSDKConstants.JSON_KEY_FULFILLED_DATE)));
				int mediaType = answeredRequest.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE);
				Log.d(TAG, TAG + "mediaType: " + mediaType);
				if(mediaType == MMSDKConstants.MEDIA_TYPE_IMAGE) {
					MMImageLoaderAdapter.loadImage(new LoadImageCallback(i),
												   media.getString(MMSDKConstants.JSON_KEY_MEDIA_URL));
					answeredRequestItem.setIsImage(true);
				} else if(mediaType == MMSDKConstants.MEDIA_TYPE_VIDEO) {
					MMDownloadVideoAdapter.downloadVideo(new CreateVideoThumbnailCallback(i),
														 media.getString(MMSDKConstants.JSON_KEY_MEDIA_URL), 
														 i);
					answeredRequestItem.setIsVideo(true);
					answeredRequestItem.setPlayOnClickListener(new MMVideoPlayOnClickListener(getActivity(), media.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
				}
				
				answeredRequestItem.setAccepted(media.getBoolean(MMSDKConstants.JSON_KEY_ACCEPTED));
				answeredRequestItem.setShareMediaOnClickListener(new MMShareMediaOnClickListener(getActivity()));
				answeredRequestItem.setLocationNameOnClickListener(new MMLocationNameOnClickListener(locationNameClickListener, answeredRequest));
				answeredRequestItem.setAcceptMediaOnClickListener(new MMAcceptMediaOnClickListener(getActivity(),
																								   new MMAcceptedRequestCallback(),
																								   media.getString(MMSDKConstants.JSON_KEY_REQUEST_ID),
																								   media.getString(MMSDKConstants.JSON_KEY_MEDIA_ID),
																								   MMConstants.PARTNER_ID,
																								   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
																								   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY)));
				answeredRequestItem.setRejectMediaOnClickListener(new MMRejectMediaOnClickListener(new MMRejectRequestCallback(), 
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
		
		adapter = new MMAnsweredRequestsArrayAdapter(getActivity(), R.layout.listview_row_answered_requests, answeredRequestItems);
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
			answeredRequestItems[position].setImageMedia((Bitmap) obj);
			answeredRequestItems[position].setImageOnClickListener(new MMImageOnClickListener(getActivity(), (Bitmap) obj));
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
				MMRequestAdapter.getAnsweredRequests(new AnsweredRequestCallback(), 
													 MMConstants.PARTNER_ID,
													 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
													 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				
				Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
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
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String)obj);
			try {
				
				JSONObject jObj = new JSONObject((String) obj);
				MMRequestAdapter.getAnsweredRequests(new AnsweredRequestCallback(),
												   MMConstants.PARTNER_ID,
												   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
												   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				
				Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
				MMProgressDialog.dismissDialog();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Callback to create a thumbnail from a recently downloaded video
	 * @author Dezapp, LLC
	 * 
	 */
	private class CreateVideoThumbnailCallback implements MMCallback {
		int position;
		
		public CreateVideoThumbnailCallback(int position) {
			this.position = position;
		}
		
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				Uri videoUri = (Uri) obj;	
				
				Log.d(TAG, "videoUri: " + videoUri.getPath());
				
				MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
		        mRetriever.setDataSource(videoUri.getPath());
//		        answeredRequestItems[position].setImageMedia(ThumbnailUtils.extractThumbnail(mRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC), mediaWidth, mediaHeight));
		        answeredRequestItems[position].setImageMedia(mRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
		        File videoFile = new File(videoUri.getPath());
		        videoFile.delete();
		        adapter.notifyDataSetChanged();
			}
			
		}
	}
}