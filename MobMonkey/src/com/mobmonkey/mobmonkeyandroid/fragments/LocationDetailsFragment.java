package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.LocationDetailsMediaScreen;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.MakeARequestScreen;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMLocationDetailsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMLocationDetailsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMLocationDetailsAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
	private JSONObject location;
	private JSONObject locationInfo;
	
	private TextView tvNavBarTitle;
	private TextView tvLocName;
	private LinearLayout llMakeRequest;
	private TextView tvMembersFound;
	private MMExpandedListView elvLocInfo;
	private Button btnCreateHotSpot;
	private MMExpandedListView elvLoc;
	
	private ProgressBar pbLoadMedia;
	private LinearLayout llMedia;
	private ImageView ivtnMedia;
	private ImageButton ibPlay;
	private TextView tvExpiryDate;
	private ImageButton ibShareMedia;
	private ImageButton ibStream;
	private ImageButton ibVideo;
	private ImageButton ibImage;
	private TextView tvStreamMediaCount;
	private TextView tvVideoMediaCount;
	private TextView tvImageMediaCount;
	
	private MMLocationDetailsArrayAdapter locArrayAdapter;
	private MMLocationDetailsItem[] locItems;
	
	private JSONArray streamMediaUrl;
	private JSONArray videoMediaUrl;
	private JSONArray imageMediaUrl;
	
	private MMOnAddressFragmentItemClickListener onAddressFragmentItemClicklistener;
	private MMOnAddNotificationsFragmentItemClickListener onAddNotificationsFragmentItemClickListener;
	
	private String mediaResults;
	private boolean retrieveLocationDetails = true;
	private boolean retrieveVideoMedia = true;
	private boolean retrieveImageMedia = true;
	private Bitmap imageMedia;
	private View mediaButtonSelected;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_locationdetails_screen, container, false);
		
		tvNavBarTitle = (TextView) view.findViewById(R.id.tvnavbartitle);
		tvLocName = (TextView) view.findViewById(R.id.tvlocname);
		llMakeRequest = (LinearLayout) view.findViewById(R.id.llmakerequest);
		tvMembersFound = (TextView) view.findViewById(R.id.tvmembersfound);
		elvLocInfo = (MMExpandedListView) view.findViewById(R.id.elvlocinfo);
		btnCreateHotSpot = (Button) view.findViewById(R.id.btncreatehotspot);
		elvLoc = (MMExpandedListView) view.findViewById(R.id.elvloc);
		
		pbLoadMedia = (ProgressBar) view.findViewById(R.id.pbloadmedia);
		llMedia = (LinearLayout) view.findViewById(R.id.llmedia);
		ivtnMedia = (ImageView) view.findViewById(R.id.ivtnmedia);
		ibPlay = (ImageButton) view.findViewById(R.id.ibplay);
		tvExpiryDate = (TextView) view.findViewById(R.id.tvexpirydate);
		ibShareMedia = (ImageButton) view.findViewById(R.id.ibsharemedia);
		ibStream = (ImageButton) view.findViewById(R.id.ibstream);
		ibVideo = (ImageButton) view.findViewById(R.id.ibvideo);
		ibImage = (ImageButton) view.findViewById(R.id.ibimage);
		tvStreamMediaCount = (TextView) view.findViewById(R.id.tvstreammediacount);
		tvVideoMediaCount = (TextView) view.findViewById(R.id.tvvideomediacount);
		tvImageMediaCount = (TextView) view.findViewById(R.id.tvimagemediacount);
		
		streamMediaUrl = new JSONArray();
		videoMediaUrl = new JSONArray();
		imageMediaUrl = new JSONArray();
		
		try {
			if(!userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, MMSDKConstants.DEFAULT_STRING_EMPTY).equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				favoritesList = new JSONArray(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, MMSDKConstants.DEFAULT_STRING_EMPTY));
			} else {
				favoritesList = new JSONArray();
			}
			location = new JSONObject(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			setLocationDetails();
			if(retrieveLocationDetails) {
				MMLocationDetailsAdapter.getLocationDetails(new LocationCallback(),
															location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID),
															location.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID),
															MMConstants.PARTNER_ID,
															userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
															userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				MMMediaAdapter.retrieveAllMediaForLocation(new MediaCallback(),
														   location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID),
														   location.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID),
														   MMConstants.PARTNER_ID,
														   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
														   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
