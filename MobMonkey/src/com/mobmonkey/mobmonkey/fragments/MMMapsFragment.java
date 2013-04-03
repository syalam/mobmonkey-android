package com.mobmonkey.mobmonkey.fragments;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMapsFragment extends SupportMapFragment implements OnInfoWindowClickListener {
	private static final String TAG = "MMMapsFragment: ";
	
	private GoogleMap googleMap;
	private SupportMapFragment smfFavoriteLocations;
	private HashMap<Marker, JSONObject> markerHashMap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStates) {
		smfFavoriteLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmap);
		
		googleMap = smfFavoriteLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();
		
		smfFavoriteLocations = (SupportMapFragment) getFragmentManager().findFragmentByTag("map");		
		
		if(smfFavoriteLocations == null) {
			smfFavoriteLocations = SupportMapFragment.newInstance();
			
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.add(android.R.id.content, smfFavoriteLocations, "map");
			fragmentTransaction.commit();
			
			smfFavoriteLocations.onCreate(savedInstanceStates);
		}
		
		return super.onCreateView(inflater, container, savedInstanceStates);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		locationSelectedListener.onLocationSelected(markerHashMap.get((Marker) marker));
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, TAG + "onDestroyView");
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap() throws JSONException {
		markerHashMap.clear();
		googleMap.clear();
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE));
			
			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
					position(resultLocLatLng).
					title(jObj.getString(MMAPIConstants.JSON_KEY_NAME))
					.snippet(jObj.getString(MMAPIConstants.JSON_KEY_ADDRESS)));
			
			markerHashMap.put(locationResultMarker, jObj);
		}
		
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		googleMap.setOnInfoWindowClickListener(MMMapsFragment.this);
		googleMap.setMyLocationEnabled(true);
	}
	
	/**
	 * 
	 * @author Dezapp, LLC
	 *
	 */
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
       private final View mWindow;
       private final View mContents;

       public CustomInfoWindowAdapter() {
           mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
           mContents = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
       }

       @Override
       public View getInfoWindow(Marker marker) {
           render(marker, mWindow);
           return mWindow;
       }

       @Override
       public View getInfoContents(Marker marker) {
           render(marker, mContents);
           return mContents;
       }

       private void render(Marker marker, View view) {
           String title = marker.getTitle();
           TextView titleUi = ((TextView) view.findViewById(R.id.title));
           if (title != null) {
               titleUi.setText(title);
           } else {
               titleUi.setText(MMAPIConstants.DEFAULT_STRING);
           }

           String snippet = marker.getSnippet();
           TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
           if (snippet != null) {
               snippetUi.setText(snippet);
           } else {
               snippetUi.setText(MMAPIConstants.DEFAULT_STRING);
           }
       }
   }
}
