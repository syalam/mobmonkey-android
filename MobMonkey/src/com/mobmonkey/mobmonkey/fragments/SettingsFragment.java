package com.mobmonkey.mobmonkey.fragments;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.R.array;
import com.mobmonkey.mobmonkey.R.id;
import com.mobmonkey.mobmonkey.R.layout;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignOutAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author Dezapp, LLC
 *
 */
public class SettingsFragment extends MMFragment implements OnClickListener, OnItemClickListener {
	private final static String TAG = "SettingsFragment: ";
	
	private OnItemClickListener listener;
	ListView lvSettingsCategory;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_settings_screen, container, false);
		
		lvSettingsCategory = (ListView) view.findViewById(R.id.lvsettingscategory);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.settings_category_list_row, R.id.tvsettingscategory, getResources().getStringArray(R.array.settings_category));
		lvSettingsCategory.setAdapter(arrayAdapter);
		lvSettingsCategory.setOnItemClickListener(this);
		
		Button btnSignOut = (Button) view.findViewById(R.id.btnsignout);
		btnSignOut.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnsignout:
//				MMSignOutAdapter.signOut(new SignOutCallback(), userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
//					userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID);
//				progressDialog = ProgressDialog.show(getParent(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_out), true, false);
			Log.d(TAG, TAG + "signout button onclicked");
			break;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnItemClickListener) {
			listener = (OnItemClickListener) activity;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		listener.onSettingsItemCLick(position);
	}
	
	public interface OnItemClickListener {
		public void onSettingsItemCLick(int position);
	}

	@Override
	public void onFragmentBackPressed() {
		
	}
}
