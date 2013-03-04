package com.mobmonkey.mobmonkey;

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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class AddLocationMapScreen extends FragmentActivity {
	Location location;
	
	Button btnAddLoc;
	boolean addLocClicked;
	
	SupportMapFragment smfLocation;
	GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_location_map_screen);
		
		init(); 
	}
	
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnaddloc:
				getLocation();
				break;
		}
	}
	
	private void init() {
		location = getIntent().getParcelableExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION);
		btnAddLoc = (Button) findViewById(R.id.btnaddloc);
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
				if(!addLocClicked)
				{
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
			Toast.makeText(AddLocationMapScreen.this, "Tap on the location you'd like to add on the map", Toast.LENGTH_LONG).show();
			btnAddLoc.setText(R.string.ad_btn_cancel);
			addLocClicked = false;
		} else {
			btnAddLoc.setText("");
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