//				MMProgressDialog.displayDialog(getActivity(),
//											   MMSDKConstants.DEFAULT_STRING_EMPTY,
//											   getString(R.string.pd_loading_location_information));
			} else {
				setLocationMembers();
				hasMedia();
				if(mediaButtonSelected != null) {
					onClick(mediaButtonSelected);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		return view;		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnAddressFragmentItemClickListener) {
			onAddressFragmentItemClicklistener = (MMOnAddressFragmentItemClickListener) activity;
			if(activity instanceof MMOnAddNotificationsFragmentItemClickListener) {
				onAddNotificationsFragmentItemClickListener = (MMOnAddNotificationsFragmentItemClickListener) activity;
			}
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
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, location.toString());
				startActivity(intent);
				break;
			case R.id.ibstream:
				intent = new Intent(getActivity(), LocationDetailsMediaScreen.class);
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE, MMSDKConstants.MEDIA_TYPE_LIVESTREAMING);
				intent.putExtra(MMSDKConstants.MEDIA_LIVESTREAMING, streamMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_VIDEO, videoMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_IMAGE, imageMediaUrl.toString());
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_WIDTH, ivtnMedia.getMeasuredWidth());
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_HEIGHT, ivtnMedia.getMeasuredHeight());
				startActivity(intent);
				break;
			case R.id.ibvideo:
				intent = new Intent(getActivity(), LocationDetailsMediaScreen.class);
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE, MMSDKConstants.MEDIA_TYPE_VIDEO);
				intent.putExtra(MMSDKConstants.MEDIA_LIVESTREAMING, streamMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_VIDEO, videoMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_IMAGE, imageMediaUrl.toString());
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_WIDTH, ivtnMedia.getMeasuredWidth());
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_HEIGHT, ivtnMedia.getMeasuredHeight());
				startActivity(intent);
				break;
			case R.id.ibimage:
				intent = new Intent(getActivity(), LocationDetailsMediaScreen.class);
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE, MMSDKConstants.MEDIA_TYPE_IMAGE);
				intent.putExtra(MMSDKConstants.MEDIA_LIVESTREAMING, streamMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_VIDEO, videoMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_IMAGE, imageMediaUrl.toString());
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_WIDTH, ivtnMedia.getMeasuredWidth());
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_HEIGHT, ivtnMedia.getMeasuredHeight());
				startActivity(intent);
				break;
			case R.id.btncreatehotspot:
				break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		switch(adapterView.getId()) {
			case R.id.elvlocinfo:
				if(position == 0) {
					Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
					dialerIntent.setData(Uri.parse("tel:" +  ((TextView)view.findViewById(R.id.tvlabel)).getText().toString()));
					startActivity(dialerIntent);
				} else if(position == 1) {
					onAddressFragmentItemClicklistener.onAddressFragmentItemClick(location);
				}
				break;
			case R.id.elvloc:
				if(position == 0) {
					onAddNotificationsFragmentItemClickListener.onAddNotificationsFragmentItemClick(location);
				} else if(position == 1) {
					TextView tvFavorite = (TextView) view.findViewById(R.id.tvlabel);
					if(tvFavorite.getText().toString().equals(getString(R.string.tv_favorite))) {
						addFavorite();
					} else if(tvFavorite.getText().toString().equals(getString(R.string.tv_remove_favorite))) {
						removeFavorite();
					}
				}
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		Log.d(TAG, "onFragmentBackPressed");
		MMMediaAdapter.cancelRetrieveAllMediaForLocation();
	}
	
	/**
	 * Function that set all the details of the current location
	 * @throws JSONException
	 */
	private void setLocationDetails() throws JSONException {
		tvNavBarTitle.setText(location.getString(MMSDKConstants.JSON_KEY_NAME));
		tvLocName.setText(location.getString(MMSDKConstants.JSON_KEY_NAME));
		
		MMLocationDetailsItem[] mmLocationDetailsItems = new MMLocationDetailsItem[2];
		for(int i = 0; i < mmLocationDetailsItems.length; i++) {
			mmLocationDetailsItems[i] = new MMLocationDetailsItem();
		}
		
		mmLocationDetailsItems[0].setLocationDetailIconId(R.drawable.cat_icon_telephone);
		String phoneNumber = location.getString(MMSDKConstants.JSON_KEY_PHONE_NUMBER);
		if(phoneNumber.equals(MMSDKConstants.DEFAULT_STRING_NULL) || phoneNumber.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			mmLocationDetailsItems[0].setLocationDetail(getString(R.string.tv_no_phone_number_available));
		} else {
			mmLocationDetailsItems[0].setLocationDetail(phoneNumber);
		}
		mmLocationDetailsItems[1].setLocationDetailIconId(R.drawable.cat_icon_address);
		mmLocationDetailsItems[1].setLocationDetail(location.getString(MMSDKConstants.JSON_KEY_ADDRESS) +
				MMSDKConstants.DEFAULT_STRING_NEWLINE +
				location.getString(MMSDKConstants.JSON_KEY_LOCALITY) +
				MMSDKConstants.DEFAULT_STRING_COMMA_SPACE +
				location.getString(MMSDKConstants.JSON_KEY_REGION) +
				MMSDKConstants.DEFAULT_STRING_COMMA_SPACE +
				location.getString(MMSDKConstants.JSON_KEY_POSTCODE));
		
		ArrayAdapter<MMLocationDetailsItem> arrayAdapter = new MMLocationDetailsArrayAdapter(getActivity(), R.layout.listview_row_locationdetails, mmLocationDetailsItems);
		arrayAdapter.isEnabled(0);
		elvLocInfo.setAdapter(arrayAdapter);
		
		locItems = new MMLocationDetailsItem[2];
		for(int i = 0; i < locItems.length; i++) {
			locItems[i] = new MMLocationDetailsItem();
		}
		
		locItems[0].setLocationDetailIconId(R.drawable.cat_icon_alarm_clock);
		locItems[0].setLocationDetail(getString(R.string.tv_add_notifications));
		locItems[1].setLocationDetailIconId(R.drawable.cat_icon_favorite);
		locItems[1].setLocationDetail(getString(R.string.tv_favorite));
		
		for(int i = 0; i < favoritesList.length(); i++) {
			if(favoritesList.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_LOCATION_ID).equals(location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID))) {
				locItems[1].setLocationDetail(getString(R.string.tv_remove_favorite));
				break;
			}
		}
		
		locArrayAdapter = new MMLocationDetailsArrayAdapter(getActivity(), R.layout.listview_row_locationdetails, locItems);
		elvLoc.setAdapter(locArrayAdapter);
		
		llMakeRequest.setOnClickListener(LocationDetailsFragment.this);
		elvLocInfo.setOnItemClickListener(LocationDetailsFragment.this);
		btnCreateHotSpot.setOnClickListener(LocationDetailsFragment.this);
		elvLoc.setOnItemClickListener(LocationDetailsFragment.this);
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void setLocationMembers() throws JSONException {
		if(locationInfo.getInt(MMSDKConstants.JSON_KEY_MONKEYS) == MMSDKConstants.DEFAULT_INT_ZERO) {
			tvMembersFound.setText(R.string.tv_no_members_found);
		} else {
			tvMembersFound.setText(locationInfo.getInt(MMSDKConstants.JSON_KEY_MONKEYS) + MMSDKConstants.DEFAULT_STRING_SPACE + getString(R.string.tv_members_found));
		}
	}
	
	/**
	 * Function that handles the processing of the result from retrieve all media call to the server
	 * @throws JSONException
	 */
	private void hasMedia() throws JSONException {
		if(mediaResults != null) {
			JSONObject mediaJObj = new JSONObject(mediaResults); 
			JSONArray mediaJArr = mediaJObj.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA);
			Log.d(TAG, "mediaResults: " + mediaJObj.toString());
			int streamMediaCount = 0;
			int videoMediaCount = 0;
			int imageMediaCount = 0;
			
			if(mediaJArr.length() > 0) {
				llMedia.setVisibility(View.VISIBLE);
				
				boolean isFirstMedia = true;
				
				for(int i = 0; i < mediaJArr.length(); i++) {
					JSONObject jObj = mediaJArr.getJSONObject(i);
					
					String media = jObj.getString(MMSDKConstants.JSON_KEY_TYPE);
					
					if(media.equals(MMSDKConstants.MEDIA_LIVESTREAMING)) {
						if(isFirstMedia) {
							if(retrieveVideoMedia) {
								MMImageLoaderAdapter.loadImage(new LoadImageCallback(),
															   jObj.getString(MMSDKConstants.JSON_KEY_THUMB_URL));
							} else {
								ivtnMedia.setImageBitmap(imageMedia);
							}
							tvExpiryDate.setText(MMUtility.getExpiryDate(System.currentTimeMillis() - jObj.getLong(MMSDKConstants.JSON_KEY_UPLOADED_DATE)));
							ibPlay.setVisibility(View.VISIBLE);
							ibPlay.setOnClickListener(LocationDetailsFragment.this);
							isFirstMedia = false;
						}
						streamMediaUrl.put(jObj);
						streamMediaCount++;
					} else if(media.equals(MMSDKConstants.MEDIA_VIDEO)) {
						if(isFirstMedia) {
							if(retrieveVideoMedia) {
								MMImageLoaderAdapter.loadImage(new LoadVideoThumbnailCallback(),
															   jObj.getString(MMSDKConstants.JSON_KEY_THUMB_URL));
							} else {
								ivtnMedia.setImageBitmap(imageMedia);
							}
							tvExpiryDate.setVisibility(View.VISIBLE);
							tvExpiryDate.setText(MMUtility.getExpiryDate(System.currentTimeMillis() - jObj.getLong(MMSDKConstants.JSON_KEY_UPLOADED_DATE)));
							ibPlay.setVisibility(View.VISIBLE);
							ibPlay.setOnClickListener(new MMVideoPlayOnClickListener(getActivity(), jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL)));
							isFirstMedia = false;
						}
						videoMediaUrl.put(jObj);
						videoMediaCount++;
					} else if(media.equals(MMSDKConstants.MEDIA_IMAGE)) {
						if(isFirstMedia) {
							ivtnMedia.setClickable(true);
							if(retrieveImageMedia) {
								MMImageLoaderAdapter.loadImage(new LoadImageCallback(),
															   jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL));
							} else {
								Log.d(TAG, "width: " + ivtnMedia.getWidth() + " height: " + ivtnMedia.getHeight());
								Log.d(TAG, "measuredWidth: " + ivtnMedia.getMeasuredWidth() + " measuredHeight: " + ivtnMedia.getMeasuredHeight());
								ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(imageMedia, ivtnMedia.getMeasuredWidth(), ivtnMedia.getMeasuredHeight()));
								ivtnMedia.setOnClickListener(new MMImageOnClickListener(getActivity(), imageMedia));
							}
							tvExpiryDate.setVisibility(View.VISIBLE);
							tvExpiryDate.setText(MMUtility.getExpiryDate(System.currentTimeMillis() - jObj.getLong(MMSDKConstants.JSON_KEY_UPLOADED_DATE)));
							isFirstMedia = false;
						}
						imageMediaUrl.put(jObj);
						imageMediaCount++;
					}
				}
				
				if(streamMediaCount > 0) {
					ibStream.setOnClickListener(LocationDetailsFragment.this);
					ibStream.setBackgroundResource(R.drawable.tn_hasmedia_stream);
					tvStreamMediaCount.setText(streamMediaCount + MMSDKConstants.DEFAULT_STRING_EMPTY);
				}
				if(videoMediaCount > 0) {
					ibVideo.setOnClickListener(LocationDetailsFragment.this);
					ibVideo.setBackgroundResource(R.drawable.tn_hasmedia_video);
					tvVideoMediaCount.setText(videoMediaCount + MMSDKConstants.DEFAULT_STRING_EMPTY);
				}
				if(imageMediaCount > 0) {
					ibImage.setOnClickListener(LocationDetailsFragment.this);
					ibImage.setBackgroundResource(R.drawable.tn_hasmedia_image);
					tvImageMediaCount.setText(imageMediaCount + MMSDKConstants.DEFAULT_STRING_EMPTY);
				}
				
				ibShareMedia.setOnClickListener(new MMShareMediaOnClickListener(getActivity()));
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
	//		}
		}
	}
	
	/**
	 * Make a server call to add location to favorites
	 */
	private void addFavorite() {
		try {
			MMFavoritesAdapter.addFavorite(new AddFavoriteCallback(),  
										   location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID),
										   location.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID),
										   MMConstants.PARTNER_ID,
										   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_updating_favorites));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Make a server call to remove location from favorites
	 */
	private void removeFavorite() {
		try {
			MMFavoritesAdapter.removeFavorite(new RemoveFavoriteCallback(),  
											  location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID), 
											  location.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID), 
											  MMConstants.PARTNER_ID, 
											  userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY), 
											  userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_updating_favorites));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that makes a call to the server to request the updated favorites list after user either add/remove favorite for the current location
	 */
	private void updateFavoritesList() {
		MMFavoritesAdapter.getFavorites(new FavoritesCallback(),
										MMConstants.PARTNER_ID,
										userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
										userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
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
				Log.d(TAG, TAG + "location info: " + (String) obj);
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						locationInfo = new JSONObject((String) obj);
						
						if(locationInfo.has(MMSDKConstants.JSON_KEY_STATUS)) {
							MMMediaAdapter.cancelRetrieveAllMediaForLocation();
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
	//								getString(R.string.toast_unable_to_load_location_info)).show();
	
							getActivity().onBackPressed();
						} else {
							retrieveLocationDetails = false;
							setLocationMembers();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
			pbLoadMedia.setVisibility(View.GONE);
			
			if(obj != null) {
				Log.d(TAG, TAG + "mediaResults: " + (String) obj);
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if(!jObj.has(MMSDKConstants.JSON_KEY_STATUS)) {
							mediaResults = (String) obj;
							hasMedia();
						} else {
							MMProgressDialog.dismissDialog();
							Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
				}
				}
			}
		}
	}
	
