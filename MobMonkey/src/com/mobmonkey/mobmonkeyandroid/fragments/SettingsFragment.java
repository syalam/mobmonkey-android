package com.mobmonkey.mobmonkeyandroid.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private MMOnSettingsFragmentItemClickListener listener;
	private ListView lvSettingsCategory;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_settings_screen, container, false);
		
		lvSettingsCategory = (ListView) view.findViewById(R.id.lvsettingscategory);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.listview_row_settings, R.id.tvsettingscategory, getResources().getStringArray(R.array.settings_category));
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
				MMUserAdapter.signOut(new SignOutCallback(),
									  MMConstants.PARTNER_ID,
									  userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
									  userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				MMProgressDialog.displayDialog(getActivity(),
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_signing_out));
			break;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnSettingsFragmentItemClickListener) {
			listener = (MMOnSettingsFragmentItemClickListener) activity;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		listener.onSettingsFragmentItemClick(position);
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
			MMProgressDialog.dismissDialog();

			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
					// TODO: clear all the saved username/email/passwords/tokens
					Session session = Session.getActiveSession();
					if(session != null) {
						Log.d(TAG, TAG + "session not null");
						session.closeAndClearTokenInformation();
					}
					Toast.makeText(getActivity(), R.string.toast_sign_out_successful, Toast.LENGTH_SHORT).show();
				}
				userPrefsEditor.remove(MMSDKConstants.TAB_TITLE_CURRENT_TAG);
				userPrefsEditor.commit();
 				getActivity().finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Log.d(TAG, TAG + "callback response: " + (String) obj);
		}
	}
}
