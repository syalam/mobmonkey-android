package com.mobmonkey.mobmonkey.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkey.MMVideoPlayerScreen;
import com.mobmonkey.mobmonkey.MakeARequestScreen;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMExpandedListView;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMProgressDialog;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeyapi.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeyapi.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Dezapp, LLC
 *
 */
public class LocationDetailsFragment extends MMFragment implements OnClickListener, OnItemClickListener {
	private static final String TAG = "LocationDetailsFragment: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	private JSONArray favoritesList;
	private JSONObject locationDetails;
	
	private TextView tvLocNameTitle;
	private TextView tvLocName;
	private LinearLayout llMakeRequest;
	private TextView tvMembersFound;
	private MMExpandedListView elvLocDetails;
	private LinearLayout llFavorite;
	private TextView tvFavorite;
	
	private LinearLayout llMedia;
	private ImageView ivStream;
	private ImageView ivVideo;
	private ImageView ivImage;
	private ImageView ivStreamPlay;
	private ImageView ivVideoPlay;
	private TextView tvStreamExpiryDate;
	private TextView tvVideoExpiryDate;
	private TextView tvImageExpiryDate;
	private ImageButton ibShareMedia;
	private ImageButton ibStream;
	private ImageButton ibVideo;
	private ImageButton ibImage;
	private TextView tvStreamMediaCount;
	private TextView tvVideoMediaCount;
	private TextView tvImageMediaCount;
	
	private OnLocationDetailsItemClickListener listener;
	
