package com.mobmonkey.mobmonkey.fragments;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.R.array;
import com.mobmonkey.mobmonkey.R.id;
import com.mobmonkey.mobmonkey.R.layout;
import com.mobmonkey.mobmonkey.utils.MMFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	ListView lvSocialNetworks;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_socialnetworks_screen, container, false);
		
		lvSocialNetworks = (ListView) view.findViewById(R.id.lvsocialnetwork);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.social_networks_listview_row, R.id.tvsocialnetworktext, getResources().getStringArray(R.array.social_networks_name));
		lvSocialNetworks.setAdapter(arrayAdapter);
		
		return view;
	}

	@Override
	public void onFragmentBackPressed() {
		
	}
}
