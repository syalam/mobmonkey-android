package com.mobmonkey.mobmonkey;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchLocationResultMapScreen extends FragmentActivity {
	private static final String TAG = "SearchLocationResultMapScreen: ";
	
	GoogleMap googleMap;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_location_result_map_screen);
		
		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmap)).getMap();
		
		JSONObject jObj;
		try {
			jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));			
			
			Location location = MMLocationManager.getGPSLocation(new MMLocationListener());
			
			LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE));
			
			googleMap.addMarker(new MarkerOptions().
				position(resultLocLatLng).
				title(jObj.getString(MMAPIConstants.JSON_KEY_NAME))
				.snippet(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS)));
			
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					Log.d(TAG, TAG + "marker clicked!");
					
					return false;
				}
			});
			
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
			googleMap.setMyLocationEnabled(true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
