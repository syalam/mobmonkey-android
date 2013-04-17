package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.MMVideoPlayerScreen;
import com.mobmonkey.mobmonkeyandroid.MakeARequestScreen;
import com.mobmonkey.mobmonkeyandroid.utils.MMArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMLocationDetailsAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
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
import android.widget.VideoView;

/**
 * @author Dezapp, LLC
 *
 */
public class LocationDetailsFragment extends MMFragment implements OnClickListener, OnItemClickListener {
	private static final String TAG = "LocationDetailsFragment: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	private JSONArray favoritesList;
	private JSONObject location;
	private JSONObject locationDetails;
	
	private TextView tvLocNameTitle;
	private TextView tvLocName;
	private LinearLayout llMakeRequest;
	private TextView tvMembersFound;
	private MMExpandedListView elvLocDetails;
	private LinearLayout llFavorite;
	private TextView tvFavorite;
	
	private LinearLayout llMedia;
//	private VideoView vvMedia;
	private ImageView ivMedia;
	private ImageButton ibPlay;
	private TextView tvExpiryDate;
	private ImageButton ibShareMedia;
	private ImageView ivtnStream;
	private ImageView ivtnVideo;
	private ImageView ivtnImage;
	private TextView tvStreamMediaCount;
	private TextView tvVideoMediaCount;
	private TextView tvImageMediaCount;
	
	private OnLocationDetailsItemClickListener listener;
	
	private String mediaResults;
	private boolean retrieveLocationDetails = false;
	private View mediaButtonSelected;
	private String mediaStreamVideoUrl;
	
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
//		vvMedia = (VideoView) view.findViewById(R.id.vvmedia);
		ivMedia = (ImageView) view.findViewById(R.id.ivmedia);
		ibPlay = (ImageButton) view.findViewById(R.id.ibplay);
		tvExpiryDate = (TextView) view.findViewById(R.id.tvexpirydate);
		ibShareMedia = (ImageButton) view.findViewById(R.id.ibsharemedia);
		ivtnStream = (ImageView) view.findViewById(R.id.ivtnstream);
		ivtnVideo = (ImageView) view.findViewById(R.id.ivtnvideo);
		ivtnImage = (ImageView) view.findViewById(R.id.ivtnimage);
		tvStreamMediaCount = (TextView) view.findViewById(R.id.tvstreammediacount);
		tvVideoMediaCount = (TextView) view.findViewById(R.id.tvvideomediacount);
		tvImageMediaCount = (TextView) view.findViewById(R.id.tvimagemediacount);
		
