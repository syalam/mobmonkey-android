package com.mobmonkey.mobmonkeyandroid.fragments;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMAnsweredRequestArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMAnsweredRequestItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * Android {@link Fragment} to display Answered Requests.
 * @author Dezapp, LLC
 *
 */

public class AnsweredRequestsFragment extends MMFragment {
	private static final String TAG = "AnsweredRequestsFragment";
	private ListView lvAnsweredRequests;
	private JSONArray answeredRequests;
	private SharedPreferences userPrefs;
	private int positionClicked;
	private Location location;
	private MMAnsweredRequestArrayAdapter arrayAdapter;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_answeredrequests_screen, container, false);
		lvAnsweredRequests = (ListView) view.findViewById(R.id.lvAnsweredrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		try {
			answeredRequests = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS));
			arrayAdapter = new MMAnsweredRequestArrayAdapter(getActivity(), R.layout.answeredrequests_listview_row, getAnsweredRequestItems());
			lvAnsweredRequests.setAdapter(arrayAdapter);
			lvAnsweredRequests.setOnItemClickListener(new onAnsweredRequestsClick());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return view;
	}
	
	@Override
	public void onFragmentBackPressed() {

	}

	/**
	 * function that generate an array of {@link MMAnsweredRequestItem} and returns it.
	 * @return {@link MMAnsweredRequestItem[]}
	 * @throws JSONException
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	
	private MMAnsweredRequestItem[] getAnsweredRequestItems() throws JSONException, NumberFormatException, ParseException {
		MMAnsweredRequestItem[] answeredRequestItems = new MMAnsweredRequestItem[answeredRequests.length()];

		for(int i = 0; i < answeredRequests.length(); i++) {
			JSONObject jObj = answeredRequests.getJSONObject(i);
			MMAnsweredRequestItem item = new MMAnsweredRequestItem();
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
			
			answeredRequestItems[i] = item;
		}
		
		return answeredRequestItems;
	}
	
	/**
	 * The {@link OnItemClickListener} for {@link ListView} in AnsweredRequestsFragment.
	 *
	 */
	private class onAnsweredRequestsClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			
		}
		
	}
}
