package com.mobmonkey.mobmonkey.fragments;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobmonkey.mobmonkey.R;
import com.mobmonkey.mobmonkey.utils.MMAssignedRequestsArrayAdapter;
import com.mobmonkey.mobmonkey.utils.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkey.utils.MMUtility;
import com.mobmonkey.mobmonkeyapi.adapters.MMAnswerRequestAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
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
	private MMAssignedRequestsArrayAdapter arrayAdapter;
	private SharedPreferences userPrefs;
	private int positionClicked;
	
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
			assignedRequests = new JSONArray(getArguments().getString(MMAPIConstants.KEY_INTENT_EXTRA_INBOX_REQUESTS));
			arrayAdapter = new MMAssignedRequestsArrayAdapter(getActivity(), R.layout.assignedrequests_listview_row, getAssignedRequestItems());
			lvAssignedRequests.setAdapter(arrayAdapter);
			lvAssignedRequests.setOnItemClickListener(new onAssignedRequestsClick());
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
	
	private class onAssignedRequestsClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long id) {
			//Log.d(TAG, "itemClicked: " + position);
			positionClicked = position;
			try {
				JSONObject data = assignedRequests.getJSONObject(position);
				
				switch(data.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE)) {
					// Image request
					case 1:
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(takePictureIntent, MMAPIConstants.REQUEST_CODE_IMAGE);
						break;
					// Video request
					case 2:
						Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
						startActivityForResult(takeVideoIntent, MMAPIConstants.REQUEST_CODE_VIDEO);
						break;
					default:
						break;
				}
			} catch (JSONException ex) {
				
			}
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		
		// picture data
		if(requestCode == MMAPIConstants.REQUEST_CODE_IMAGE) {
			Bundle extras = data.getExtras();
			Bitmap mImageBitmap = (Bitmap) extras.get("data");
			
			// encode image to Base64 String
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
			Log.d(TAG, imageEncoded);
			
			try {
				MMAnswerRequestAdapter.AnswerRequest(new mmAnswerRequest(), 
											   MMConstants.PARTNER_ID, 
											   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
											   userPrefs.getString(MMAPIConstants.KEY_AUTH,MMAPIConstants.DEFAULT_STRING), 
											   assignedRequests.getJSONObject(positionClicked).getString(MMAPIConstants.JSON_KEY_REQUESTID), 
											   imageEncoded, 
											   new Date().getTime(), 
											   1);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}					   
		} 
		// video 
		else if(requestCode == MMAPIConstants.REQUEST_CODE_VIDEO) {
			Uri mVideoUri = data.getData();
			File mVideoFile = new File(mVideoUri.getPath());
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(mVideoFile));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				long fileLength = mVideoFile.length();
				byte[] b = new byte[(int) fileLength];
				int bytesRead;
	            while ((bytesRead = in.read(b)) != -1) {
	                bos.write(b, 0, bytesRead);
	            }
	            byte[] ficheroAEnviar = bos.toByteArray();
	            String videoEncoded = Base64.encodeToString(ficheroAEnviar, Base64.DEFAULT);
	            
	            Log.d(TAG, videoEncoded);
	            
	            // send videoEncoded to the server via adapter
	            
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
	}
	
	private class mmAnswerRequest implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String) obj);
		}
		
	}
}