		try {
			if(!userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_FAVORITES, MMAPIConstants.DEFAULT_STRING_EMPTY).equals(MMAPIConstants.DEFAULT_STRING_EMPTY)) {
				favoritesList = new JSONArray(userPrefs.getString(MMAPIConstants.SHARED_PREFS_KEY_FAVORITES, MMAPIConstants.DEFAULT_STRING_EMPTY));
			} else {
				favoritesList = new JSONArray();
			}
			location = new JSONObject(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			
			if(!retrieveLocationDetails) {
				MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_loading_location_information));
				MMLocationDetailsAdapter.getLocationDetails(new LocationCallback(), 
						location.getString(MMAPIConstants.JSON_KEY_LOCATION_ID), 
						location.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID), 
						userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
						userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY), 
						MMConstants.PARTNER_ID);
				MMLocationDetailsAdapter.retrieveAllMediaForLocation(new MediaCallback(), 
						userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
						userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY), 
						MMConstants.PARTNER_ID, 
						location.getString(MMAPIConstants.JSON_KEY_LOCATION_ID), 
						location.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID));
				MMLocationDetailsAdapter.shareMediaForLocation(new ShareMediaCallback());
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
			case R.id.ibplay:
				intent = new Intent(getActivity(), MMVideoPlayerScreen.class);
				intent.putExtra(MMAPIConstants.JSON_KEY_MEDIA_URL, mediaStreamVideoUrl);
				startActivity(intent);
				break;
			case R.id.ibsharemedia:
				// TODO: fill in appropriate items for Share Media functionality
				//intent = new Intent(getActivity(), class);
				//intent.putExtra(name, value);
				//startActivity(intent);
				System.out.println("yay");
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
			listener.onLocationDetailsItem(position, MMAPIConstants.DEFAULT_STRING_EMPTY);
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
		tvMembersFound.setText(locationDetails.getString(MMAPIConstants.JSON_KEY_MONKEYS) + MMAPIConstants.DEFAULT_STRING_SPACE + getString(R.string.tv_members_found));
		
		int[] icons = new int[]{R.drawable.cat_icon_telephone, R.drawable.cat_icon_map_pin, R.drawable.cat_icon_alarm_clock};
		int[] indicatorIcons = new int[]{R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator, R.drawable.listview_accessory_indicator};
		String[] details = new String[3];
		String phoneNumber = locationDetails.getString(MMAPIConstants.JSON_KEY_PHONE_NUMBER);
		if(phoneNumber.equals(MMAPIConstants.DEFAULT_STRING_NULL) || phoneNumber.equals(MMAPIConstants.DEFAULT_STRING_EMPTY)) {
			details[0] = getString(R.string.tv_no_phone_number_available);
		} else {
			details[0] = phoneNumber;
		}
		details[1] = locationDetails.getString(MMAPIConstants.JSON_KEY_ADDRESS) + MMAPIConstants.DEFAULT_STRING_NEWLINE + locationDetails.getString(MMAPIConstants.JSON_KEY_LOCALITY) + MMAPIConstants.DEFAULT_STRING_COMMA_SPACE + 
				locationDetails.getString(MMAPIConstants.JSON_KEY_REGION) + MMAPIConstants.DEFAULT_STRING_COMMA_SPACE + locationDetails.getString(MMAPIConstants.JSON_KEY_POSTCODE);
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
			
			boolean isFirstMedia = true;
			
			for(int i = 0; i < mediaJArr.length(); i++) {
				JSONObject jObj = mediaJArr.getJSONObject(i);
				
				String mediaType = jObj.getString(MMAPIConstants.JSON_KEY_TYPE);
				
				if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_LIVESTREAMING)) {
					if(isFirstMedia) {
						mediaStreamVideoUrl = jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL);
//						vvMedia.setVideoURI(Uri.parse(mediaStreamVideoUrl));
//						vvMedia.seekTo(1);
						MediaMetadataRetriever mmr = new MediaMetadataRetriever();
						mmr.setDataSource(getActivity(), Uri.parse(mediaStreamVideoUrl));
						ivMedia.setImageBitmap(mmr.getFrameAtTime(1000));
						tvExpiryDate.setText(MMUtility.getDate(System.currentTimeMillis() - jObj.getLong(MMAPIConstants.JSON_KEY_EXPIRY_DATE), "mm") + "m");
						ibPlay.setVisibility(View.VISIBLE);
						ibPlay.setOnClickListener(LocationDetailsFragment.this);
						isFirstMedia = false;
					}
					streamMediaCount++;
				} else if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_VIDEO)) {
					if(isFirstMedia) {
						mediaStreamVideoUrl = jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL);
						Log.d(TAG, TAG + "mediaStreamVideoUrl: " + mediaStreamVideoUrl);
//						MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//						mmr.setDataSource(getActivity(), Uri.parse(mediaStreamVideoUrl));
//						
//						ivMedia.setImageBitmap(ThumbnailUtils.createVideoThumbnail(mediaStreamVideoUrl, Thumbnails.MICRO_KIND));
						tvExpiryDate.setText(MMUtility.getDate(System.currentTimeMillis() - jObj.getLong(MMAPIConstants.JSON_KEY_EXPIRY_DATE), "mm") + "m");
						ibPlay.setVisibility(View.VISIBLE);
						ibPlay.setOnClickListener(LocationDetailsFragment.this);
						isFirstMedia = false;
					}
					videoMediaCount++;
				} else if(mediaType.equals(MMAPIConstants.MEDIA_TYPE_IMAGE)) {
					if(isFirstMedia) {
						MMImageLoaderAdapter.loadImage(new LoadImageCallback(), jObj.getString(MMAPIConstants.JSON_KEY_MEDIA_URL));
						tvExpiryDate.setText(MMUtility.getDate(System.currentTimeMillis() - jObj.getLong(MMAPIConstants.JSON_KEY_EXPIRY_DATE), "mm") + "m");
						isFirstMedia = false;
					}
					imageMediaCount++;
				}
			}
			
			if(streamMediaCount > 0) {
				ivtnStream.setBackgroundResource(R.drawable.tn_hasmedia_stream);
				tvStreamMediaCount.setText(streamMediaCount + MMAPIConstants.DEFAULT_STRING_EMPTY);
			}
			if(videoMediaCount > 0) {
				ivtnVideo.setBackgroundResource(R.drawable.tn_hasmedia_video);
				tvVideoMediaCount.setText(videoMediaCount + MMAPIConstants.DEFAULT_STRING_EMPTY);
			}
			if(imageMediaCount > 0) {
				ivtnImage.setBackgroundResource(R.drawable.tn_hasmedia_image);
				tvImageMediaCount.setText(imageMediaCount + MMAPIConstants.DEFAULT_STRING_EMPTY);
			}
			
			ibShareMedia.setOnClickListener(LocationDetailsFragment.this);

		}
		// TODO: to be removed, for testing only