	private String mediaResults;
	private boolean retrieveMedia = false;
	private boolean hasStreamExpiryDate = false;
	private boolean hasVideoExpiryDate = false;
	private boolean hasImageExpiryDate = false;
	private View mediaButtonSelected;
	private String mediaStreamUrl;
	private String mediaVideoUrl;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_locationdetails_screen, container, false);
		
		tvLocNameTitle = (TextView) view.findViewById(R.id.tvlocnametitle);
		tvLocName = (TextView) view.findViewById(R.id.tvlocname);
		llMakeRequest = (LinearLayout) view.findViewById(R.id.llmakerequest);
		tvMembersFound = (TextView) view.findViewById(R.id.tvmembersfound);
		elvLocDetails = (MMExpandedListView) view.findViewById(R.id.elvlocdetails);
		llFavorite = (LinearLayout) view.findViewById(R.id.llfavorite);
		tvFavorite = (TextView) view.findViewById(R.id.tvfavorite);
		
		llMedia = (LinearLayout) view.findViewById(R.id.llmedia);
		ivStream = (ImageView) view.findViewById(R.id.ivstream);
		ivVideo = (ImageView) view.findViewById(R.id.ivvideo);
		ivImage = (ImageView) view.findViewById(R.id.ivimage);
		ivStreamPlay = (ImageView) view.findViewById(R.id.ivstreamplay);
		ivVideoPlay = (ImageView) view.findViewById(R.id.ivvideoplay);
		tvStreamExpiryDate = (TextView) view.findViewById(R.id.tvstreamexpirydate);
		tvVideoExpiryDate = (TextView) view.findViewById(R.id.tvvideoexpirydate);
		tvImageExpiryDate = (TextView) view.findViewById(R.id.tvimageexpirydate);
		ibShareMedia = (ImageButton) view.findViewById(R.id.ibsharemedia);
		ibStream = (ImageButton) view.findViewById(R.id.ibstream);
		ibVideo = (ImageButton) view.findViewById(R.id.ibvideo);
		ibImage = (ImageButton) view.findViewById(R.id.ibimage);
		tvStreamMediaCount = (TextView) view.findViewById(R.id.tvstreammediacount);
		tvVideoMediaCount = (TextView) view.findViewById(R.id.tvvideomediacount);
		tvImageMediaCount = (TextView) view.findViewById(R.id.tvimagemediacount);
		
		try {
			if(!userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING).equals(MMAPIConstants.DEFAULT_STRING)) {
				favoritesList = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, MMAPIConstants.DEFAULT_STRING));
			} else {
				favoritesList = new JSONArray();
			}
			locationDetails = new JSONObject(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			
			if(!retrieveMedia) {
				MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_retrieving_media));
				MMMediaAdapter.retrieveAllMediaForLocation(new MediaCallback(), 
						userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
						userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), 
						MMConstants.PARTNER_ID, 
						locationDetails.getString(MMAPIConstants.JSON_KEY_LOCATION_ID), 
						locationDetails.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID));
				setLocationDetails();
			} else {
				setLocationDetails();
				hasMedia();
				if(mediaButtonSelected != null) {
					onClick(mediaButtonSelected);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			llFavorite.setClickable(false);
		}
				
		return view;		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnLocationDetailsItemClickListener) {
			listener = (OnLocationDetailsItemClickListener) activity;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		Intent intent;
		switch(view.getId()) {
			case R.id.llmakerequest:
				intent = new Intent(getActivity(), MakeARequestScreen.class);
				intent.putExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, locationDetails.toString());
				startActivity(intent);
				break;
			case R.id.llfavorite:
				favoriteClicked();
				break;
			case R.id.ivstreamplay:
				intent = new Intent(getActivity(), MMVideoPlayerScreen.class);
				intent.putExtra(MMAPIConstants.JSON_KEY_MEDIA_URL, mediaStreamUrl);
				startActivity(intent);
				break;
			case R.id.ivvideoplay:
				intent = new Intent(getActivity(), MMVideoPlayerScreen.class);
				intent.putExtra(MMAPIConstants.JSON_KEY_MEDIA_URL, mediaVideoUrl);
				startActivity(intent);
				break;
			case R.id.ibsharemedia:
				break;
			case R.id.ibstream:
				streamMediaSelected();
				break;
			case R.id.ibvideo:
				videoMediaSelected();
				break;
			case R.id.ibimage:
				imageMediaSelected();
				break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		if(position == 0) {
			listener.onLocationDetailsItem(position, ((TextView)view.findViewById(R.id.tvlabel)).getText().toString());
		} else if(position == 1) {
			listener.onLocationDetailsItem(position, locationDetails.toString());
		} else if(position == 2) {
			listener.onLocationDetailsItem(position, MMAPIConstants.DEFAULT_STRING);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	/**
	 * Function that set all the details of the current location
	 * @throws JSONException
	 */
	private void setLocationDetails() throws JSONException {
		tvLocNameTitle.setText(locationDetails.getString(MMAPIConstants.JSON_KEY_NAME));
		tvLocName.setText(locationDetails.getString(MMAPIConstants.JSON_KEY_NAME));
		tvMembersFound.setText(locationDetails.getString(MMAPIConstants.JSON_KEY_MONKEYS) + MMAPIConstants.DEFAULT_SPACE + getString(R.string.tv_members_found));
		
		int[] icons = new int[]{R.drawable.cat_icon_telephone, R.drawable.cat_icon_map_pin, R.drawable.cat_icon_alarm_clock};
		int[] indicatorIcons = new int[]{R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator};
		String[] details = new String[3];
		String phoneNumber = locationDetails.getString(MMAPIConstants.JSON_KEY_PHONE_NUMBER);
		if(phoneNumber.equals(MMAPIConstants.DEFAULT_STRING_NULL) || phoneNumber.equals(MMAPIConstants.DEFAULT_STRING)) {
			details[0] = getString(R.string.tv_no_phone_number_available);
		} else {
			details[0] = phoneNumber;
		}
		details[1] = locationDetails.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_NEWLINE + locationDetails.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.COMMA_SPACE + 
				locationDetails.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.COMMA_SPACE + locationDetails.getString(MMAPIConstants.JSON_KEY_POSTCODE);
		details[2] = getString(R.string.tv_add_notifications);
		ArrayAdapter<Object> arrayAdapter = new MMArrayAdapter(getActivity(), R.layout.mm_listview_row, icons, details, indicatorIcons, android.R.style.TextAppearance_Small, Typeface.DEFAULT, null);
		elvLocDetails.setAdapter(arrayAdapter);
		
		llMakeRequest.setOnClickListener(LocationDetailsFragment.this);
		elvLocDetails.setOnItemClickListener(LocationDetailsFragment.this);
		llFavorite.setOnClickListener(LocationDetailsFragment.this);
		
		for(int i = 0; i < favoritesList.length(); i++) {
			if(favoritesList.getJSONObject(i).getString(MMAPIConstants.JSON_KEY_LOCATION_ID).equals(locationDetails.getString(MMAPIConstants.JSON_KEY_LOCATION_ID))) {
				tvFavorite.setText(getString(R.string.tv_remove_favorite));
				break;
			}
		}
	}
	
	/**
	 * Function that handles the processing of the result from retrieve all media call to the server
	 * @throws JSONException
	 */
	private void hasMedia() throws JSONException {		
		JSONObject mediaJObj = new JSONObject(mediaResults); 
		JSONArray mediaJArr = mediaJObj.getJSONArray(MMAPIConstants.JSON_KEY_MEDIA);
		
		int streamMediaCount = 0;
		int videoMediaCount = 0;
		int imageMediaCount = 0;
		
		if(mediaJArr.length() > 0) {
			llMedia.setVisibility(View.VISIBLE);
			
			boolean hasFirstStreamMedia = false;
			boolean hasFirstVideoMedia = false;
			boolean hasFirstImageMedia = false;
			
			for(int i = 0; i < mediaJArr.length(); i++) {
				JSONObject jObj = mediaJArr.getJSONObject(i);
				
				String mediaType = jObj.getString(MMAPIConstants.JSON_KEY_TYPE);
				
				if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_LIVESTREAMING)) {
					if(!hasFirstStreamMedia) {
						mediaStreamUrl = jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL);
						tvStreamExpiryDate.setText(MMUtility.getDate(System.currentTimeMillis() - jObj.getLong(MMAPIConstants.JSON_KEY_EXPIRY_DATE), "mm") + "m");
						hasStreamExpiryDate = true;
						hasFirstStreamMedia = true;
					}
					streamMediaCount++;
				} else if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_VIDEO)) {
					if(!hasFirstVideoMedia) {
						mediaVideoUrl = jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL);
						tvVideoExpiryDate.setText(MMUtility.getDate(System.currentTimeMillis() - jObj.getLong(MMAPIConstants.JSON_KEY_EXPIRY_DATE), "mm") + "m");
						hasVideoExpiryDate = true;
						hasFirstVideoMedia = true;
					}
					videoMediaCount++;
				} else if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_IMAGE)) {
					if(!hasFirstImageMedia) {
						MMImageLoaderAdapter.loadImage(new LoadImageCallback(), jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
						tvImageExpiryDate.setText(MMUtility.getDate(System.currentTimeMillis() - jObj.getLong(MMAPIConstants.JSON_KEY_EXPIRY_DATE), "mm") + "m");
						hasImageExpiryDate = true;
						hasFirstImageMedia = true;
					}
					imageMediaCount++;
				}
			}
			
			if(streamMediaCount > 0) {
				ivStreamPlay.setClickable(true);
				tvStreamMediaCount.setText(streamMediaCount + MMAPIConstants.DEFAULT_STRING);
			}
			if(videoMediaCount > 0) {
				ivVideoPlay.setClickable(true);
				tvVideoMediaCount.setText(videoMediaCount + MMAPIConstants.DEFAULT_STRING);
			}
			if(imageMediaCount > 0) {
				tvImageMediaCount.setText(imageMediaCount + MMAPIConstants.DEFAULT_STRING);
			}
			
			ivStreamPlay.setOnClickListener(LocationDetailsFragment.this);
			ivVideoPlay.setOnClickListener(LocationDetailsFragment.this);
			ibShareMedia.setOnClickListener(LocationDetailsFragment.this);
			ibStream.setOnClickListener(LocationDetailsFragment.this);
			ibVideo.setOnClickListener(LocationDetailsFragment.this);
			ibImage.setOnClickListener(LocationDetailsFragment.this);
		}
