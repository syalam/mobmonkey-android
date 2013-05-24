package com.mobmonkey.mobmonkeyandroid;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.mobmonkey.mobmonkeyandroid.utils.MMCameraSurfaceView;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VideoRecorderActivity extends Activity {
	
	private Camera camera;
	private MMCameraSurfaceView cameraSurfaceView;
	private MediaRecorder mediaRecorder;
	
	private Button btnRecordStop;
	private TextView tvElapsedTime;
	private LinearLayout llVideoOkCancel;
	
	private boolean isRecording;
	
	private String videoPath;
	private long startTime;
	
	private Timer recordTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_recorder_screen);
		
		isRecording = false;
		
		File mmDir = new File(MMSDKConstants.MOBMONKEY_DIRECTORY);
		if(!mmDir.exists()) {
			mmDir.mkdir();
		}
		
		videoPath =  MMSDKConstants.MOBMONKEY_RECORDED_VIDEO_FILEPATH;
		camera = getCameraInstance();
		
		if(camera == null) {
			new AlertDialog.Builder(VideoRecorderActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.ad_title_camera_error)
				.setMessage(R.string.ad_message_camera_error)
				.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onBackPressed();
					}
				})
				.show();
		}
		
		FrameLayout rlVideoPreview = (FrameLayout) findViewById(R.id.flvideopreview);
		tvElapsedTime = (TextView) findViewById(R.id.tvelapsedtime);
		btnRecordStop = (Button) findViewById(R.id.btnrecordstop);
		llVideoOkCancel = (LinearLayout) findViewById(R.id.llvideookcancel);
		
		cameraSurfaceView = new MMCameraSurfaceView(this, camera);
		rlVideoPreview.addView(cameraSurfaceView);
	}
	
	/**
	 * 
	 * @param view
	 */
	public void viewOnClick(View view) {
		switch(view.getId()) {
			case R.id.btnrecordstop:
				recordStopOnClick();
				break;
			case R.id.btnok:
				okOnClick();
				break;
			case R.id.btncancel:
				cancelOnClick();
				break;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
			c.setDisplayOrientation(90);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return c;
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean prepareMediaRecorder() {
		camera = getCameraInstance();
		mediaRecorder = new MediaRecorder();
		
		camera.unlock();
		mediaRecorder.setCamera(camera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		mediaRecorder.setVideoEncodingBitRate(3000000);
		mediaRecorder.setVideoFrameRate(24);
		mediaRecorder.setVideoSize(640, 480);
		mediaRecorder.setOutputFile(videoPath);
		mediaRecorder.setOrientationHint(90);
		
		// maximun duration?
		// mediaRecorder.setMaxDuration(60000);
		// maximum file size?
		// mediaRecorder.setMaxFileSize(5000000);
		
		mediaRecorder.setPreviewDisplay(cameraSurfaceView.getHolder().getSurface());
		
		try {
			mediaRecorder.prepare();
		} catch(IllegalStateException ex) {
			ex.printStackTrace();
			releaseMediaRecorder();
			return false;
		} catch(IOException ex) {
			ex.printStackTrace();
			releaseMediaRecorder();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 */
	public void recordStopOnClick() {
		if(isRecording) {
			mediaRecorder.stop();
			releaseMediaRecorder();
			
			llVideoOkCancel.setVisibility(View.VISIBLE);
			btnRecordStop.setVisibility(View.INVISIBLE);
			
			recordTimer.cancel();
			
		} else {
			releaseCamera();
			if(!prepareMediaRecorder()) {
				new AlertDialog.Builder(VideoRecorderActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.ad_title_video_recording_error)
					.setMessage(R.string.ad_message_video_recording_error)
					.setNeutralButton(R.string.ad_btn_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onBackPressed();
						}
					})
					.show();
			} else {
				tvElapsedTime.setVisibility(View.VISIBLE);
				mediaRecorder.start();
				isRecording = true;
				btnRecordStop.setText(R.string.btn_stop);
				startTime = System.currentTimeMillis();
	
				recordTimer = new Timer();
				recordTimer.schedule(new TimerTask() {
	
					@Override
					public void run() {
						recordedTime();
					}
					
				}, 0, 1000);
			}
		}
	}
	
	/**
	 * 
	 */
	public void okOnClick() {
		releaseCamera();
		releaseMediaRecorder();

		setResult(RESULT_OK);
		
		recordTimer.cancel();
		recordTimer = null;
		finish();
	}
	
	/**
	 * 
	 */
	public void cancelOnClick() {
		isRecording = false;
		
		llVideoOkCancel.setVisibility(View.INVISIBLE);
		btnRecordStop.setText(R.string.btn_record);
		btnRecordStop.setVisibility(View.VISIBLE);
		tvElapsedTime.setText(MMSDKConstants.DEFAULT_STRING_EMPTY);
		
	}
	
	private void releaseMediaRecorder() {
		if(mediaRecorder != null) {
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
			camera.lock();
		}
	}
	
	private void releaseCamera() {
		if(camera != null) {
			camera.release();
			camera = null;
		}
	}
	
	private void recordedTime() {
//		long currentTime = System.currentTimeMillis(), 
//			 elapsedTime = (currentTime - startTime)/1000;
//		recordTime.setText(String.format("%ds", elapsedTime));
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				long currentTime = System.currentTimeMillis(), 
					 elapsedTime = (currentTime - startTime)/1000;
				tvElapsedTime.setText(String.format(" %ds ", elapsedTime));
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		releaseMediaRecorder();
		releaseCamera();
	}
	
	
}
