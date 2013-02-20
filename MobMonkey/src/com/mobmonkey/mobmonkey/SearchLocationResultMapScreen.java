package com.mobmonkey.mobmonkey;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class SearchLocationResultMapScreen extends MapActivity {
	private static final String TAG = "SearchLocationResultMapScreen: ";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_location_result_map_screen);
		
		MapView mvLocationResult = (MapView) findViewById(R.id.mvlocationresult);
		mvLocationResult.setBuiltInZoomControls(true);
		
		List<Overlay> mapOverlays = mvLocationResult.getOverlays();
		
		JSONObject jObj;
		try {
			jObj = new JSONObject(getIntent().getStringExtra(MMAPIConstants.KEY_INTENT_EXTRA_LOCATION_DETAILS));
			
			LocationItemizedOverlay locationItemizedOverlay = new LocationItemizedOverlay(getResources().getDrawable(R.drawable.icon_search), SearchLocationResultMapScreen.this);
			
			double latitude = jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE);
			double longitude = jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE);
			
			Log.d(TAG, TAG + "lat: " + latitude + " long: " + longitude);
			
			GeoPoint geoPoint = new GeoPoint((int) (latitude * 1E6) , (int) (longitude * 1E6));
			Log.d(TAG, TAG + "geoPoint: lat: " + geoPoint.getLatitudeE6() + " long: " + geoPoint.getLongitudeE6());
			
			OverlayItem overlayItem = new OverlayItem(geoPoint, jObj.getString(MMAPIConstants.JSON_KEY_NAME), "View details");
			
			locationItemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(locationItemizedOverlay);
			
			MapController mcLocationResult = mvLocationResult.getController();
			mcLocationResult.animateTo(geoPoint);
			mcLocationResult.setZoom(19);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private class LocationItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
		Context context;
		
		public LocationItemizedOverlay(Drawable defaultMarker) {
			super(defaultMarker);
		}
		
		public LocationItemizedOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			this.context = context;
		}
		
		public void addOverlay(OverlayItem overlay) {
			overlayItems.add(overlay);
			populate();
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			return overlayItems.get(i);
		}

		@Override
		public int size() {
			return overlayItems.size();
		}

		/* (non-Javadoc)
		 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
		 */
		@Override
		protected boolean onTap(int index) {
			OverlayItem item = overlayItems.get(index);
			new AlertDialog.Builder(context)
				.setTitle(item.getTitle())
				.setMessage(item.getSnippet())
				.show();
			
			return true;
		}
	}
}