//		else {
//			llMedia.setVisibility(View.VISIBLE);
//			hasImageExpiryDate = true;
//			tvImageExpiryDate.setText("30m");
//			MMImageLoaderAdapter.loadImage(new LoadImageCallback(), "http://i.imgur.com/T0Va07Y.jpg");
//			
//			ivVideoPlay.setClickable(true);
//			
//			ivStreamPlay.setOnClickListener(LocationDetailsFragment.this);
//			ivVideoPlay.setOnClickListener(LocationDetailsFragment.this);
//			ibShareMedia.setOnClickListener(LocationDetailsFragment.this);
//			ibStream.setOnClickListener(LocationDetailsFragment.this);
//			ibVideo.setOnClickListener(LocationDetailsFragment.this);
//			ibImage.setOnClickListener(LocationDetailsFragment.this);
//		}
	}
	
	/**
	 * Function to handle the event of when the user clicked on the stream media button
	 */
	private void streamMediaSelected() {
		mediaButtonSelected = ibStream;
		ibStream.setSelected(true);
		ibVideo.setSelected(false);
		ibImage.setSelected(false);
		ivStream.setVisibility(View.VISIBLE);
		ivVideo.setVisibility(View.INVISIBLE);
		ivImage.setVisibility(View.INVISIBLE);
		ivStreamPlay.setVisibility(View.VISIBLE);
		ivVideoPlay.setVisibility(View.INVISIBLE);
		if(hasStreamExpiryDate) {
			tvStreamExpiryDate.setVisibility(View.VISIBLE);
		}
		tvVideoExpiryDate.setVisibility(View.INVISIBLE);
		tvImageExpiryDate.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * Function to handle the event of when the user clicked on the video media button
	 */
	private void videoMediaSelected() {
		mediaButtonSelected = ibVideo;
		ibStream.setSelected(false);
		ibVideo.setSelected(true);
		ibImage.setSelected(false);
		ivStream.setVisibility(View.INVISIBLE);
		ivVideo.setVisibility(View.VISIBLE);
		ivImage.setVisibility(View.INVISIBLE);
		ivStreamPlay.setVisibility(View.INVISIBLE);
		ivVideoPlay.setVisibility(View.VISIBLE);
		tvStreamExpiryDate.setVisibility(View.INVISIBLE);
		if(hasVideoExpiryDate) {
			tvVideoExpiryDate.setVisibility(View.VISIBLE);
		}
		tvImageExpiryDate.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * Function to handle the event of when the user clicked on the image media button
	 */
	private void imageMediaSelected() {
		mediaButtonSelected = ibImage;
		ibStream.setSelected(false);
		ibVideo.setSelected(false);
		ibImage.setSelected(true);
		ivStream.setVisibility(View.INVISIBLE);
		ivVideo.setVisibility(View.INVISIBLE);
		ivImage.setVisibility(View.VISIBLE);
		ivStreamPlay.setVisibility(View.INVISIBLE);
		ivVideoPlay.setVisibility(View.INVISIBLE);
		tvStreamExpiryDate.setVisibility(View.INVISIBLE);
		tvVideoExpiryDate.setVisibility(View.INVISIBLE);
		if(hasImageExpiryDate) {
			tvImageExpiryDate.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Function to handle the event of when the user clicks on favorite/remove favorite
	 */
	private void favoriteClicked() {
		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_updating_favorites));
		try {
			if(tvFavorite.getText().toString().equals(getString(R.string.tv_favorite))) {
				// Make a server call and add to favorites
				MMFavoritesAdapter.addFavorite(new AddFavoriteCallback(),  
												locationDetails.getString(MMAPIConstants.JSON_KEY_LOCATION_ID), 
												locationDetails.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID), 
												MMConstants.PARTNER_ID, 
												userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
												userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
			} else if(tvFavorite.getText().toString().equals(getString(R.string.tv_remove_favorite))) {
				// Make a server call remove from favorites
				MMFavoritesAdapter.removeFavorite(new RemoveFavoriteCallback(),  
												  locationDetails.getString(MMAPIConstants.JSON_KEY_LOCATION_ID), 
												  locationDetails.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID), 
												  MMConstants.PARTNER_ID, 
												  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
												  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
			}
		} catch(JSONException e) {
			
		}
	}
	
	/**
	 * Function that makes a call to the server to request the updated favorites list after user either add/remove favorite for the current location
	 */
	private void updateFavoritesList() {
		MMFavoritesAdapter.getFavorites(new FavoritesCallback(),
				MMConstants.PARTNER_ID, 
				userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
				userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING));
	}
	
	/**
	 * Listener for when user clicks on phone field, address field, or add notifications field of the location details
	 * @author Dezapp, LLC
	 *
	 */
	public interface OnLocationDetailsItemClickListener {
		public void onLocationDetailsItem(int position, Object obj);
	}
	
	/**
	 * Callback to handle the result after making retrieve all media call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class MediaCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "mediaResults: " + (String) obj);
				try {
					JSONObject jObj = new JSONObject((String) obj);
					retrieveMedia = true;
					mediaResults = (String) obj;
					if(jObj.has(MMAPIConstants.JSON_KEY_STATUS)) {
						Toast.makeText(getActivity(), jObj.getString(MMAPIConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
						llFavorite.setClickable(false);
					} else {
						hasMedia();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Callback to display the image it retrieve from the mediaurl
	 * @author Dezapp, LLC
	 *
	 */
	private class LoadImageCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			ivImage.setImageBitmap((Bitmap) obj);
		}
	}
	
	/**
	 * Callback to handle the result after making add favorite call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class AddFavoriteCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				try {
					JSONObject response = new JSONObject(((String) obj));
					if(response.getString(MMAPIConstants.JSON_KEY_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
						Toast.makeText(getActivity(), R.string.toast_add_favorite, Toast.LENGTH_SHORT).show();
						tvFavorite.setText(R.string.tv_remove_favorite);
						updateFavoritesList();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Callback to handle the result after making remove favorite call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class RemoveFavoriteCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				try {
					JSONObject response = new JSONObject(((String) obj));
					if(response.getString(MMAPIConstants.JSON_KEY_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
						Toast.makeText(getActivity(), R.string.toast_remove_favorite, Toast.LENGTH_SHORT).show();
						tvFavorite.setText(R.string.tv_favorite);
						updateFavoritesList();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Callback to update the user's favorites list in app data after making get favorites call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class FavoritesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "response: " + ((String) obj));
				userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_BOOKMARKS, (String) obj);
				userPrefsEditor.commit();
			}
		}
	}
}
