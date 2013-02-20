package com.mobmonkey.mobmonkey.utils;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * @author Dezapp, LLC
 *
 */
public class MMLocationItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	private ArrayList<JSONObject> searchLocations = new ArrayList<JSONObject>();
	Context context;
	
	public MMLocationItemizedOverlay(Drawable defaultMarker) {
		super(defaultMarker);
	}

	public MMLocationItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
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
	
	public void addOverlay(OverlayItem overlay) {
		overlayItems.add(overlay);
		populate();
	}
	
	public void addLocationResult(JSONObject jObj) {
		searchLocations.add(jObj);
	}
}
