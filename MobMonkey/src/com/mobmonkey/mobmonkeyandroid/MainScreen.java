package com.mobmonkey.mobmonkeyandroid;

import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;
import com.mobmonkey.mobmonkeyandroid.fragments.AddNotificationsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.AnsweredRequestsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.AssignedRequestsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.CategoriesFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.ExistingHotSpotsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.FavoritesFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.HistoryFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.InboxFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.LocationDetailsMapFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.MyInfoFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.MyInterestsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.NewHotSpotFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.OpenRequestsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SearchLocationsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SearchResultsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SettingsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SocialNetworksFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.TopViewedFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.TrendingNowFragment;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnAddNotificationsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnAddressFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCategoryResultsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnCreateHotSpotFragmentClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnDeleteHotSpotFragmentFinishListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnExistingHotSpotsFragmentCreateHotSpotClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnExistingHotSpotsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentFinishListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentMultipleBackListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnHistoryFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnInboxFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnMapIconFragmentClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnMasterLocationNearbyLocationsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnMyInfoFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnMyInterestsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnNearbyLocationsItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSearchResultsFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnSocialNetworksFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnTrendingFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.listeners.OnLocationNameClickFragmentListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMBaseActivity;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMCategoryAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMCheckinAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMFavoritesAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMGCMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * Android {@link Activity} screen displays the signed in user portion of the
 * application with different tabs.
 * 
 * @author Dezapp, LLC
 * 
 */
public class MainScreen extends MMBaseActivity implements
		MMOnMyInfoFragmentItemClickListener,
		MMOnSocialNetworksFragmentItemClickListener,
		MMOnMyInterestsFragmentItemClickListener, MMOnFragmentFinishListener,
		MMOnTrendingFragmentItemClickListener,
		MMOnInboxFragmentItemClickListener,
		OnLocationNameClickFragmentListener, MMOnMapIconFragmentClickListener,
		MMOnSearchResultsFragmentItemClickListener,
		MMOnAddressFragmentItemClickListener,
		MMOnNearbyLocationsItemClickListener,
		MMOnCreateHotSpotFragmentClickListener,
		MMOnAddNotificationsFragmentItemClickListener,
		 MMOnHistoryFragmentItemClickListener,
		 MMOnCategoryFragmentItemClickListener,
		 MMOnMasterLocationNearbyLocationsFragmentItemClickListener,
		 MMOnCategoryResultsFragmentItemClickListener,
		 MMOnExistingHotSpotsFragmentItemClickListener,
		 MMOnExistingHotSpotsFragmentCreateHotSpotClickListener,
		 MMOnFragmentMultipleBackListener,
		 MMOnDeleteHotSpotFragmentFinishListener{
	protected static final String TAG = "MainScreen: ";

	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;

	private FragmentManager fragmentManager;

	private Stack<MMFragment> fragmentStack;

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					MMSDKConstants.KEY_INTENT_EXTRA_MESSAGE);
			Toast.makeText(MainScreen.this, newMessage, Toast.LENGTH_LONG)
					.show();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreate");
		setContentView(R.layout.main_screen);
		super.onCreate(savedInstanceState);
		checkForGPSLocation();

		fragmentStack = new Stack<MMFragment>();
		fragmentManager = getSupportFragmentManager();

	}


	// /*
	// * (non-Javadoc)
	// * @see android.app.Activity#onActivityResult(int, int,
	// android.content.Intent)
	// */
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// Log.d(TAG, TAG + ":onActivityResult");
	// super.onActivityResult(requestCode, resultCode, data);
	// if(requestCode == MMSDKConstants.REQUEST_CODE_TURN_ON_GPS_LOCATION) {
	// if(MMLocationManager.isGPSEnabled()) {
	// checkForGPSAccess();
	// } else {
	// noGPSEnabled();
	// }
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.d(TAG, TAG + "onDestroy");
		MMProgressDialog.dismissDialog();
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
	}

	/**
	 * Function that check if user's device has GPS access. Display a
	 * {@link Toast} message informing the user if there is no GPS access.
	 */
	private void checkForGPSLocation() {
		if (MMLocationManager.getGPSLocation() == null) {
			new AlertDialog.Builder(MainScreen.this)
					.setTitle(R.string.ad_title_no_location)
					.setMessage(R.string.ad_message_no_location)
					.setCancelable(false)
					.setNeutralButton(R.string.ad_btn_ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									init();
								}
							}).show();
		} else {
			init();
		}
	}

	/**
	 * Initialize all the variables to be used in {@link MainScreen}
	 */
	private void init() {
		Log.d(TAG, "init");
		userPrefs = getSharedPreferences(MMSDKConstants.USER_PREFS,
				MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		if (userPrefs.getBoolean(MMSDKConstants.KEY_USE_OAUTH, false)) {
			MMAdapter.useOAuth(MMConstants.PARTNER_ID, userPrefs.getString(
					MMSDKConstants.KEY_OAUTH_PROVIDER_USER_NAME,
					MMSDKConstants.DEFAULT_STRING_EMPTY), userPrefs.getString(
					MMSDKConstants.KEY_OAUTH_PROVIDER,
					MMSDKConstants.DEFAULT_STRING_EMPTY));
		} else {
			MMAdapter.useMobMonkey(MMConstants.PARTNER_ID, userPrefs.getString(
					MMSDKConstants.KEY_USER,
					MMSDKConstants.DEFAULT_STRING_EMPTY), userPrefs.getString(
					MMSDKConstants.KEY_AUTH,
					MMSDKConstants.DEFAULT_STRING_EMPTY));
		}

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				MMSDKConstants.INTENT_FILTER_DISPLAY_MESSAGE));
		registerGCM();

		// tabWidget = getTabWidget();
		// tabHost = getTabHost();

		getAllCategories();
		getAllFavorites();
		checkUserIn();
	}

	/**
	 * Function that registers the Google Cloud Message service
	 */
	private void registerGCM() {
		GCMRegistrar.checkDevice(MainScreen.this);
		GCMRegistrar.checkManifest(MainScreen.this);

		String regId = GCMRegistrar.getRegistrationId(MainScreen.this);
		Log.d(TAG, TAG + "regId: " + regId);
		if (regId.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			GCMRegistrar.register(MainScreen.this, GCMIntentService.SENDER_ID);
		} else {
			MMGCMAdapter.registerGCMRegId(
					new RegisterGCMWithMobMonkeyCallback(), MainScreen.this,
					regId);
		}
	}

