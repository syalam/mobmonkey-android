package com.mobmonkey.mobmonkey;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
	}
	
	private void getLocation() {
		if(addLocClicked) {
			Toast.makeText(AddLocationMapScreen.this, "Tap on the location you'd like to add on the map", Toast.LENGTH_LONG).show();
			btnAddLoc.setText(R.string.ad_btn_cancel);
			addLocClicked = false;
			// TODO: start up something to listen for user click on x,y pixel
		} else {
//			btnAddLoc.setBackgroundResource(R.id.title_bar_icon_plus);
			addLocClicked = true;
		}
	}
}