//	/**
//	 * Callback to handle the result from a Share Media request
//	 * @author Dezapp, LLC
//	 *
//	 */
//	private class ShareMediaCallback implements MMCallback {
//		@Override
//		public void processCallback(Object obj) {			
//			// TODO: Implement callback to handle Share Media functionality
//
//		}
//	}	
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class LoadVideoThumbnailCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					}
				} else if(obj instanceof Bitmap){				
					retrieveImageMedia = false;
					imageMedia = (Bitmap) obj;
					ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(imageMedia, ivtnMedia.getMeasuredWidth(), ivtnMedia.getMeasuredHeight()));
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
			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					}
				} else if(obj instanceof Bitmap){				
					retrieveImageMedia = false;
					imageMedia = (Bitmap) obj;
					ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(imageMedia, ivtnMedia.getMeasuredWidth(), ivtnMedia.getMeasuredHeight()));
					ivtnMedia.setOnClickListener(new MMImageOnClickListener(getActivity(), imageMedia));
				}
			}
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
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject response = new JSONObject(((String) obj));
						if(response.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							Toast.makeText(getActivity(), R.string.toast_add_favorite_success, Toast.LENGTH_SHORT).show();
							locItems[1].setLocationDetail(getString(R.string.tv_remove_favorite));
							locArrayAdapter.notifyDataSetChanged();
							updateFavoritesList();
						} else {
							MMDialog.dismissDialog();
							Toast.makeText(getActivity(), R.string.toast_add_favorite_fail, Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject response = new JSONObject(((String) obj));
						if(response.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							Toast.makeText(getActivity(), R.string.toast_remove_favorite_successs, Toast.LENGTH_SHORT).show();
							locItems[1].setLocationDetail(getString(R.string.tv_favorite));
							locArrayAdapter.notifyDataSetChanged();
							updateFavoritesList();
						} else {
							MMProgressDialog.dismissDialog();
							Toast.makeText(getActivity(), R.string.toast_remove_favorite_fail, Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
				}
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
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, TAG + "response: " + ((String) obj));
					userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, (String) obj);
					userPrefsEditor.commit();
				}
				
			}
		}
	}
}