/*	*//**
	 * Function that set the tabs and the corresponding {@link Activity} for the
	 * {@link TabHost}
	 *//*
	private void setTabs() {
		addTab(MMSDKConstants.TAB_TITLE_TRENDING_NOW,
				R.drawable.tab_trendingnow, TrendingNowActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_INBOX, R.drawable.tab_inbox,
				InboxActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_SEARCH, R.drawable.tab_search,
				SearchLocationsActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_FAVORITES, R.drawable.tab_favorites,
				FavoritesActivity.class);
		addTab(MMSDKConstants.TAB_TITLE_SETTINGS, R.drawable.tab_settings,
				SettingsActivity.class);
		tabHost.setCurrentTab(userPrefs.getInt(
				MMSDKConstants.TAB_TITLE_CURRENT_TAG, 0));
	}*/



	/**
	 * Function to get all the categories from the server
	 */
	private void getAllCategories() {
		if (!userPrefs.contains(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES)
				&& MMLocationManager.isGPSEnabled()
				&& MMLocationManager.getGPSLocation() != null) {
			MMCategoryAdapter.cancelGetAllCategories();
			MMCategoryAdapter.getAllCategories(new CategoriesCallback());
			if (MMProgressDialog.isProgressDialogNull()
					|| !MMProgressDialog.isProgressDialogShowing()) {
				MMProgressDialog.displayDialog(MainScreen.this,
						MMSDKConstants.DEFAULT_STRING_EMPTY,
						getString(R.string.pd_loading)
								+ getString(R.string.pd_ellipses));
			}
		}
	}

	/**
	 * Function to get all the user's favorites from the server NOTE: This is
	 * needed to check the location in location info
	 */
	private void getAllFavorites() {
		if (MMLocationManager.isGPSEnabled()
				&& MMLocationManager.getGPSLocation() != null) {
			MMFavoritesAdapter.cancelGetFavorites();
			MMFavoritesAdapter.getFavorites(new FavoritesCallback());
			if (MMProgressDialog.isProgressDialogNull()
					|| !MMProgressDialog.isProgressDialogShowing()) {
				MMProgressDialog.displayDialog(MainScreen.this,
						MMSDKConstants.DEFAULT_STRING_EMPTY,
						getString(R.string.pd_loading)
								+ getString(R.string.pd_ellipses));
			}
		}
	}

	/**
	 * Function to check user in at his/her current location when he/she signs
	 * in
	 */
	private void checkUserIn() {
		if (MMLocationManager.isGPSEnabled()
				&& MMLocationManager.getGPSLocation() != null) {
			MMCheckinAdapter.checkInUser(new CheckUserInCallback());
			if (MMProgressDialog.isProgressDialogNull()
					|| !MMProgressDialog.isProgressDialogShowing()) {
				MMProgressDialog.displayDialog(MainScreen.this,
						MMSDKConstants.DEFAULT_STRING_EMPTY,
						getString(R.string.pd_loading)
								+ getString(R.string.pd_ellipses));
			}
		} else {
			// setTabs();
			selectFragmentFromDrawer(0); // Go to trending screen
		}
	}

	/**
	 * Callback to handle the response after register with MobMonkey with GCM
	 * regId
	 * 
	 * @author Dezapp, LLC
	 * 
	 */
	private class RegisterGCMWithMobMonkeyCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if (obj != null) {
				Log.d(TAG, TAG + "RegisterGCMWithMobMobnkeyCallback response: "
						+ (String) obj);

				if (((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this,
							getString(R.string.toast_connection_timed_out),
							Toast.LENGTH_SHORT).show();
				} else {
					// TODO:
				}
			}
		}
	}

	/**
	 * Callback that gets all the categories
	 * 
	 * @author Dezapp, LLC
	 * 
	 */
	private class CategoriesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if (obj != null) {
				Log.d(TAG, TAG + "CategoriesCallback: " + ((String) obj));

				if (((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this,
							getString(R.string.toast_connection_timed_out),
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if (!jObj.has(MMSDKConstants.JSON_KEY_STATUS)) {
							userPrefsEditor
									.putString(
											MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES,
											(String) obj);
						}
						userPrefsEditor.commit();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Callback to update the user's favorites list in app data after making get
	 * favorites call to the server
	 * 
	 * @author Dezapp, LLC
	 * 
	 */
	private class FavoritesCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if (obj != null) {
				Log.d(TAG, TAG + "FavoritesCallback: " + ((String) obj));
				if (((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this,
							getString(R.string.toast_connection_timed_out),
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						userPrefsEditor.putString(
								MMSDKConstants.SHARED_PREFS_KEY_FAVORITES,
								(String) obj);
						userPrefsEditor.commit();

						JSONObject jObj = new JSONObject((String) obj);
						if (jObj.has(MMSDKConstants.JSON_KEY_STATUS)) {
							Toast.makeText(
									MainScreen.this,
									jObj.getString(MMSDKConstants.JSON_KEY_DESCRIPTION),
									Toast.LENGTH_LONG).show();
							userPrefsEditor
									.remove(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES);
							userPrefsEditor
									.remove(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES);
							userPrefsEditor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			// setTabs();
		}
	}

	/**
	 * 
	 * @author Dezapp, LLC
	 * 
	 */
	private class CheckUserInCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if (obj != null) {
				if (((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(MainScreen.this,
							getString(R.string.toast_connection_timed_out),
							Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, TAG + "checkinuser response: " + (String) obj);
					try {
						JSONObject jObj = new JSONObject((String) obj);
						if (jObj.getString(MMSDKConstants.JSON_KEY_STATUS)
								.equals(MMSDKConstants.RESPONSE_STATUS_UNAUTHORIZED_EMAIL)) {
							userPrefsEditor
									.remove(MMSDKConstants.SHARED_PREFS_KEY_ALL_CATEGORIES);
							userPrefsEditor
									.remove(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES);
							userPrefsEditor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					// setTabs();
				}
			}
		}
	}

	/*********************** Settings Methods *********************/
	@Override
	public void onFragmentFinish() {

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentStack.pop();
		fragmentTransaction.setCustomAnimations(R.anim.slide_left_in,
				R.anim.slide_right_out);
		fragmentTransaction.replace(R.id.content_frame, fragmentStack.peek());
		fragmentTransaction.commit();

	}

	@Override
	public void onMyInterestsFragmentItemClick(JSONArray myInterests,
			boolean isTopLevel) {
		MyInterestsFragment myInterestsFragment = new MyInterestsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_INTERESTS,
				myInterests.toString());
		data.putBoolean(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, isTopLevel);
		myInterestsFragment.setArguments(data);
		performTransaction(myInterestsFragment);

	}

	@Override
	public void onSocialNetworksItemClick() {
		performTransaction(new SocialNetworksFragment());

	}

	@Override
	public void onMyInfoFragmentItemClick() {
		performTransaction(new MyInfoFragment());

	}

	/****************** Trending **********************/

	@Override
	public void onTrendingFragmentItemClick(int position) {
		MMFragment mmFragment = null;

		switch (position) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			mmFragment = new TopViewedFragment();
			break;
		case 3:
			break;
		}

		performTransaction(mmFragment);

	}

	/**
	 * Handler when back button is pressed, it will not close and destroy the
	 * current {@link Activity} but instead it will remain on the current
	 * {@link Activity}
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

		if (fragmentStack.size() > 1) {
			MMFragment mmFragment = fragmentStack.peek();
			mmFragment.onFragmentBackPressed();
		}

		moveTaskToBack(true);
		return;
	}

	/******************* Inbox ****************************/

	@Override
	public void locationNameClick(JSONObject obj) {

		Log.d(TAG, obj.toString());
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS,
				obj.toString());
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);

	}

	@Override
	public void onInboxFragmentItemClick(int position) {

		MMFragment mmFragment = null;
		String actionBarTitle = null;
		switch (position) {
		case 0:
			mmFragment = new OpenRequestsFragment();
			actionBarTitle = getResources().getString(
					R.string.tv_title_openrequests);
			break;
		case 1:
			mmFragment = new AnsweredRequestsFragment();
			actionBarTitle = getResources().getString(
					R.string.tv_title_answeredrequests);

			break;
		case 2:
			mmFragment = new AssignedRequestsFragment();
			actionBarTitle = getResources().getString(
					R.string.tv_title_assignedrequests);

			break;
		case 3:
			// mmFragment = new NotificationsFragment();
			break;
		}

		// Bundle data = new Bundle();
		// data.putString(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS,
		// requests);
		// mmFragment.setArguments(data);
		performTransaction(mmFragment);
		getSupportActionBar().setTitle(actionBarTitle);

	}

	/***************Favorites*******************/
	
	@Override
	public void onAddNotificationsFragmentItemClick(JSONObject jObj) {
		AddNotificationsFragment addNotificationsFragment = new AddNotificationsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		addNotificationsFragment.setArguments(data);
		performTransaction(addNotificationsFragment);
		
	}

	@Override
	public void onCreateHotSpotClick(JSONArray jArr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateHotSpotClick(JSONObject jObj, int requestCode) {
		NewHotSpotFragment newHotSpotFragment = new NewHotSpotFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION, jObj.toString());
		data.putInt(MMSDKConstants.KEY_INTENT_EXTRA_REQUEST_CODE, requestCode);
		newHotSpotFragment.setArguments(data);
		performTransaction(newHotSpotFragment);
		
	}

	@Override
	public void onNearbyLocationsItemClick(String location) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, location);
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
		
	}

	@Override
	public void onAddressFragmentItemClick(JSONObject jObj) {
		LocationDetailsMapFragment locationsDetailsMapFragment = new LocationDetailsMapFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		locationsDetailsMapFragment.setArguments(data);
		performTransaction(locationsDetailsMapFragment);
	
	}

	@Override
	public void onSearchResultsFragmentItemClick(String locationInfo) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, locationInfo);
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);
		
	}

	@Override
	public void onMapIconFragmentClick(int which) {
		if(which == MMSDKConstants.FAVORITES_FRAGMENT_MAP) {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			fragmentTransaction.replace(R.id.llfragmentcontainer,fragmentStack.push(new FavoritesMapFragment()));
			fragmentTransaction.commit();
		} else if(which == MMSDKConstants.FAVORITES_FRAGMENT_LIST) {		
			fragmentManager.beginTransaction().remove(fragmentStack.pop());
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
		
	}

	/**********************Search Locations*************************/
	
	@Override
	public void onDeleteHotSpotFinish(String locationId, String providerId) {
		if(fragmentStack.size() > 1) {
			MMFragment mmFragment = fragmentStack.pop();
			
			mmFragment.onFragmentBackPressed();
			
			mmFragment = fragmentStack.peek();
			Bundle data = new Bundle();
			
			
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);			
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
		
	}

	@Override
	public void onFragmentMultipleBack() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		while(fragmentStack.size() > 1) {
			Log.d(TAG, TAG + "fragment: " + fragmentStack.peek());
			fragmentTransaction.remove(fragmentStack.pop());
			if(fragmentStack.peek() instanceof SearchLocationsFragment) {
				fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
				fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
				fragmentTransaction.commit();
			}
		}		
	}

	@Override
	public void onExistingHotSpotsCreateHotSpotClick(JSONObject jObj,
			int requestCode) {
		NewHotSpotFragment newHotSpotFragment = new NewHotSpotFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION, jObj.toString());
		data.putInt(MMSDKConstants.KEY_INTENT_EXTRA_REQUEST_CODE, requestCode);
		newHotSpotFragment.setArguments(data);
		performTransaction(newHotSpotFragment);		
	}

	@Override
	public void onExistingHotSpotsItemClick(JSONObject jObj) {
		LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS, jObj.toString());
		locationDetailsFragment.setArguments(data);
		performTransaction(locationDetailsFragment);		
	}

	@Override
	public void onCategoriesResultsFragmentItemClick(String searchCategory,
			String results) {
		SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE, searchCategory);
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_SEARCH_RESULTS, results);
		searchResultsFragment.setArguments(data);
		performTransaction(searchResultsFragment);		
	}

	@Override
	public void onMasterLocationNearbyLocationsItemClick(JSONObject jObj,
			int requestCode) {
		MMFragment mmFragment = null;
		Bundle data = new Bundle();
		if(jObj.isNull(MMSDKConstants.JSON_KEY_SUB_LOCATIONS)) {
			mmFragment = new NewHotSpotFragment();
			data.putString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION, jObj.toString());
			data.putInt(MMSDKConstants.REQUEST_CODE, requestCode);
		} else {
			mmFragment = new ExistingHotSpotsFragment();
			data.putString(MMSDKConstants.KEY_INTENT_EXTRA_EXISTING_HOT_SPOTS, jObj.toString());
		}
		
		mmFragment.setArguments(data);
		performTransaction(mmFragment);		
	}

	@Override
	public void onCategoryFragmentItemClick(String selectedCategory,
			JSONArray subCategories, boolean isTopLevel) {
		CategoriesFragment categoriesFragment = new CategoriesFragment();
		Bundle data = new Bundle();
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORY_TITLE, selectedCategory);
		data.putString(MMSDKConstants.KEY_INTENT_EXTRA_CATEGORIES, subCategories.toString());
		data.putBoolean(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, isTopLevel);
		categoriesFragment.setArguments(data);
		performTransaction(categoriesFragment);		
	}

	@Override
	public void onHistoryItemClick() {
		performTransaction(new HistoryFragment());
		
	}


	@Override
	protected Context getActivityContext() {
		return MainScreen.this;
	}

}
