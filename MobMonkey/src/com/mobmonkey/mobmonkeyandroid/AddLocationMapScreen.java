package com.mobmonkey.mobmonkeyandroid;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class AddLocationMapScreen extends FragmentActivity {
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
		googleMap.setOnMapClickListener(new OnMapClickListener(){
			@Override
			public void onMapClick(LatLng pointClicked) {
				if(!addLocClicked) {
					try{
						Address locationClicked = getAddressForLocation(AddLocationMapScreen.this, pointClicked.latitude, pointClicked.longitude);
						Toast.makeText(AddLocationMapScreen.this, "Address: "+locationClicked.getAddressLine(0), Toast.LENGTH_SHORT).show();
						
						// pass information to category screen
						Bundle bundle = new Bundle();
						bundle.putString(MMAPIConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(0));
						bundle.putString(MMAPIConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
						bundle.putString(MMAPIConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
						bundle.putString(MMAPIConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
						bundle.putString(MMAPIConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude()+"");
						bundle.putString(MMAPIConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude()+"");
						
						Intent intent = new Intent(AddLocationMapScreen.this, AddLocationScreen.class);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
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
	
	public Address getAddressForLocation(Context context, double latitude, double longitude) throws IOException {
        int maxResults = 1;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
    }
}
