package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmonkey.mobmonkeyandroid.HotSpotRangeActionSheet;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

/**
 * @author Dezapp, LLC
 *
 */
public class NewHotSpotFragment extends MMFragment implements OnTouchListener,
															  OnKeyListener,
															  OnClickListener {
	private static final String TAG = "NewHotSpotFragment: ";
	
	private JSONObject locationInfo;
	
	private ScrollView svHotSpotDetails;
	private SupportMapFragment smfNewHotSpot;
	private EditText etName;
	private EditText etDescription;
	private EditText etRange;
	private Button btnCreateHotSpot;
	private GoogleMap googleMap;
	
	private InputMethodManager inputMethodManager;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		View view = inflater.inflate(R.layout.fragment_new_hot_spot, container, false);
		svHotSpotDetails = (ScrollView) view.findViewById(R.id.svhotspotdetails);
		smfNewHotSpot = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragnewhotspotmap);
		etName = (EditText) view.findViewById(R.id.etname);
		etDescription = (EditText) view.findViewById(R.id.etdescription);
		etRange = (EditText) view.findViewById(R.id.etrange);
		btnCreateHotSpot = (Button) view.findViewById(R.id.btncreatehotspot);
		googleMap = smfNewHotSpot.getMap();
		
		etName.setOnClickListener(NewHotSpotFragment.this);
		etDescription.setOnKeyListener(NewHotSpotFragment.this);
		etRange.setOnTouchListener(NewHotSpotFragment.this);
		btnCreateHotSpot.setOnClickListener(NewHotSpotFragment.this);
		
		try {
			locationInfo = new JSONObject(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_HOT_SPOT_LOCATION));

			LatLng locLatLng = new LatLng(locationInfo.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), locationInfo.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE));
			
			googleMap.addMarker(new MarkerOptions()
				.position(locLatLng)
				.title(locationInfo.getString(MMSDKConstants.JSON_KEY_NAME))
				.snippet(locationInfo.getString(MMSDKConstants.JSON_KEY_ADDRESS)));
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locLatLng, 16));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP) {
			switch(v.getId()) {
				case R.id.etrange:
					Intent intent = new Intent(getActivity(), HotSpotRangeActionSheet.class);
					startActivityForResult(intent, MMSDKConstants.REQUEST_CODE_RANGE);
					return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
			inputMethodManager.hideSoftInputFromWindow(etDescription.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.etname:
				break;
			case R.id.btncreatehotspot:
				break;
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MMSDKConstants.REQUEST_CODE_RANGE) {
			if(resultCode == Activity.RESULT_OK) {
				Log.d(TAG, TAG + "range: " + data.getIntExtra(MMSDKConstants.KEY_INTENT_EXTRA_RANGE, MMSDKConstants.DEFAULT_INT));
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		try {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.remove(smfNewHotSpot);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroyView();
	}

	/* (non-Javadoc)
	 * @see com.mobmonkey.mobmonkeyandroid.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {

	}
}
