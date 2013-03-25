package com.mobmonkey.mobmonkey.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.R.array;
import com.mobmonkey.mobmonkey.R.id;
import com.mobmonkey.mobmonkey.R.layout;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignOutAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 *
 */
public class SettingsFragment extends MMFragment implements OnClickListener, OnItemClickListener {
	private final static String TAG = "SettingsFragment: ";
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;
	
	private OnItemClickListener listener;
	ProgressDialog progressDialog;
	ListView lvSettingsCategory;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, getActivity().MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
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
				MMSignOutAdapter.signOut(new SignOutCallback(), userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
					userPrefs.getString(MMAPIConstants.KEY_AUTH, MMAPIConstants.DEFAULT_STRING), MMConstants.PARTNER_ID);
				progressDialog = ProgressDialog.show(getActivity(), MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_out), true, false);
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
	
    /**
     * Custom {@link MMCallback} specifically for {@link SettingsScreen} to be processed after receiving response from MobMonkey server.
     * @author Dezapp, LLC
     *
     */
	private class SignOutCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}

			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString(MMAPIConstants.KEY_RESPONSE_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
					// TODO: clear all the saved username/email/passwords/tokens
					Session session = Session.getActiveSession();
					if(session != null) {
						Log.d(TAG, TAG + "session not null");
						session.closeAndClearTokenInformation();
					}
					Toast.makeText(getActivity(), R.string.toast_sign_out_successful, Toast.LENGTH_SHORT).show();
				}
 				getActivity().finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Log.d(TAG, TAG + "callback response: " + (String) obj);
		}
	}
}
