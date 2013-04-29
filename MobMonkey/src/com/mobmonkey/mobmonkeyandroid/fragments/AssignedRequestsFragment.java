package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.mobmonkey.mobmonkeyandroid.MediaRecorderActivity;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMAssignedRequestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
import com.mobmonkey.mobmonkeysdk.adapters.MMInboxAdapter;
import com.mobmonkey.mobmonkeysdk.adapters.MMRequestAdapter;
import com.mobmonkey.mobmonkeysdk.utils.MMCallback;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationListener;
import com.mobmonkey.mobmonkeysdk.utils.MMLocationManager;
import com.mobmonkey.mobmonkeysdk.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

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
	private int clickedPosition;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		userPrefs = getActivity().getSharedPreferences(MMSDKConstants.USER_PREFS, Context.MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		View view = inflater.inflate(R.layout.fragment_assignedrequests_screen, container, false);
		lvAssignedRequests = (ListView) view.findViewById(R.id.lvassignedrequests);
		location = MMLocationManager.getGPSLocation(new MMLocationListener());
		
		try {
			MMRequestAdapter.getAssignedRequests(new AssignedRequestCallback(), 
												 MMConstants.PARTNER_ID,
												 userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
												 userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
		} catch (Exception ex) {
			ex.printStackTrace();
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
			item.title = jObj.getString(MMSDKConstants.JSON_KEY_NAME_OF_LOCATION);
			if(jObj.getString(MMSDKConstants.JSON_KEY_MESSAGE).equals(MMSDKConstants.DEFAULT_STRING_NULL)) {
				item.message = MMSDKConstants.DEFAULT_STRING_EMPTY;
			} else {
				item.message = jObj.getString(MMSDKConstants.JSON_KEY_MESSAGE);
			}
			
			//date can be null. leave time as a blank string if its null
			if(jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_DATE).compareTo(MMSDKConstants.DEFAULT_STRING_NULL) == 0) {
				item.time = MMSDKConstants.DEFAULT_STRING_EMPTY;
			}
			else {
				item.time = MMUtility.getDate(Long.parseLong(jObj.getString(MMSDKConstants.JSON_KEY_REQUEST_DATE)), "MMMM dd hh:mma");
			}
			
			item.dis = MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + getString(R.string.miles);
			item.mediaType = jObj.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE);
			
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
			clickedPosition = position;
			try {
				JSONObject data = assignedRequests.getJSONObject(position);
				
				switch(data.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE)) {
					// Image request
					case 1:
						userPrefsEditor.putInt(MMSDKConstants.TAB_TITLE_CURRENT_TAG, 1);
						userPrefsEditor.commit();
						Log.d(TAG, "current tab tag: " + userPrefs.getInt(MMSDKConstants.TAB_TITLE_CURRENT_TAG, 0));
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(takePictureIntent, MMSDKConstants.REQUEST_CODE_IMAGE);
						break;
					// Video request
					case 2:
						Intent takeVideoIntent = new Intent(getActivity(), MediaRecorderActivity.class);
						startActivityForResult(takeVideoIntent, MMSDKConstants.REQUEST_CODE_VIDEO);
						break;
						/*
						Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
						takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
						startActivityForResult(takeVideoIntent, MMSDKConstants.REQUEST_CODE_VIDEO);
						*/
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
		if(requestCode == MMSDKConstants.REQUEST_CODE_IMAGE) {
			Log.d(TAG, "return from taking picture with camera");
			Bundle extras = data.getExtras();
			Bitmap mImageBitmap = (Bitmap) extras.get("data");
			
			// encode image to Base64 String
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
			
			try {
				MMRequestAdapter.answerRequest(new AnswerRequest(),
											   MMSDKConstants.MEDIA_IMAGE,
											   assignedRequests.getJSONObject(clickedPosition).getString(MMSDKConstants.JSON_KEY_REQUEST_ID),
											   assignedRequests.getJSONObject(clickedPosition).getInt(MMSDKConstants.JSON_KEY_REQUEST_TYPE),
											   MMSDKConstants.MEDIA_CONTENT_JPEG,
											   imageEncoded,
											   MMConstants.PARTNER_ID,
											   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
											   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				MMProgressDialog.displayDialog(getActivity(),
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_uploading_image));
			} catch (JSONException e) {
				e.printStackTrace();
			}					   
		} 
		// video 
		else if(requestCode == MMSDKConstants.REQUEST_CODE_VIDEO) {
			
			//Uri vid = data.getData();
		    //String videoPath = getRealPathFromURI(vid);
			String videoPath = data.getStringExtra(MMSDKConstants.KEY_INTENT_EXTRA_VIDEO_PATH);
		    
		    try {
		    	FileInputStream fis = new FileInputStream(new File(videoPath));

		    	// TODO: changed the file format to .mp4
		    	File tmpFile = new File(Environment.getExternalStorageDirectory(), 
		    							MMSDKConstants.MOBMONKEY_VIDEO_TEMP_FILENAME); 

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
		        MMRequestAdapter.answerRequest(new AnswerRequest(),
					     					   MMSDKConstants.MEDIA_VIDEO,
					     					   assignedRequests.getJSONObject(clickedPosition).getString(MMSDKConstants.JSON_KEY_REQUEST_ID),
					     					   assignedRequests.getJSONObject(clickedPosition).getInt(MMSDKConstants.JSON_KEY_REQUEST_TYPE),
					     					   MMSDKConstants.MEDIA_CONTENT_MP4,
					     					   videoEncoded,
					     					   MMConstants.PARTNER_ID,
					     					   userPrefs.getString(MMSDKConstants.KEY_USER, MMSDKConstants.DEFAULT_STRING_EMPTY),
					     					   userPrefs.getString(MMSDKConstants.KEY_AUTH, MMSDKConstants.DEFAULT_STRING_EMPTY));
				MMProgressDialog.displayDialog(getActivity(),
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_uploading_video));		        
		    } catch (IOException e) {
		    	e.printStackTrace();
		    } catch (JSONException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * {@link MMCallback} function. Get call after successfully fulfilled a request.
	 *
	 */
	
	private class AnswerRequest implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			Log.d(TAG, "Response: " + (String) obj);
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				// if successfully uploaded a media, refresh list
				try {
					JSONObject jObj = new JSONObject((String) obj);
					if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
	//					MMAssignedRequestsItem[] items, data;
	//					data = getAssignedRequestItems();
	//					items = new MMAssignedRequestsItem[data.length - 1];
	//					
	//					for(int i = 0; i < data.length; i++) {
	//						if(i < positionClicked) {
	//							items[i] = data[i];
	//						} else if (i > positionClicked) {
	//							items[i-1] = data[i];
	//						}
	//					}
						JSONArray newArray = new JSONArray();
						for(int i = 0; i < assignedRequests.length(); i++) {
							if(i != clickedPosition) {
								newArray.put(assignedRequests.getJSONObject(i));
							}
						}
						assignedRequests = newArray;
						
						arrayAdapter = new MMAssignedRequestsArrayAdapter(getActivity(), R.layout.listview_row_assigned_requests, getAssignedRequestItems());
						lvAssignedRequests.setAdapter(arrayAdapter);
						lvAssignedRequests.invalidate();
						
						Toast.makeText(getActivity().getApplicationContext(),
									   "You have successfully fulfilled a request.",
									   Toast.LENGTH_LONG).
									   show();
						
						// remove temp video files
						File mmTempFile = new File("sdcard/" + MMSDKConstants.MOBMONKEY_VIDEO_TEMP_FILENAME),
							 mmVideoFile = new File("sdcard/" + MMSDKConstants.MOBMONKEY_VIDEO_FILENAME);
						
						boolean deletedTemp = mmTempFile.delete(),
								deletedVideo = mmVideoFile.delete();
					} 
					// if fail
					else {
						Toast.makeText(getActivity().getApplicationContext(), 
								   	   "An error has occured while uploading media.", 
								   	   Toast.LENGTH_LONG)
								   	   .show();
					}
				} catch(JSONException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
//			try {
//				JSONObject jObj = new JSONObject((String)obj);
//				if(jObj.getString(MMAPIConstants.JSON_KEY_STATUS).equals(MMAPIConstants.RESPONSE_STATUS_SUCCESS)) {
//					MMAssignedRequestsItem[] items, data;
//					data = getAssignedRequestItems();
//					items = new MMAssignedRequestsItem[data.length - 1];
//					
//					for(int i = 0; i < data.length; i++) {
//						if(i < positionClicked) {
//							items[i] = data[i];
//						} else if (i > positionClicked) {
//							items[i-1] = data[i];
//						}
//					}
//					
//					assignedRequests = new JSONArray((String)obj);
//					arrayAdapter = new MMAssignedRequestsArrayAdapter(getActivity(), R.layout.assignedrequests_listview_row, data = getAssignedRequestItems());
//					lvAssignedRequests.setAdapter(arrayAdapter);
//					lvAssignedRequests.invalidate();
//					
//					Toast.makeText(getActivity().getApplicationContext(), 
//							   "You have successfully fulfilled a request.", 
//							   Toast.LENGTH_LONG)
//							   .show();
//				} else {
//					Toast.makeText(getActivity().getApplicationContext(), 
//							   "An error has occured while uploading media.", 
//							   Toast.LENGTH_LONG)
//							   .show();
//				}
//				
//				
//			} catch (JSONException e) {
//				e.printStackTrace();
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
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
	
	/**
	 * {@link MMCallback} function. Get call Assigned requests.
	 *
	 */
	private class AssignedRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			if(obj != null) {
				Log.d(TAG, "AssignedRequest: " + (String) obj);
				try {
					assignedRequests = new JSONArray((String) obj);
					arrayAdapter = new MMAssignedRequestsArrayAdapter(getActivity(), R.layout.listview_row_assigned_requests, getAssignedRequestItems());
					lvAssignedRequests.setAdapter(arrayAdapter);
					lvAssignedRequests.setOnItemClickListener(new onAssignedRequestsClick());
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
