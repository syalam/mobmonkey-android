package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.AddLocationScreen;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class FavoritesMapFragment extends MMFragment implements OnClickListener, 
																OnInfoWindowClickListener, 
																OnMapClickListener {
	private static final String TAG = "MMMapsFragment: ";
	
	private SharedPreferences userPrefs;
	private Location location;
	
	private ImageButton ibMap;
	private Button btnAddLoc;
	private Button btnCancel;
	private SupportMapFragment smfFavoriteLocations;
	
	private JSONArray favoritesList;
	private GoogleMap googleMap;
	private HashMap<Marker, JSONObject> markerHashMap;
	private boolean addLocClicked;
	private Marker currMarker;
	private float currZoomLevel = 16;
	
	private MMOnMapIconFragmentClickListener mapIconClickListener;
	private MMOnSearchResultsFragmentItemClickListener locationSelectListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStates) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		View view = inflater.inflate(R.layout.fragment_favorites_map, container, false);
		ibMap = (ImageButton) view.findViewById(R.id.ibmap);
		btnAddLoc = (Button) view.findViewById(R.id.btnaddloc);
		btnCancel = (Button) view.findViewById(R.id.btncancel);
		smfFavoriteLocations = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmap);
		
		googleMap = smfFavoriteLocations.getMap();
		markerHashMap = new HashMap<Marker, JSONObject>();		
		addLocClicked = false;
		
		ibMap.setOnClickListener(FavoritesMapFragment.this);
		btnAddLoc.setOnClickListener(FavoritesMapFragment.this);
		btnCancel.setOnClickListener(FavoritesMapFragment.this);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnMapIconFragmentClickListener) {
			mapIconClickListener = (MMOnMapIconFragmentClickListener) activity;
			if(activity instanceof MMOnSearchResultsFragmentItemClickListener) {
				locationSelectListener = (MMOnSearchResultsFragmentItemClickListener) activity;
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.ibmap:
				if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
					mapIconClickListener.onMapIconFragmentClick(MMSDKConstants.FAVORITES_FRAGMENT_LIST);
				}
				break;
			case R.id.btnaddloc:
				if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
					Toast.makeText(getActivity(), R.string.toast_tap_location_to_add, Toast.LENGTH_LONG).show();
					addLocClicked = true;
					btnAddLoc.setVisibility(View.INVISIBLE);
					btnCancel.setVisibility(View.VISIBLE);
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ibMap.getLayoutParams();
					params.addRule(RelativeLayout.LEFT_OF, R.id.btncancel);
					ibMap.setLayoutParams(params);
				}
				break;
			case R.id.btncancel:
				addLocClicked = false;
				btnAddLoc.setVisibility(View.VISIBLE);
				btnCancel.setVisibility(View.INVISIBLE);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ibMap.getLayoutParams();
				params.addRule(RelativeLayout.LEFT_OF, R.id.btnaddloc);
				ibMap.setLayoutParams(params);
				break;
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		currMarker = marker;
		currZoomLevel = googleMap.getCameraPosition().zoom;
		locationSelectListener.onSearchResultsFragmentItemClick(markerHashMap.get((Marker) marker));
	}

	@Override
	public void onMapClick(LatLng pointClicked) {
		if(addLocClicked) {
			try{
				Address locationClicked = getAddressForLocation(pointClicked.latitude, pointClicked.longitude);
//				Toast.makeText(getActivity(), "Address: "+locationClicked.getAddressLine(0), Toast.LENGTH_SHORT).show();
				
				// pass information to category screen
				Bundle bundle = new Bundle();
				bundle.putString(MMSDKConstants.JSON_KEY_ADDRESS, locationClicked.getAddressLine(0));
				bundle.putString(MMSDKConstants.JSON_KEY_LOCALITY, locationClicked.getLocality());
				bundle.putString(MMSDKConstants.JSON_KEY_REGION, locationClicked.getAdminArea());
				bundle.putString(MMSDKConstants.JSON_KEY_POSTCODE, locationClicked.getPostalCode());
				bundle.putString(MMSDKConstants.JSON_KEY_LATITUDE, locationClicked.getLatitude()+"");
				bundle.putString(MMSDKConstants.JSON_KEY_LONGITUDE, locationClicked.getLongitude()+"");
				
				Intent intent = new Intent(getActivity(), AddLocationScreen.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(MMLocationManager.isGPSEnabled() && MMLocationManager.getGPSLocation(new MMLocationListener()) != null) {
			try {
				refreshFavorites();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, TAG + "onDestroyView");
		try {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.remove(smfFavoriteLocations);
			transaction.commit();
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void onFragmentBackPressed() {
		
	}

	/**
	 * Make a call to the server and refresh the Favorites list
	 * @throws JSONException 
	 */
	private void refreshFavorites() throws JSONException {
		String favorites = userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_FAVORITES, MMSDKConstants.DEFAULT_STRING_EMPTY);
		if(!favorites.equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
			favoritesList = new JSONArray(favorites);
		} else {
			favoritesList = new JSONArray();
		}
		
		addToGoogleMap();
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	private void addToGoogleMap() throws JSONException {
		markerHashMap.clear();
		googleMap.clear();
		LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		
		for(int i = 0; i < favoritesList.length(); i++) {
			JSONObject jObj = favoritesList.getJSONObject(i);
			
			LatLng resultLocLatLng = new LatLng(jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE));
			
			Marker locationResultMarker = googleMap.addMarker(new MarkerOptions().
					position(resultLocLatLng).
					title(jObj.getString(MMSDKConstants.JSON_KEY_NAME))
					.snippet(jObj.getString(MMSDKConstants.JSON_KEY_ADDRESS)));
			
			if(currMarker != null && currMarker.getTitle().equals(locationResultMarker.getTitle()) && currMarker.getTitle().equals(locationResultMarker.getTitle())) {
				Log.d(TAG, TAG + "marker equal");
				locationResultMarker.showInfoWindow();
				currentLoc = currMarker.getPosition();
			}
			
			markerHashMap.put(locationResultMarker, jObj);
		}

		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, currZoomLevel));
		googleMap.setOnInfoWindowClickListener(FavoritesMapFragment.this);
		googleMap.setOnMapClickListener(FavoritesMapFragment.this);
		googleMap.setMyLocationEnabled(true);
	}
	
	public Address getAddressForLocation(double latitude, double longitude) throws IOException {
        int maxResults = 1;

        Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
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
               titleUi.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
           }

           String snippet = marker.getSnippet();
           TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
           if (snippet != null) {
               snippetUi.setText(snippet);
           } else {
               snippetUi.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
           }
       }
   }
}
