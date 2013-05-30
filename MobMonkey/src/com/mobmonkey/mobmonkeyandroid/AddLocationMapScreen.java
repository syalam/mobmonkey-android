package com.mobmonkey.mobmonkeyandroid;

import org.json.JSONException;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMSupportMapFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMGeocoderAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class AddLocationMapScreen extends FragmentActivity implements OnMapClickListener {
	FragmentManager fragmentManager;
	
	private Button btnCancel;
	private Button btnPlus;
	private boolean addLocClicked;
	
	private SupportMapFragment smfLocation;
	private GoogleMap googleMap;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.add_location_map_screen);
		
		init(); 
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_ADD_LOCATION) {
			if(resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
				overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.OnMapClickListener#onMapClick(com.google.android.gms.maps.model.LatLng)
	 */
	@Override
	public void onMapClick(LatLng pointClicked) {
		if(addLocClicked) {
			MMGeocoderAdapter.getFromLocation(AddLocationMapScreen.this,
											  new ReverseGeocodeCallback(),
											  pointClicked.latitude,
											  pointClicked.longitude);
			MMProgressDialog.displayDialog(AddLocationMapScreen.this,
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_retrieving_location_information));
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getMMSupportMapFragment();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.remove(smfLocation);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}

	/**
	 * 
	 * @param view
	 */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btncancel:
				getLocation();
				break;
			case R.id.btnplus:
				getLocation();
				break;
		}
	}
	
	/**
	 * 
	 */
	private void init() {
		fragmentManager = getSupportFragmentManager();
		btnCancel = (Button) findViewById(R.id.btncancel);
		btnPlus = (Button) findViewById(R.id.btnplus);
		addLocClicked = false;
	}
	
	/**
	 * @throws JSONException 
	 * 
	 */
	private void getMMSupportMapFragment() {
		smfLocation = (MMSupportMapFragment) fragmentManager.findFragmentByTag(MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
		if(smfLocation == null) {
			smfLocation = new MMSupportMapFragment() {
				
				/* (non-Javadoc)
				 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
				 */
				@Override
				public void onActivityCreated(Bundle savedInstanceState) {
					super.onActivityCreated(savedInstanceState);
					googleMap = smfLocation.getMap();
					if(googleMap != null) {
						setUpGoogleMap();
					}
				}
			};
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.lladdlocationmap, smfLocation, MMSDKConstants.MMSUPPORT_MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
	}
	
	/**
	 * 
	 */
	private void setUpGoogleMap() {
		LatLng currentLoc = new LatLng(MMLocationManager.getLocationLatitude(), MMLocationManager.getLocationLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));
		googleMap.setOnMapClickListener(AddLocationMapScreen.this);
		googleMap.setMyLocationEnabled(true);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
	}
	
	/**
	 * 
	 */
	private void getLocation() {
		if(!addLocClicked) {
			Toast.makeText(AddLocationMapScreen.this, R.string.toast_tap_location_to_add, Toast.LENGTH_LONG).show();
			btnCancel.setVisibility(View.VISIBLE);
			btnPlus.setVisibility(View.INVISIBLE);
			addLocClicked = true;
		} else {
			btnCancel.setVisibility(View.INVISIBLE);
			btnPlus.setVisibility(View.VISIBLE);
			addLocClicked = false;
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
						Toast.makeText(AddLocationMapScreen.this, getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
					} else if(((String) obj).equals(MMSDKConstants.SERVICE_NOT_AVAILABLE)) {
						Toast.makeText(AddLocationMapScreen.this, R.string.toast_service_not_available, Toast.LENGTH_LONG).show();
					}
				} else if(obj instanceof Address) {
					Address locationClicked = (Address) obj;
					
					Intent intent = new Intent(AddLocationMapScreen.this, AddLocationScreen.class);
					intent.putExtra(MMSDKConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(MMSDKConstants.DEFAULT_INT_ZERO));
					intent.putExtra(MMSDKConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
					intent.putExtra(MMSDKConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
					intent.putExtra(MMSDKConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
					intent.putExtra(MMSDKConstants.JSON_KEY_COUNTRY_CODE, locationClicked.getCountryCode());
					intent.putExtra(MMSDKConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude());
					intent.putExtra(MMSDKConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude());
					startActivityForResult(intent, MMSDKConstants.REQUEST_CODE_ADD_LOCATION);
				}
			}
		}
	}
}
