package com.mobmonkey.mobmonkeyandroid;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mobmonkey.mobmonkeyandroid.R;
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
	private Location location;
	
	private Button btnAddLoc;
	private Button btnPlus;
	private boolean addLocClicked;
	
	private SupportMapFragment smfLocation;
	private GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_hold);
		setContentView(R.layout.add_location_map_screen);
		
		init(); 
	}
	
	@Override
	public void onMapClick(LatLng pointClicked) {
		MMGeocoderAdapter.getFromLocation(AddLocationMapScreen.this, new ReverseGeocodeCallback(), pointClicked.latitude, pointClicked.longitude);
		MMProgressDialog.displayDialog(AddLocationMapScreen.this, MMSDKConstants.DEFAULT_STRING_EMPTY, getString(R.string.pd_retrieving_location_information));
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_bottom_out);
	}

	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnaddloc:
				getLocation();
				break;
			case R.id.btnplus:
				getLocation();
				break;
		}
	}
	
	private void init() {
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		btnAddLoc = (Button) findViewById(R.id.btnaddloc);
		btnPlus = (Button) findViewById(R.id.btnplus);
		addLocClicked = true;
		
		smfLocation = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmap);
		googleMap = smfLocation.getMap();
		
		setUpGoogleMap();
	}
	
	private void setUpGoogleMap() {
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMapClickListener(AddLocationMapScreen.this);
	}
	
	private void getLocation() {
		if(addLocClicked) {
			Toast.makeText(AddLocationMapScreen.this, R.string.toast_tap_location_to_add, Toast.LENGTH_LONG).show();
			btnAddLoc.setVisibility(View.VISIBLE);
			btnPlus.setVisibility(View.INVISIBLE);
			addLocClicked = false;
		} else {
			btnAddLoc.setVisibility(View.INVISIBLE);
			btnPlus.setVisibility(View.VISIBLE);
			addLocClicked = true;
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
				Address locationClicked = (Address) obj;
				
				Bundle bundle = new Bundle();
				bundle.putString(MMSDKConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(0));
				bundle.putString(MMSDKConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
				bundle.putString(MMSDKConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
				bundle.putString(MMSDKConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
				bundle.putDouble(MMSDKConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude());
				bundle.putDouble(MMSDKConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude());
				
				Intent intent = new Intent(AddLocationMapScreen.this, AddLocationScreen.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}
}
