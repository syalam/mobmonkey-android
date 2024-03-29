package com.mobmonkey.mobmonkeyandroid.fragments;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.arrayadapters.MMAssignedRequestsArrayAdapter;
import com.mobmonkey.mobmonkeyandroid.arrayadaptersitems.MMAssignedRequestsItem;
import com.mobmonkey.mobmonkeyandroid.utils.MMConstants;
import com.mobmonkey.mobmonkeyandroid.utils.MMExpandedListView;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;
import com.mobmonkey.mobmonkeyandroid.utils.MMUtility;
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
	private MMExpandedListView elvAssignedRequests;
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
		elvAssignedRequests = (MMExpandedListView) view.findViewById(R.id.elvassignedrequests);
		location = MMLocationManager.getGPSLocation();
		
		try {
			MMRequestAdapter.getAssignedRequests(new AssignedRequestCallback());
			MMProgressDialog.displayDialog(getActivity(),
										   MMSDKConstants.DEFAULT_STRING_EMPTY,
										   getString(R.string.pd_retrieving_assigned_requests));
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
	
	private MMAssignedRequestsItem[] getAssignedRequestItems() throws JSONException, ParseException {
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
			
			item.time = jObj.isNull(MMSDKConstants.JSON_KEY_ASSIGNED_DATE) ? item.time = MMSDKConstants.DEFAULT_STRING_EMPTY : MMUtility.getDate(jObj.getLong(MMSDKConstants.JSON_KEY_ASSIGNED_DATE), MMSDKConstants.DATE_FORMAT_MMMM_DD_HH_SEMICOLON_MMA);
			item.dis = MMUtility.calcDist(location, jObj.getDouble(MMSDKConstants.JSON_KEY_LATITUDE), jObj.getDouble(MMSDKConstants.JSON_KEY_LONGITUDE)) + MMSDKConstants.DEFAULT_STRING_SPACE + getString(R.string.miles);
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
				
				File mmDir = new File(MMSDKConstants.MOBMONKEY_DIRECTORY);
//				Log.d(TAG, TAG + "mmDir exists: " + mmDir.exists());
				if(!mmDir.exists()) {
					mmDir.mkdirs();
				}
				switch(data.getInt(MMSDKConstants.JSON_KEY_MEDIA_TYPE)) {
					// Image request
					case 1:
						userPrefsEditor.putInt(MMSDKConstants.TAB_TITLE_CURRENT_TAG, 1);
						userPrefsEditor.commit();
						Log.d(TAG, "current tab tag: " + userPrefs.getInt(MMSDKConstants.TAB_TITLE_CURRENT_TAG, 0));
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MMSDKConstants.MOBMONKEY_DIRECTORY, "mmpic.jpg")));
						startActivityForResult(takePictureIntent, MMSDKConstants.REQUEST_CODE_IMAGE);
						break;
					// Video request
					case 2:
