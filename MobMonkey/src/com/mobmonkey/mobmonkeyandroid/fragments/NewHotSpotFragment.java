package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyandroid.HotSpotRangeActionSheet;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentMultipleBackListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSupportMapFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMGeocoderAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMHotSpotAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class NewHotSpotFragment extends MMFragment implements OnMapClickListener,
															  OnTouchListener,
															  OnKeyListener,
															  OnClickListener {
	private static final String TAG = "NewHotSpotFragment: ";
	
	private SharedPreferences userPrefs;
	private FragmentManager fragmentManager;
	private InputMethodManager inputMethodManager;
		
	private JSONObject locationInfo;
	private int requestCode;
	
	private ScrollView svHotSpotDetails;
	private MMSupportMapFragment smfNewHotSpot;
	private EditText etName;
	private EditText etDescription;
	private EditText etRange;
	private Button btnCreateHotSpot;
	private GoogleMap googleMap;
	private Marker newHotSpotMarker;
	
	private Address locationClicked;
	
	private MMOnFragmentMultipleBackListener fragmentMultipleBackListener;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
    	fragmentManager = getFragmentManager();
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		requestCode = getArguments().getInt(MMSDKConstants.REQUEST_CODE);
		
		View view = inflater.inflate(R.layout.fragment_new_hot_spot, container, false);
		svHotSpotDetails = (ScrollView) view.findViewById(R.id.svhotspotdetails);
		etName = (EditText) view.findViewById(R.id.etname);
		etDescription = (EditText) view.findViewById(R.id.etdescription);
		etRange = (EditText) view.findViewById(R.id.etrange);
		btnCreateHotSpot = (Button) view.findViewById(R.id.btncreatehotspot);
		
		etName.setOnClickListener(NewHotSpotFragment.this);
		etDescription.setOnKeyListener(NewHotSpotFragment.this);
		etRange.setOnTouchListener(NewHotSpotFragment.this);
		btnCreateHotSpot.setOnClickListener(NewHotSpotFragment.this);
		
		etName.setText("Wilson Hot Spot");
		etRange.setText("50 meters");
		
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnFragmentMultipleBackListener) {
			fragmentMultipleBackListener = (MMOnFragmentMultipleBackListener) activity;
		}
	}

	/* (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnMapClickListener#onMapClick(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void onMapClick(LatLng pointClicked) {
		MMGeocoderAdapter.getFromLocation(getActivity(),
										  new ReverseGeocodeCallback(),
										  pointClicked.latitude,
										  pointClicked.longitude);
		MMProgressDialog.displayDialog(getActivity(),
									   MMSDKConstants.DEFAULT_STRING_EMPTY,
									   getString(R.string.pd_retrieving_location_information));
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP) {
			switch(v.getId()) {
				case R.id.etrange:
					Intent intent = new Intent(getActivity(), HotSpotRangeActionSheet.class);
					startActivityForResult(intent, MMSDKConstants.REQUEST_CODE_RANGE);
					return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
			inputMethodManager.hideSoftInputFromWindow(etDescription.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btncreatehotspot:
				try {
					checkFields();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_RANGE) {
			if(resultCode == Activity.RESULT_OK) {
				etRange.setText(data.getIntExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.DEFAULT_INT) + MMSDKConstants.DEFAULT_STRING_SPACE + getString(R.string.et_text_meters));
				Log.d(TAG, TAG + "range: " + data.getIntExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.DEFAULT_INT));
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		getMMSupportMapFragment();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.remove(smfNewHotSpot);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void getMMSupportMapFragment() {
		smfNewHotSpot = (MMSupportMapFragment) fragmentManager.findFragmentByTag(MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
		if(smfNewHotSpot == null) {
			smfNewHotSpot = new MMSupportMapFragment() {
				/* (non-Javadoc)
				 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
				 */
				@Override
				public void onActivityCreated(Bundle savedInstanceState) {
					super.onActivityCreated(savedInstanceState);
					googleMap = smfNewHotSpot.getMap();
					if(googleMap != null) {
						addToGoogleMap();
					}
				}
			};
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.flnewhotspotmap, smfNewHotSpot, MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
	}
	
	/**
	 * 
	 */
	private void addToGoogleMap() {
		try {
			locationInfo = new JSONObject(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION));
			LatLng locLatLng = new LatLng(locationInfo.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), locationInfo.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE));
			
			googleMap.addMarker(new MarkerOptions()
				.position(locLatLng)
				.title(locationInfo.getString(MMSDKConstants.JSON_KEY_NAME))
				.snippet(locationInfo.getString(MMSDKConstants.JSON_KEY_ADDRESS))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locLatLng, 17));
			googleMap.setMyLocationEnabled(true);
			googleMap.setOnMapClickListener(NewHotSpotFragment.this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void checkFields() throws JSONException {
		if(checkName()) {
			MMHotSpotAdapter.createSubLocationWithLocationInfo(new CreateHotSpotCallback(),
															   etName.getText().toString(),
															   etDescription.getText().toString(),
															   etRange.getText().toString(),
															   MMSDKConstants.DEFAULT_STRING_EMPTY,
															   getLatitude(),
															   getLongitude(),
															   locationInfo.getString(MMSDKConstants.JSON_KEY_CATEGORY_IDS),
															   locationInfo.getString(MMSDKConstants.JSON_KEY_COUNTRY_CODE).toLowerCase(),
															   locationInfo.getString(MMSDKConstants.JSON_KEY_LOCALITY),
															   locationInfo.getString(MMSDKConstants.JSON_KEY_PHONE_NUMBER),
															   MMConstants.PROVIDER_ID,
															   locationInfo.getString(MMSDKConstants.JSON_KEY_REGION),
															   locationInfo.getString(MMSDKConstants.JSON_KEY_WEBSITE),
															   locationInfo.getString(MMSDKConstants.JSON_KEY_LOCATION_ID),
															   locationInfo.getString(MMSDKConstants.JSON_KEY_PROVIDER_ID),
															   MMConstants.PARTNER_ID,
															   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
															   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_creating_hot_spot));
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkName() {
		if(!TextUtils.isEmpty(etName.getText().toString())) {
			return checkRange();
		} else {
			displayAlert(R.string.ad_message_empty_name);
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkRange() {
		if(!TextUtils.isEmpty(etRange.getText().toString())) {
			return true;
		} else {
			displayAlert(R.string.ad_message_empty_range);
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	private double getLatitude() throws JSONException {
		if(locationClicked != null) {
			return locationClicked.getLatitude();
		} else {
			return locationInfo.getDouble(MMSDKConstants.JSON_KEY_LATITUDE);
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws JSONException
	 */
	private double getLongitude() throws JSONException {
		if(locationClicked != null) {
			return locationClicked.getLongitude();
		} else {
			return locationInfo.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE);
		}
	}
	
    /**
     * 
     * @param messageId
     */
    private void displayAlert(int messageId) {
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.app_name)
			.setMessage(messageId)
			.setNeutralButton(android.R.string.ok, null)
			.show();
	}
	
    /**
     * 
     */
    private void updateHotSpotMarker() {
		LatLng newLocLatLng = new LatLng(locationClicked.getLatitude(), locationClicked.getLongitude());
		if(newHotSpotMarker != null) {
			newHotSpotMarker.remove();
		}
		
		newHotSpotMarker = googleMap.addMarker(new MarkerOptions()
			.position(newLocLatLng)
			.title(getString(R.string.tv_new_hot_spot)));
		
		
		newHotSpotMarker.showInfoWindow();
    }
    
    /**
     * 
     * @param result
     */
    private void handleCreateHotSpotCallback(String result) {
    	try {
	    	JSONObject jObj = new JSONObject(result);
	    	if(!jObj.has(MMSDKConstants.JSON_KEY_STATUS)) {
				if(requestCode == MMSDKConstants.REQUEST_CODE_MASTER_LOCATION || requestCode == MMSDKConstants.REQUEST_CODE_EXISTING_HOT_SPOTS) {
					fragmentMultipleBackListener.onFragmentMultipleBack();
				} else if(requestCode == MMSDKConstants.REQUEST_CODE_LOCATION_DETAILS){
					getActivity().onBackPressed();
				}
	    	} else {
	    		Toast.makeText(getActivity(), R.string.toast_error_create_hot_spot, Toast.LENGTH_SHORT).show();
	    	}
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    }
    
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class ReverseGeocodeCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();

			if(obj != null) {
				if(obj instanceof String) {
					if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
						Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					}
				} else if(obj instanceof Address) {
					locationClicked = (Address) obj;
					Log.d(TAG, TAG + "address: " + locationClicked.toString());
					updateHotSpotMarker();
				}
			}
		}
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class CreateHotSpotCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, TAG + "hotspot response: " + (String) obj);
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					handleCreateHotSpotCallback((String) obj);
				}
			}
		}
	}
}