//		else {
//			llMedia.setVisibility(View.VISIBLE);
//			tvExpiryDate.setText("30m");
////			MMImageLoaderAdapter.loadImage(new LoadImageCallback(), "http://i.imgur.com/T0Va07Y.jpg");
//			
//			mediaStreamVideoUrl = "http://www.tools4movies.com/dvd_catalyst_profile_samples/Harold%20Kumar%203%20Christmas%20tablet.mp4";
//			ibPlay.setVisibility(View.VISIBLE);
//			ibPlay.setOnClickListener(LocationDetailsFragment.this);
//			streamMediaCount = 1;
//			
//			if(streamMediaCount > 0) {
//				ivtnStream.setBackgroundResource(R.drawable.tn_hasmedia_stream);
//				tvStreamMediaCount.setText(streamMediaCount + MMAPIConstants.DEFAULT_STRING);
//			}
//			if(videoMediaCount > 0) {
//				ivtnVideo.setBackgroundResource(R.drawable.tn_hasmedia_video);
//				tvVideoMediaCount.setText(videoMediaCount + MMAPIConstants.DEFAULT_STRING);
//			}
//			if(imageMediaCount > 0) {
//				ivtnImage.setBackgroundResource(R.drawable.tn_hasmedia_image);
//				tvImageMediaCount.setText(imageMediaCount + MMAPIConstants.DEFAULT_STRING);
//			}
//			
//			ibShareMedia.setOnClickListener(LocationDetailsFragment.this);
//		}
	}
	
	/**
	 * Function to handle the event of when the user clicks on favorite/remove favorite
	 */
	private void favoriteClicked() {
		MMProgressDialog.displayDialog(getActivity(), MMAPIConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_updating_favorites));
		try {
			if(tvFavorite.getText().toString().equals(getString(R.string.tv_favorite))) {
				// Make a server call and add to favorites
				MMFavoritesAdapter.addFavorite(new AddFavoriteCallback(),  
												locationDetails.getString(MMAPIConstants.JSON_KEY_LOCATION_ID), 
												locationDetails.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID), 
												MMConstants.PARTNER_ID, 
												userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
												userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY));
			} else if(tvFavorite.getText().toString().equals(getString(R.string.tv_remove_favorite))) {
				// Make a server call remove from favorites
				MMFavoritesAdapter.removeFavorite(new RemoveFavoriteCallback(),  
												  locationDetails.getString(MMAPIConstants.JSON_KEY_LOCATION_ID), 
												  locationDetails.getString(MMAPIConstants.JSON_KEY_PROVIDER_ID), 
												  MMConstants.PARTNER_ID, 
												  userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
												  userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY));
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
				userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING_EMPTY), 
				userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING_EMPTY));
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
	 * Callback to handle the result after making retrieve location info call to the server
	 * @author Dezapp, LLC
	 *
	 */
	private class LocationCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				Log.d(TAG, TAG + "Response: " + ((String) obj));
				try {
					locationDetails = new JSONObject((String) obj);
					
					if(locationDetails.has(MMAPIConstants.JSON_KEY_STATUS)) {
						MMLocationDetailsAdapter.cancelRetrieveAllMediaForLocation();
						MMProgressDialog.dismissDialog();
						
						LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View customDialog = inflater.inflate(com.mobmonkey.mobmonkeysdk.R.layout.mmtoast, null);
						ImageView ivToastImage = (ImageView)customDialog.findViewById(R.id.ivtoastimage);
						ivToastImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
						
						TextView ivToastText = (TextView)customDialog.findViewById(R.id.tvtoasttext);
						ivToastText.setText(R.string.toast_unable_to_load_location_info);
						MMDialog.displayCustomDialog(getActivity(), customDialog);
						
//						MMToast.makeToastWithImage(getActivity().getApplicationContext(), 
//								getActivity().getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel), 
//								getActivity().getString(R.string.toast_unable_to_load_location_info)).show();

						getActivity().onBackPressed();
					} else {
						retrieveLocationDetails = true;
						setLocationDetails();
					}
					
					setLocationDetails();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
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
					if(!jObj.has(MMAPIConstants.JSON_KEY_STATUS)) {
						mediaResults = (String) obj;
						hasMedia();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Callback to handle the result from a Share Media request
	 * @author Dezapp, LLC
	 *
	 */
	private class ShareMediaCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {			
			// TODO: Implement callback to handle Share Media functionality

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
			ivMedia.setImageBitmap((Bitmap) obj);
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
				userPrefsEditor.putString(MMAPIConstants.SHARED_PREFS_KEY_FAVORITES, (String) obj);
				userPrefsEditor.commit();
			}
		}
	}
}
