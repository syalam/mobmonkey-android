package com.mobmonkey.mobmonkey.fragments;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMAssignedRequestsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationListener;
import com.mobmonkey.mobmonkeyapi.utils.MMLocationManager;

/**
 * @author Dezapp, LLC
 *
 */
public class AssignedRequestsFragment extends MMFragment {
	private static final String TAG = "AssignedRequestsScreen";
	
	private Location location;
	private ListView lvAssignedRequests;
	private JSONArray assignedRequests;
	MMAssignedRequestsArrayAdapter arrayAdapter;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_assignedrequests_screen, container, false);
		lvAssignedRequests = (ListView) view.findViewById(R.id.lvassignedrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		try {
			assignedRequests = new JSONArray(getArguments().getString(MMAPIConstants.JSON_KEY_ASSIGNED_REQUESTS));
			arrayAdapter = new MMAssignedRequestsArrayAdapter(getActivity(), R.layout.assignedrequests_listview_row, getAssignedRequestItems());
			lvAssignedRequests.setAdapter(arrayAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobmonkey.mobmonkey.utils.MMFragment#onFragmentBackPressed()
	 */
	@Override
	public void onFragmentBackPressed() {
		
	}
	
	private MMAssignedRequestsItem[] getAssignedRequestItems() throws JSONException, NumberFormatException, ParseException {
		MMAssignedRequestsItem[] assginedRequestItems = new MMAssignedRequestsItem[assignedRequests.length()];

		for(int i = 0; i < assignedRequests.length(); i++) {
			JSONObject jObj = assignedRequests.getJSONObject(i);
			MMAssignedRequestsItem item = new MMAssignedRequestsItem();
			item.title = jObj.getString(MMAPIConstants.JSON_KEY_NAME_OF_LOCATION);
			if(jObj.getString(MMAPIConstants.JSON_KEY_MESSAGE).equals(MMAPIConstants.DEFAULT_STRING_NULL)) {
				item.message = MMAPIConstants.DEFAULT_STRING;
			} else {
				item.message = jObj.getString(MMAPIConstants.JSON_KEY_MESSAGE);
			}
			
			//date can be null. leave time as a blank string if its null
			if(jObj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE).compareTo(MMAPIConstants.DEFAULT_STRING_NULL) == 0) {
				item.time = MMAPIConstants.DEFAULT_STRING;
			}
			else {
				item.time = MMUtility.getDate(Long.parseLong(jObj.getString(MMAPIConstants.JSON_KEY_REQUEST_DATE)), "MMMM dd hh:mma");
			}
			
			item.dis = MMUtility.calcDist(location, jObj.getDouble(MMAPIConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMAPIConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles);
			item.mediaType = jObj.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE);
			
			assginedRequestItems[i] = item;
		}
		
		return assginedRequestItems;
	}
}
