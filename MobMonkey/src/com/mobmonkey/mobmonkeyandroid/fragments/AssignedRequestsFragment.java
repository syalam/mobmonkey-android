package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMAnsweredRequestItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMAssignedRequestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.utils.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMAnswerRequestAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;

/**
 * Android {@link Fragment} to display Assigned Requests.
 * @author Dezapp, LLC
 *
 */
public class AssignedRequestsFragment extends MMFragment {
	private static final String TAG = "AssignedRequestsScreen";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private Location location;
	private ListView lvAssignedRequests;
	private JSONArray assignedRequests;
	private MMAssignedRequestsArrayAdapter arrayAdapter;
	private int positionClicked;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMAPIConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
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
	/**
	 * function that generate an array of {@link MMAssignedRequestsItem} and returns it.
	 * @return {@link MMAssignedRequestsItem[]}
	 * @throws JSONException
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	
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
	
	/**
	 * The {@link OnItemClickListener} for {@link ListView} in AssignedRequestsFragment.
	 *
	 */
	private class onAssignedRequestsClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			//Log.d(TAG, "itemClicked: " + position);
			positionClicked = position;
			try {
				JSONObject data = assignedRequests.getJSONObject(position);
				
				switch(data.getInt(MMAPIConstants.JSON_KEY_MEDIA_TYPE)) {
					// Image request
					case 1:
						userPrefsEditor.putInt(MMAPIConstants.TAB_TITLE_CURRENT_TAG, 1);
						userPrefsEditor.commit();
						Log.d(TAG, "current tab tag: " + userPrefs.getInt(MMAPIConstants.TAB_TITLE_CURRENT_TAG, 0));
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(takePictureIntent, MMAPIConstants.REQUEST_CODE_IMAGE);
						break;
					// Video request
					case 2:
						Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
						takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
						startActivityForResult(takeVideoIntent, MMAPIConstants.REQUEST_CODE_VIDEO);
						break;
					default:
						break;
				}
			} catch (JSONException ex) {
				
			}
		}
		
	}
	
	/**
	 * Gets the return media file, image, or video, and convert it into {@link Base64} string. The function then sends the
	 * {@link Base64} to the server via {@link MMAnswerRequestAdapter}.
	 */
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, TAG + ":onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != FragmentActivity.RESULT_OK)
			return;
		
		// picture data
		if(requestCode == MMAPIConstants.REQUEST_CODE_IMAGE) {
			Log.d(TAG, "return from taking picture with camera");
			Bundle extras = data.getExtras();
			Bitmap mImageBitmap = (Bitmap) extras.get("data");
			
			// encode image to Base64 String
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
			
			try {
				MMAnswerRequestAdapter.AnswerRequest(new mmAnswerRequest(), 
											   MMConstants.PARTNER_ID, 
											   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
											   userPrefs.getString(MMAPIConstants.KEY_AUTH,MMAPIConstants.DEFAULT_STRING), 
											   assignedRequests.getJSONObject(positionClicked).getString(MMAPIConstants.JSON_KEY_REQUEST_ID),
											   assignedRequests.getJSONObject(positionClicked).getInt(MMAPIConstants.JSON_KEY_REQUEST_TYPE),
											   MMAPIConstants.MEDIA_CONTENT_JPEG,
											   imageEncoded,
											   MMAPIConstants.MEDIA_TYPE_IMAGE);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}					   
		} 
		// video 
		else if(requestCode == MMAPIConstants.REQUEST_CODE_VIDEO) {
			
			Uri vid = data.getData();
		    String videoPath = getRealPathFromURI(vid);
		    
		    try {
		    	FileInputStream fis = new FileInputStream(new File(videoPath));

		    	File tmpFile = new File(Environment.getExternalStorageDirectory(),"mobmonkeyVideo.3gp"); 

		    	//save the video to the File path
		    	FileOutputStream fos = new FileOutputStream(tmpFile);

		    	byte[] buf = new byte[1024];
		    	int len;
		    	while ((len = fis.read(buf)) > 0) {
		    		fos.write(buf, 0, len);
		    	}       
		    	fis.close();
		    	fos.close();
		    	  
		    	// encode to base64
		    	String videoEncoded = "";
		    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(tmpFile));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				long fileLength = tmpFile.length();
				byte[] b = new byte[(int) fileLength];
				int bytesRead;
		        while ((bytesRead = in.read(b)) != -1) {
		        	bos.write(b, 0, bytesRead);
		        }
		        try {
		        	byte[] ficheroAEnviar = bos.toByteArray();
			        videoEncoded = Base64.encodeToString(ficheroAEnviar, Base64.DEFAULT);
		        } catch (OutOfMemoryError err) {
		        	Log.d(TAG, "OutOfMemoryError");
		        	byte[] bv = bos.toByteArray();
		        	Base64.encode(bv, Base64.DEFAULT);
		        	
		        	videoEncoded = new String(bv, "UTF-8");
		        }
		        
		    	// send base64 file to server
		        MMAnswerRequestAdapter.AnswerRequest(new mmAnswerRequest(), 
						   MMConstants.PARTNER_ID, 
						   userPrefs.getString(MMAPIConstants.KEY_USER, MMAPIConstants.DEFAULT_STRING), 
						   userPrefs.getString(MMAPIConstants.KEY_AUTH,MMAPIConstants.DEFAULT_STRING), 
						   assignedRequests.getJSONObject(positionClicked).getString(MMAPIConstants.JSON_KEY_REQUEST_ID),
						   assignedRequests.getJSONObject(positionClicked).getInt(MMAPIConstants.JSON_KEY_REQUEST_TYPE),
						   MMAPIConstants.MEDIA_CONTENT_MP4,
						   videoEncoded,
						   MMAPIConstants.MEDIA_TYPE_VIDEO);
		        
		    } catch (IOException e) {
		    	e.printStackTrace();
		    } catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * {@link MMCallback} function. Get call after successfully fulfilled a request.
	 *
	 */
	
	private class mmAnswerRequest implements MMCallback {

		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, (String) obj);
		}
		
	}
	
	/**
	 * Returns the real path of a file from its {@link Uri}
	 * 
	 * @param {@link Uri} of a file
	 * @return {@link String} of the real path of an {@link Uri}.
	 */
	
	public String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
}
