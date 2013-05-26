package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.LocationDetailsMediaScreen;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.MakeARequestScreen;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMExistingHotSpotsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMLocationDetailsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMLocationDetailsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageDownloaderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMImageLoaderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMLocationAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMMediaAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Dezapp, LLC
 *
 */
public class LocationDetailsFragment extends MMFragment implements OnClickListener,
																   OnItemClickListener {
	private static final String TAG = "LocationDetailsFragment: ";
	
	private SharedPreferences userPrefs;
	private JSONArray favoritesList;
	private JSONObject location;
	private JSONObject locationInfo;
	private JSONArray subLocations;
	
	private TextView tvNavBarTitle;
	private TextView tvLocName;
	private ImageView ivHotSpotsBadge;
	private TextView tvHotSpotsCounter;
	private LinearLayout llMakeRequest;
	private TextView tvMembersFound;
	private MMExpandedListView elvLocInfo;
	private LinearLayout llHotSpots;
	private TextView tvHotSpots;
	private MMExpandedListView elvHotSpots;
	private Button btnCreateHotSpot;
	private MMExpandedListView elvLoc;
	private LinearLayout llDeleteLocationHotSpot;
	private TextView tvDeleteLocationHotSpot;
	
	private ProgressBar pbLoadMedia;
	private LinearLayout llMedia;
	private ImageView ivtnMedia;
	private ImageButton ibPlay;
	private TextView tvExpiryDate;
	private TextView tvMediaMessage;
	private ImageButton ibShareMedia;
	private ImageButton ibStream;
	private ImageButton ibVideo;
	private ImageButton ibImage;
	private TextView tvStreamMediaCount;
	private TextView tvVideoMediaCount;
	private TextView tvImageMediaCount;
	
	private MMLocationDetailsArrayAdapter locArrayAdapter;
	private MMExistingHotSpotsArrayAdapter existingHotSpotsArrayAdapter;
	private MMLocationDetailsItem[] locItems;
	
	private JSONArray streamMediaUrl;
	private JSONArray videoMediaUrl;
	private JSONArray imageMediaUrl;
	
	private MMOnAddressFragmentItemClickListener addressFragmentItemClickListener;
	private MMOnNearbyLocationsItemClickListener nearbyLocationsItemClickListener;
	private MMOnCreateHotSpotFragmentClickListener createHotSpotFragmentClickListener;
	private MMOnAddNotificationsFragmentItemClickListener addNotificationsFragmentItemClickListener;
	private MMOnDeleteHotSpotFragmentFinishListener deleteHotSpotFinishFragmentListener;
	
	private String mediaResults;
	private boolean retrieveLocationDetails = true;
	private boolean retrieveVideoMedia = true;
	private boolean retrieveImageMedia = true;
	private Bitmap imageMedia;
	private View mediaButtonSelected;
	private boolean isLocation;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		View view = inflater.inflate(R.layout.fragment_locationdetails_screen, container, false);
		
		tvNavBarTitle = (TextView) view.findViewById(R.id.tvnavbartitle);
		tvLocName = (TextView) view.findViewById(R.id.tvlocname);
		ivHotSpotsBadge = (ImageView) view.findViewById(R.id.ivhotspotsbadge);
		tvHotSpotsCounter = (TextView) view.findViewById(R.id.tvhotspotscounter);
		llMakeRequest = (LinearLayout) view.findViewById(R.id.llmakerequest);
		tvMembersFound = (TextView) view.findViewById(R.id.tvmembersfound);
		elvLocInfo = (MMExpandedListView) view.findViewById(R.id.elvlocinfo);
		llHotSpots = (LinearLayout) view.findViewById(R.id.llhotspots);
		tvHotSpots = (TextView) view.findViewById(R.id.tvhotspots);
		elvHotSpots = (MMExpandedListView) view.findViewById(R.id.elvhotspots);
		btnCreateHotSpot = (Button) view.findViewById(R.id.btncreatehotspot);
		elvLoc = (MMExpandedListView) view.findViewById(R.id.elvloc);
		llDeleteLocationHotSpot = (LinearLayout) view.findViewById(R.id.lldeletelocationhotspot);
		tvDeleteLocationHotSpot = (TextView) view.findViewById(R.id.tvdeletelocationhotspot);
		
		pbLoadMedia = (ProgressBar) view.findViewById(R.id.pbloadmedia);
		llMedia = (LinearLayout) view.findViewById(R.id.llmedia);
		ivtnMedia = (ImageView) view.findViewById(R.id.ivtnmedia);
		ibPlay = (ImageButton) view.findViewById(R.id.ibplay);
		tvExpiryDate = (TextView) view.findViewById(R.id.tvexpirydate);
		tvMediaMessage = (TextView) view.findViewById(R.id.tvmediamessage);
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
			Log.d(TAG, TAG + "location: " + location.toString());
			setLocationDetails();
			checkForHotSpots();
			if(retrieveLocationDetails) {
				MMLocationAdapter.getLocationInfo(new LocationCallback(),
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

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnAddressFragmentItemClickListener) {
			addressFragmentItemClickListener = (MMOnAddressFragmentItemClickListener) activity;
			if(activity instanceof MMOnNearbyLocationsItemClickListener) {
				nearbyLocationsItemClickListener = (MMOnNearbyLocationsItemClickListener) activity;
				if(activity instanceof MMOnCreateHotSpotFragmentClickListener) {
					createHotSpotFragmentClickListener = (MMOnCreateHotSpotFragmentClickListener) activity;
					if(activity instanceof MMOnAddNotificationsFragmentItemClickListener) {
						addNotificationsFragmentItemClickListener = (MMOnAddNotificationsFragmentItemClickListener) activity;
						if(activity instanceof MMOnDeleteHotSpotFragmentFinishListener) {
							deleteHotSpotFinishFragmentListener = (MMOnDeleteHotSpotFragmentFinishListener) activity;
						}
					}
				}
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
			case R.id.tvmediamessage:
				Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mobmonkey.com")); // TODO: hardcoded, to be removed
//				Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationInfo.getString(MMSDKConstants.JSON_KEY_MESSAGE_URL)));
				startActivity(urlIntent);
				break;
			case R.id.ibstream:
				intent = new Intent(getActivity(), LocationDetailsMediaScreen.class);
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE, MMSDKConstants.MEDIA_TYPE_LIVESTREAMING);
				intent.putExtra(MMSDKConstants.MEDIA_LIVESTREAMING, streamMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_VIDEO, videoMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_IMAGE, imageMediaUrl.toString());
				startActivity(intent);
				break;
			case R.id.ibvideo:
				intent = new Intent(getActivity(), LocationDetailsMediaScreen.class);
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE, MMSDKConstants.MEDIA_TYPE_VIDEO);
				intent.putExtra(MMSDKConstants.MEDIA_LIVESTREAMING, streamMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_VIDEO, videoMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_IMAGE, imageMediaUrl.toString());
				startActivity(intent);
				break;
			case R.id.ibimage:
				intent = new Intent(getActivity(), LocationDetailsMediaScreen.class);
				intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_MEDIA_TYPE, MMSDKConstants.MEDIA_TYPE_IMAGE);
				intent.putExtra(MMSDKConstants.MEDIA_LIVESTREAMING, streamMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_VIDEO, videoMediaUrl.toString());
				intent.putExtra(MMSDKConstants.MEDIA_IMAGE, imageMediaUrl.toString());
				startActivity(intent);
				break;
			case R.id.btncreatehotspot:
				createHotSpotFragmentClickListener.onCreateHotSpotClick(locationInfo, MMSDKConstants.REQUEST_CODE_LOCATION_DETAILS);
				break;
			case R.id.lldeletelocationhotspot:
				try {
					promptDeleteLocationHotSpot();
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
					addressFragmentItemClickListener.onAddressFragmentItemClick(location);
				}
				break;
			case R.id.elvhotspots:
				nearbyLocationsItemClickListener.onNearbyLocationsItemClick(existingHotSpotsArrayAdapter.getItem(position).toString());
				break;
			case R.id.elvloc:
				if(position == 0) {
					addNotificationsFragmentItemClickListener.onAddNotificationsFragmentItemClick(location);
				} else if(position == 1) {
					TextView tvFavorite = (TextView) view.findViewById(R.id.tvlabel);
					if(tvFavorite.getText().toString().equals(getString(R.string.tv_add_to_favorites))) {
						addFavorite();
					} else if(tvFavorite.getText().toString().equals(getString(R.string.tv_remove_from_favorites))) {
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
		MMLocationAdapter.cancelGetLocationInfo();
		MMMediaAdapter.cancelRetrieveAllMediaForLocation();
	}
	
	/**
	 * Function that set all the details of the current location
	 * @throws JSONException
	 */
	private void setLocationDetails() throws JSONException {		
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
		String address = MMSDKConstants.DEFAULT_STRING_EMPTY;
		address = location.isNull(MMSDKConstants.JSON_KEY_ADDRESS) ? MMSDKConstants.DEFAULT_STRING_EMPTY : location.getString(MMSDKConstants.JSON_KEY_ADDRESS);
		address += MMSDKConstants.DEFAULT_STRING_NEWLINE;
		
		String localityRegion = MMSDKConstants.DEFAULT_STRING_EMPTY;
		localityRegion += location.isNull(MMSDKConstants.JSON_KEY_LOCALITY) ? MMSDKConstants.DEFAULT_STRING_EMPTY : location.getString(MMSDKConstants.JSON_KEY_LOCALITY);
		localityRegion += location.isNull(MMSDKConstants.JSON_KEY_LOCALITY) || location.isNull(MMSDKConstants.JSON_KEY_REGION) ? MMSDKConstants.DEFAULT_STRING_EMPTY : MMSDKConstants.DEFAULT_STRING_COMMA_SPACE;
		localityRegion += location.isNull(MMSDKConstants.JSON_KEY_REGION) ? MMSDKConstants.DEFAULT_STRING_EMPTY : location.getString(MMSDKConstants.JSON_KEY_REGION); 

		mmLocationDetailsItems[1].setLocationDetail(address + localityRegion);
		
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
		locItems[1].setLocationDetail(getString(R.string.tv_add_to_favorites));
		
		for(int i = 0; i < favoritesList.length(); i++) {
			if(favoritesList.getJSONObject(i).getString(MMSDKConstants.JSON_KEY_LOCATION_ID).equals(location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID))) {
				locItems[1].setLocationDetail(getString(R.string.tv_remove_from_favorites));
				break;
			}
		}
		
		locArrayAdapter = new MMLocationDetailsArrayAdapter(getActivity(), R.layout.listview_row_locationdetails, locItems);
		elvLoc.setAdapter(locArrayAdapter);
		
		llMakeRequest.setOnClickListener(LocationDetailsFragment.this);
		elvLocInfo.setOnItemClickListener(LocationDetailsFragment.this);
		elvLoc.setOnItemClickListener(LocationDetailsFragment.this);
	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void checkForHotSpots() throws JSONException {		
		if(location.isNull(MMSDKConstants.JSON_KEY_PARENT_LOCATION_ID)) {
			tvNavBarTitle.setText(location.getString(MMSDKConstants.JSON_KEY_NAME));
			tvLocName.setText(location.getString(MMSDKConstants.JSON_KEY_NAME));
			if(!location.isNull(MMSDKConstants.JSON_KEY_SUB_LOCATIONS)) {
				subLocations = location.getJSONArray(MMSDKConstants.JSON_KEY_SUB_LOCATIONS);
				
				tvHotSpots.setVisibility(View.VISIBLE);
			} else {
				LinearLayout.LayoutParams params = (LayoutParams) llHotSpots.getLayoutParams();
				params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getActivity().getResources().getDisplayMetrics());
				btnCreateHotSpot.setLayoutParams(params);
			}
			btnCreateHotSpot.setOnClickListener(LocationDetailsFragment.this);
			tvDeleteLocationHotSpot.setText(R.string.btn_delete_location);
			isLocation = true;
		} else {
			tvNavBarTitle.setText(getString(R.string.tv_title_hot_spot) + MMSDKConstants.DEFAULT_STRING_SPACE + location.getString(MMSDKConstants.JSON_KEY_NAME));
			tvLocName.setText(getString(R.string.tv_title_hot_spot) + MMSDKConstants.DEFAULT_STRING_SPACE + location.getString(MMSDKConstants.JSON_KEY_NAME));
			tvDeleteLocationHotSpot.setText(R.string.btn_delete_hot_spot);
			isLocation = false;
		}
	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void setHotSpots() throws JSONException {
		if(location.isNull(MMSDKConstants.JSON_KEY_PARENT_LOCATION_ID)) {
			if(subLocations != null && subLocations.length() > 0) {
				ArrayList<JSONObject> hotSpots = new ArrayList<JSONObject>();
				for(int i = 0; i < subLocations.length(); i++) {
					hotSpots.add(subLocations.getJSONObject(i));
				}
				
				tvHotSpotsCounter.setText(Integer.toString(subLocations.length()));
				existingHotSpotsArrayAdapter = new MMExistingHotSpotsArrayAdapter(getActivity(), R.layout.listview_row_location_details_hot_spots, hotSpots);
				elvHotSpots.setAdapter(existingHotSpotsArrayAdapter);
			
				elvHotSpots.setOnItemClickListener(LocationDetailsFragment.this);
				
				ivHotSpotsBadge.setVisibility(View.VISIBLE);
				tvHotSpotsCounter.setVisibility(View.VISIBLE);
				elvHotSpots.setVisibility(View.VISIBLE);
				btnCreateHotSpot.setBackgroundResource(R.drawable.listview_border_bottom_corners_round_no_top);
				btnCreateHotSpot.setPadding(20, 20, 20, 20);
			} else {
				tvHotSpots.setVisibility(View.GONE);
				btnCreateHotSpot.setBackgroundResource(R.drawable.listview_border_four_corners_round);
			}
			btnCreateHotSpot.setVisibility(View.VISIBLE);
		}
		
		if(!location.isNull(MMSDKConstants.JSON_KEY_SUBMITTER_EMAIL)) {
			if(location.getString(MMSDKConstants.JSON_KEY_SUBMITTER_EMAIL).equals(userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY))) {
				llDeleteLocationHotSpot.setOnClickListener(LocationDetailsFragment.this);
				llDeleteLocationHotSpot.setVisibility(View.VISIBLE);
				
				Log.d(TAG, TAG + "submitter email is same as login email!!!!!");
				//TODO: implement display update/delete location button
			}
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void setLocationMembers() throws JSONException {
		if(locationInfo.getInt(MMSDKConstants.JSON_KEY_MONKEYS) == MMSDKConstants.DEFAULT_INT_ZERO) {
			tvMembersFound.setText(R.string.tv_no_members_found);
		} else {
			getString(R.string.tv_members_found);
			tvMembersFound.setText(locationInfo.getInt(MMSDKConstants.JSON_KEY_MONKEYS) + MMSDKConstants.DEFAULT_STRING_SPACE + getString(R.string.tv_members_found));
		}
		tvMediaMessage.setText("See what's happening now on MobMonkey!"); // TODO: hardcoded, to be removed later
		tvMediaMessage.setOnClickListener(LocationDetailsFragment.this);
//		tvMediaMessage.setText(locationInfo.getString(MMSDKConstants.JSON_KEY_MESSAGE));
	}
	
	/**
	 * Function that handles the processing of the result from retrieve all media call to the server
	 * @throws JSONException
	 */
	private void hasMedia() throws JSONException {
		if(mediaResults != null) {
			JSONObject mediaJObj = new JSONObject(mediaResults); 
			JSONArray mediaJArr = mediaJObj.getJSONArray(MMSDKConstants.JSON_KEY_MEDIA);
			int streamMediaCount = 0;
			int videoMediaCount = 0;
			int imageMediaCount = 0;
			
			pbLoadMedia.setVisibility(View.GONE);
			setHotSpots();
			
			if(mediaJArr.length() > 0) {
				llMedia.setVisibility(View.VISIBLE);
				
				boolean isFirstMedia = true;
				for(int i = 0; i < mediaJArr.length(); i++) {
					JSONObject jObj = mediaJArr.getJSONObject(i);
					String media = jObj.getString(MMSDKConstants.JSON_KEY_TYPE);
					
					if(media.equals(MMSDKConstants.MEDIA_LIVESTREAMING)) {
						if(isFirstMedia) {
							if(retrieveVideoMedia) {
								MMImageLoaderAdapter.loadImage(new LoadVideoThumbnailCallback(),
															   getActivity().getWindowManager().getDefaultDisplay(),
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
															   getActivity().getWindowManager().getDefaultDisplay(),
															   jObj.getString(MMSDKConstants.JSON_KEY_THUMB_URL));
							} else {
								ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(imageMedia, MMUtility.getImageMediaMeasuredWidth(getActivity()), MMUtility.getImageMediaMeasuredHeight(getActivity())));
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
//								MMImageDownloaderAdapter.download(jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL), ivtnMedia, MMUtility.getImageMediaMeasuredWidth(getActivity()), MMUtility.getImageMediaMeasuredHeight(getActivity()), imageMedia);
//								retrieveImageMedia = false;
//								ivtnMedia.setOnClickListener(new MMImageOnClickListener(getActivity(), imageMedia));
								MMImageLoaderAdapter.loadImage(new LoadImageCallback(),
															   getActivity().getWindowManager().getDefaultDisplay(),
															   jObj.getString(MMSDKConstants.JSON_KEY_MEDIA_URL));
							} else {
								imageMedia = MMImageDownloaderAdapter.getBitmapFromCache(MMSDKConstants.JSON_KEY_MEDIA_URL);
								ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(imageMedia, MMUtility.getImageMediaMeasuredWidth(getActivity()), MMUtility.getImageMediaMeasuredHeight(getActivity())));
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
										   getString(R.string.pd_adding_to_favorites));
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
										   getString(R.string.pd_removing_from_favorites));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void promptDeleteLocationHotSpot() throws JSONException {
		String message = MMSDKConstants.DEFAULT_STRING_EMPTY;
		
		if(isLocation) {
			message = getString(R.string.ad_message_delete_location) + location.getString(MMSDKConstants.JSON_KEY_NAME) + getString(R.string.ad_message_delete_location_question_mark);
		} else {
			message = getString(R.string.ad_message_delete_hot_spot) + location.getString(MMSDKConstants.JSON_KEY_NAME) + getString(R.string.ad_message_delete_hot_spot_question_mark);
		}
		
		AlertDialog confirmDeleteAlert = new AlertDialog.Builder(getActivity())
			.setTitle(R.string.ad_title_confirm_delete)
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton(R.string.ad_btn_delete, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						if(isLocation) {
							MMLocationAdapter.deleteLocation(new DeleteLocationCallback(),
															 location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID),
															 location.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID),
															 MMConstants.PARTNER_ID,
															 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
															 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
							MMProgressDialog.displayDialog(getActivity(),
														   MMSDKConstants.DEFAULT_STRING_EMPTY,
														   getString(R.string.pd_deleting_location));
						} else {
							MMLocationAdapter.deleteHotSpot(new DeleteHotSpotCallback(),
															location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID),
															location.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID),
															MMConstants.PARTNER_ID,
															userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
															userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
							MMProgressDialog.displayDialog(getActivity(),
														   MMSDKConstants.DEFAULT_STRING_EMPTY,
														   getString(R.string.pd_deleting_hot_spot));
						}
						Log.d(TAG, TAG + "user sign email: " + userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY) + " submitter email: " + location.getString(MMSDKConstants.JSON_KEY_SUBMITTER_EMAIL));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			})
			.setNegativeButton(R.string.ad_btn_cancel, null)
			.show();
//		TextView tvTitle = (TextView) confirmDeleteAlert.findViewById(android.R.id.title);
//		tvTitle.setGravity(Gravity.CENTER);
		TextView tvMessage = (TextView) confirmDeleteAlert.findViewById(android.R.id.message);
		tvMessage.setGravity(Gravity.CENTER);
		
		confirmDeleteAlert.show();
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
							
							Toast.makeText(getActivity(), locationInfo.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
							
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
					retrieveVideoMedia = false;
					imageMedia = (Bitmap) obj;
					ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(imageMedia, MMUtility.getImageMediaMeasuredWidth(getActivity()), MMUtility.getImageMediaMeasuredHeight(getActivity())));
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
					Display display = getActivity().getWindowManager().getDefaultDisplay();
					Log.d(TAG, TAG + "display width: " + display.getWidth());
					ivtnMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(imageMedia, MMUtility.getImageMediaMeasuredWidth(getActivity()), MMUtility.getImageMediaMeasuredHeight(getActivity())));
					ivtnMedia.setOnClickListener(new MMImageOnClickListener(getActivity(), Bitmap.createScaledBitmap(imageMedia, display.getWidth(), display.getHeight(), true)));
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
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject response = new JSONObject(((String) obj));
						if(response.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							Toast.makeText(getActivity(), R.string.toast_add_favorite_success, Toast.LENGTH_SHORT).show();
							locItems[1].setLocationDetail(getString(R.string.tv_remove_from_favorites));
							locArrayAdapter.notifyDataSetChanged();
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
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject response = new JSONObject(((String) obj));
						if(response.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							Toast.makeText(getActivity(), R.string.toast_remove_favorite_successs, Toast.LENGTH_SHORT).show();
							locItems[1].setLocationDetail(getString(R.string.tv_add_to_favorites));
							locArrayAdapter.notifyDataSetChanged();
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
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class DeleteLocationCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, TAG + "response: " + ((String) obj));
					
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							getActivity().onBackPressed();
						} else {
							
						}
						Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class DeleteHotSpotCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, TAG + "response: " + ((String) obj));
					
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							getActivity().onBackPressed();
//							deleteHotSpotFinishFragmentListener.onDeleteHotSpotFinish(location.getString(MMSDKConstants.JSON_KEY_LOCATION_ID), location.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID));
						} else {
							
						}
						Toast.makeText(getActivity(), jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION), Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}		
	}
}
