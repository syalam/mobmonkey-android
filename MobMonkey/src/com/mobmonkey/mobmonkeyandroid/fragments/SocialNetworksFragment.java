package com.mobmonkey.mobmonkeyandroid.fragments;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMSocialNetworksArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMSocialNetworksItem;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnFragmentFinishListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

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
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Activity.MODE_PRIVATE);
		lvSocialNetworks = (ListView) view.findViewById(R.id.lvsocialnetwork);
		MMSocialNetworksArrayAdapter arrayAdapter = new MMSocialNetworksArrayAdapter(getActivity(), R.layout.listview_row_socialnetworks, getNetworkItems());
		lvSocialNetworks.setAdapter(arrayAdapter);
		
		return view;
	}
	
	private MMSocialNetworksItem[] getNetworkItems() {
		MMSocialNetworksItem[] data = new MMSocialNetworksItem[2];
		
		data[0] = new MMSocialNetworksItem();
		data[0].title = getActivity().getResources().getStringArray(R.array.social_networks_name)[0];
		data[0].isOn = userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY)
							.equals(MMSDKConstants.OAUTH_PROVIDER_FACEBOOK) ? true:false;
		
		data[1] = new MMSocialNetworksItem();
		data[1].title = getActivity().getResources().getStringArray(R.array.social_networks_name)[1];
		data[1].isOn = userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY)
							.equals(MMSDKConstants.OAUTH_PROVIDER_TWITTER) ? true:false;
		
		
		Log.d(TAG, "Provider: " + userPrefs.getString(MMSDKConstants.KEY_OAUTH_PROVIDER, MMSDKConstants.DEFAULT_STRING_EMPTY));
		return data;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnFragmentFinishListener) {
			fragmentFinishListener = (MMOnFragmentFinishListener) activity;
		}
	}

	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		getActivity().setTitle(getResources().getString(R.string.tv_title_socialnetworks));
	}

	@Override
	public void onFragmentBackPressed() {
		fragmentFinishListener.onFragmentFinish();
	}
}
