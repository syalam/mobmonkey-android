package com.mobmonkey.mobmonkeyandroid.fragments;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMMyInterestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMMyInterestsItem;
import com.mobmonkey.mobmonkeyandroid.listeners.*;
import com.mobmonkey.mobmonkeyandroid.utils.MMCategories;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Dezapp, LLC
 *
 */
public class MyInterestsFragment extends MMFragment implements OnItemClickListener {
	private static final String TAG = "MyInterestsFragment: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private JSONArray interests;
	private JSONArray selectedInterests;
	
	private ListView lvInterests;
	
	private MMMyInterestsItem[] mmMyInterestsItems;
	private MMMyInterestsArrayAdapter mmMyInterestsArrayAdapter;
	
	private MMOnMyInterestsFragmentItemClickListener myInterestsFragmentItemClickListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_myinterests_screen, container, false);
		lvInterests = (ListView) view.findViewById(R.id.lvinterests);
		
		try {
			interests = new JSONArray(getArguments().getString(MMSDKConstants.KEY_INTENT_EXTRA_INTERESTS));
			if(!userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_MY_INTERESTS, MMSDKConstants.DEFAULT_STRING_EMPTY).equals(MMSDKConstants.DEFAULT_STRING_EMPTY)) {
				selectedInterests = new JSONArray(userPrefs.getString(MMSDKConstants.SHARED_PREFS_KEY_MY_INTERESTS, MMSDKConstants.DEFAULT_STRING_EMPTY));
			} else {
				selectedInterests = new JSONArray();
			}
			setInterestsList();			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MMOnMyInterestsFragmentItemClickListener) {
			myInterestsFragmentItemClickListener = (MMOnMyInterestsFragmentItemClickListener) activity;
			if(activity instanceof MMOnFragmentFinishListener) {
				fragmentFinishListener = (MMOnFragmentFinishListener) activity;
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		String category = ((TextView) view.findViewById(R.id.tvlabel)).getText().toString();
		
		JSONArray subCategories = MMCategories.getSubCategoriesWithCategoryName(getActivity(), category, interests);
		
		if(subCategories.length() > 1) {
			myInterestsFragmentItemClickListener.onMyInterestsFragmentItemClick(subCategories, false);
		} else {
			try {
				updateInterests(mmMyInterestsArrayAdapter.getItem(position).getInterestJObj(), position);
				mmMyInterestsArrayAdapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onFragmentBackPressed() {
		saveMyInterests();
		fragmentFinishListener.onFragmentFinish();
	}

	/**
	 * 
	 * @throws JSONException
	 */
	private void setInterestsList() throws JSONException {
		boolean topLevel = getArguments().getBoolean(MMSDKConstants.KEY_INTENT_EXTRA_TOP_LEVEL, true);
		mmMyInterestsItems = new MMMyInterestsItem[interests.length()];
		
		for(int i = 0; i < interests.length(); i++) {
			JSONObject interest = interests.getJSONObject(i);
			String interestName = interest.getString(Locale.getDefault().getLanguage());
			
			mmMyInterestsItems[i] = new MMMyInterestsItem();
			mmMyInterestsItems[i].setInterestJObj(interest);
			mmMyInterestsItems[i].setInterestName(interestName);
			if(topLevel) {
				mmMyInterestsItems[i].setInterestIconId(MMConstants.topLevelCatIcons[i]);
			} else {
				mmMyInterestsItems[i].setInterestIconId(MMSDKConstants.DEFAULT_INT_ZERO);
			}
			
			JSONArray subCategories = MMCategories.getSubCategoriesWithCategoryName(getActivity(), interestName, interests);
			if(subCategories.length() > 1) {
				mmMyInterestsItems[i].setInterestIndicatorIconId(R.drawable.listview_accessory_indicator);
			} else {
				mmMyInterestsItems[i].setInterestIndicatorIconId(android.R.drawable.checkbox_off_background);
				
				for(int j = 0; j < selectedInterests.length(); j++) {
					if(interestName.equals(selectedInterests.getJSONObject(j).getString(Locale.getDefault().getLanguage()))) {
						mmMyInterestsItems[i].setInterestIndicatorIconId(android.R.drawable.checkbox_on_background);
						break;
					}
				}
			}
		}
		
		mmMyInterestsArrayAdapter = new MMMyInterestsArrayAdapter(getActivity(), R.layout.listview_row_searchcategory, mmMyInterestsItems);
		lvInterests.setAdapter(mmMyInterestsArrayAdapter);
        lvInterests.setOnItemClickListener(MyInterestsFragment.this);
	}
	
	/**
	 * 
	 * @param interest
	 * @param position
	 * @throws JSONException 
	 */
	private void updateInterests(JSONObject interest, int position) throws JSONException {
		for(int j = 0; j < selectedInterests.length(); j++) {
			if(interest.getString(Locale.getDefault().getLanguage()).equals(selectedInterests.getJSONObject(j).getString(Locale.getDefault().getLanguage()))) {
				removeFromSelectedInterests(interest);
				mmMyInterestsItems[position].setInterestIndicatorIconId(android.R.drawable.checkbox_off_background);
				return;
			}
		}
		
		addToSelectedInterests(interest);
		mmMyInterestsItems[position].setInterestIndicatorIconId(android.R.drawable.checkbox_on_background);
	}
	
	/**
	 * 
	 * @param interestToAdd
	 * @throws JSONException
	 */
	private void addToSelectedInterests(JSONObject interestToAdd) throws JSONException {
		ArrayList<JSONObject> interests = new ArrayList<JSONObject>();
		for(int i = 0; i < selectedInterests.length(); i++) {
			interests.add(selectedInterests.getJSONObject(i));
		}
		
		interests.add(interestToAdd);
		
		selectedInterests = new JSONArray(interests);
	}
	
	/**
	 * 
	 * @param interestToRemove
	 * @throws JSONException
	 */
	private void removeFromSelectedInterests(JSONObject interestToRemove) throws JSONException {
		ArrayList<JSONObject> interests = new ArrayList<JSONObject>();
		for(int i = 0; i < selectedInterests.length(); i++) {
			if(!selectedInterests.getJSONObject(i).getString(Locale.getDefault().getLanguage()).equals(interestToRemove.getString(Locale.getDefault().getLanguage()))) {
				interests.add(selectedInterests.getJSONObject(i));
			}
		}
		
		selectedInterests = new JSONArray(interests);
	}
	
	/**
	 * 
	 */
	private void saveMyInterests() {
		userPrefsEditor.putString(MMSDKConstants.SHARED_PREFS_KEY_MY_INTERESTS, selectedInterests.toString());
		userPrefsEditor.commit();
	}
}
