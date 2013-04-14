package com.mobmonkey.mobmonkeyandroid.fragments;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMSocialNetworkArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMSocialNetworkItem;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Dezapp, LLC
 *
 */
public class SocialNetworksFragment extends MMFragment {
	private static final String TAG = "SocialNetworksFragment";
	
	private ListView lvSocialNetworks;
	private SharedPreferences userPrefs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_socialnetworks_screen, container, false);
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Activity.MODE_PRIVATE);
		lvSocialNetworks = (ListView) view.findViewById(R.id.lvsocialnetwork);
		MMSocialNetworkArrayAdapter arrayAdapter = new MMSocialNetworkArrayAdapter(getActivity(), R.layout.social_networks_listview_row, getNetworkItems());
		lvSocialNetworks.setAdapter(arrayAdapter);
		
		return view;
	}
	
	private MMSocialNetworkItem[] getNetworkItems() {
		MMSocialNetworkItem[] data = new MMSocialNetworkItem[2];
		
		data[0] = new MMSocialNetworkItem();
		data[0].title = getActivity().getResources().getStringArray(R.array.social_networks_name)[0];
		data[0].isOn = userPrefs.getString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING)
							.equals(MMAPIConstants.OAUTH_PROVIDER_FACEBOOK) ? true:false;
		
		data[1] = new MMSocialNetworkItem();
		data[1].title = getActivity().getResources().getStringArray(R.array.social_networks_name)[1];
		data[1].isOn = userPrefs.getString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING)
							.equals(MMAPIConstants.OAUTH_PROVIDER_TWITTER) ? true:false;
		
		
		Log.d(TAG, "Provider: " + userPrefs.getString(MMAPIConstants.KEY_OAUTH_PROVIDER, MMAPIConstants.DEFAULT_STRING));
		return data;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnFragmentFinishListener) {
			onFragmentFinishListener = (OnFragmentFinishListener) activity;
		}
	}

	@Override
	public void onFragmentBackPressed() {
		onFragmentFinishListener.onFragmentFinish();
	}
}