//						Log.d(TAG, TAG + "video file: " + new File(MMSDKConstants.MOBMONKEY_DIRECTORY, "mmvideo.mp4").getAbsolutePath());
//						Intent takeVideoIntent = new Intent(getActivity(), VideoRecorderActivity.class);
//						startActivityForResult(takeVideoIntent, MMSDKConstants.REQUEST_CODE_VIDEO);
						
						Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
						takeVideoIntent.putExtra("EXTRA_VIDEO_QUALITY", 1);
						takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MMSDKConstants.MOBMONKEY_DIRECTORY, "mmvideo.3gp")));
						takeVideoIntent.putExtra("android.intent.extra.durationLimit", 10);
						startActivityForResult(takeVideoIntent, MMSDKConstants.REQUEST_CODE_VIDEO);
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
		Log.d(TAG, TAG + "onActivityResult: " + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != FragmentActivity.RESULT_OK)
			return;
		
		// picture data
		if(requestCode == MMSDKConstants.REQUEST_CODE_IMAGE) {
			Log.d(TAG, "return from taking picture with camera");
//			Bundle extras = data.getExtras();
//			Bitmap mImageBitmap = (Bitmap) extras.get("data");
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap mImageBitmap = BitmapFactory.decodeFile(MMSDKConstants.MOBMONKEY_DIRECTORY + File.separator + "mmpic.jpg", options);
//			
//			Log.d(TAG, TAG + ": rotate bitmap");
//			Log.d(TAG, TAG + ": bitmap width: " + mImageBitmap.getWidth());
//			Log.d(TAG, TAG + ": bitmap height: " + mImageBitmap.getHeight());
//			Matrix matrix = new Matrix();
//			matrix.postRotate(90.0f);
			
//			Bitmap newBitmap = Bitmap.createBitmap(mImageBitmap, 0, 0, mImageBitmap.getWidth(), mImageBitmap.getHeight());
//			Log.d(TAG, TAG + ": bitmap rotated");
//			mImageBitmap = scaleDownBitmap(mImageBitmap, 200, getActivity());
			
			// encode image to Base64 String
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
			byte[] b = baos.toByteArray();
			String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
			
			try {
				MMRequestAdapter.answerRequest(new AnswerRequest(),
											   MMSDKConstants.MEDIA_IMAGE,
											   assignedRequests.getJSONObject(clickedPosition).getString(MMSDKConstants.JSON_KEY_REQUEST_ID),
											   assignedRequests.getJSONObject(clickedPosition).getInt(MMSDKConstants.JSON_KEY_REQUEST_TYPE),
											   MMSDKConstants.MEDIA_CONTENT_JPEG,
											   imageEncoded);
				MMProgressDialog.displayDialog(getActivity(),
											   MMSDKConstants.DEFAULT_STRING_EMPTY,
											   getString(R.string.pd_uploading_image));
			} catch (JSONException e) {
				e.printStackTrace();
			}					   
		} 
		// video 
		else if(requestCode == MMSDKConstants.REQUEST_CODE_VIDEO) {
			Log.d(TAG, TAG + "returning from request video");
		    try {
		    	// encode to base64
		    	File videoFile = new File(MMSDKConstants.MOBMONKEY_DIRECTORY, MMSDKConstants.MOBMONKEY_RECORDED_VIDEO_FILENAME);
		    	if(!videoFile.exists()) {
		    		videoFile.mkdir();
		    	}
		    	String videoEncoded = MMSDKConstants.DEFAULT_STRING_EMPTY;
		    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(videoFile));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				long fileLength = videoFile.length();
				byte[] b = new byte[(int) fileLength];
				int bytesRead;
		        while ((bytesRead = in.read(b)) != -1) {
		        	bos.write(b, 0, bytesRead);
		        }
		        
		        in.close();
		        
		        try {
		        	byte[] ficheroAEnviar = bos.toByteArray();
			        videoEncoded = Base64.encodeToString(ficheroAEnviar, Base64.DEFAULT);
		        } catch (OutOfMemoryError err) {
		        	Log.d(TAG, "OutOfMemoryError");
		        	byte[] bv = bos.toByteArray();
		        	Base64.encode(bv, Base64.DEFAULT);
		        	
		        	videoEncoded = new String(bv, "UTF-8");
		        }
		        
		        Log.d(TAG, TAG + "about to send to server");
		    	// send base64 file to server
		        MMRequestAdapter.answerRequest(new AnswerRequest(),
					     					   MMSDKConstants.MEDIA_VIDEO,
					     					   assignedRequests.getJSONObject(clickedPosition).getString(MMSDKConstants.JSON_KEY_REQUEST_ID),
					     					   assignedRequests.getJSONObject(clickedPosition).getInt(MMSDKConstants.JSON_KEY_REQUEST_TYPE),
					     					   MMSDKConstants.MEDIA_CONTENT_MP4,
					     					   videoEncoded);
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
	 * 
	 */
	private void displayAlertNoMoreAssignedRequests() {
		new AlertDialog.Builder(getActivity())
			.setTitle(R.string.ad_title_no_more_assigned_requests)
			.setMessage(R.string.ad_message_no_more_assigned_requests)
			.setCancelable(false)
			.setNegativeButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getActivity().onBackPressed();
				}
			})
			.show();
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
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObj = new JSONObject((String) obj);
						
						if(jObj.getString(MMSDKConstants.JSON_KEY_STATUS).equals(MMSDKConstants.RESPONSE_STATUS_SUCCESS)) {
							Toast.makeText(getActivity(), R.string.toast_uploaded_assigned_request, Toast.LENGTH_LONG).show();
						
							// remove recorded video file
							File mmVideoFile = new File(MMSDKConstants.MOBMONKEY_DIRECTORY, MMSDKConstants.MOBMONKEY_RECORDED_VIDEO_FILENAME);
							if(mmVideoFile.exists()) {
								mmVideoFile.delete();
							} else {
								File mmImageFile = new File(MMSDKConstants.MOBMONKEY_DIRECTORY + File.separator + "mmpic.jpg");
								mmImageFile.delete();
							}
							
							JSONArray newArray = new JSONArray();
							for(int i = 0; i < assignedRequests.length(); i++) {
								if(i != clickedPosition) {
									newArray.put(assignedRequests.getJSONObject(i));
								}
							}
							
							assignedRequests = newArray;
							
							arrayAdapter = new MMAssignedRequestsArrayAdapter(getActivity(), R.layout.listview_row_assigned_requests, getAssignedRequestItems());
							elvAssignedRequests.setAdapter(arrayAdapter);
							elvAssignedRequests.invalidate();
							
							if(newArray.length() < 1) {
								displayAlertNoMoreAssignedRequests();
							}
						} else {
							Toast.makeText(getActivity(), R.string.toast_failed_upload_assigned_request, Toast.LENGTH_LONG).show();
						}
					} catch(JSONException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	/**
	 * {@link MMCallback} function. Get call Assigned requests.
	 *
	 */
	private class AssignedRequestCallback implements MMCallback {
		@Override
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			if(obj != null) {
				Log.d(TAG, "AssignedRequest: " + (String) obj);
				if(((String) obj).equals(MMSDKConstants.CONNECTION_TIMED_OUT)) {
					Toast.makeText(getActivity(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show();
				} else {
					try {
						assignedRequests = new JSONArray((String) obj);
						arrayAdapter = new MMAssignedRequestsArrayAdapter(getActivity(), R.layout.listview_row_assigned_requests, getAssignedRequestItems());
						elvAssignedRequests.setAdapter(arrayAdapter);
						elvAssignedRequests.setVisibility(View.VISIBLE);
						elvAssignedRequests.setOnItemClickListener(new onAssignedRequestsClick());
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
