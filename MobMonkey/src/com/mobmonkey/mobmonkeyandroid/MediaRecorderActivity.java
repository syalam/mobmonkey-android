package com.mobmonkey.mobmonkeyandroid;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.mobmonkey.mobmonkeyandroid.utils.MMCameraSurfaceView;
import com.mobmonkey.mobmonkeysdk.utils.MMSDKConstants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MediaRecorderActivity extends Activity{
	
	private Camera camera;
	private MMCameraSurfaceView cameraSurfaceView;
	private MediaRecorder mediaRecorder;
	
	private Button recordButton;
	private TextView recordTime;
	private boolean recording;
	
	private String videoPath =  "/sdcard/mmvideo.mp4";
	private long startTime;
	
	private Timer recordTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mediarecorder_screen);
		
		recording = false;
		camera = getCameraInstance();
		
		if(camera == null) {
			Toast.makeText(MediaRecorderActivity.this, 
						   "Fail to get camera from phone.", 
						   Toast.LENGTH_LONG)
						   .show();
		}
		
		cameraSurfaceView = new MMCameraSurfaceView(this, camera);
		FrameLayout frame = (FrameLayout) findViewById(R.id.flvideoview);
		frame.addView(cameraSurfaceView);
		recordButton = (Button) findViewById(R.id.btnrecord);
		recordButton.setOnClickListener(new recordOnButtonClickListener());
		recordTime = (TextView) findViewById(R.id.tvrecordedTime);
		recordTime.setTextSize(24);
		recordTime.setTextColor(Color.WHITE);
	}
	
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
	
	private class recordOnButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(recording) {
				// stop recording and release camera
				// stop recorder
				mediaRecorder.stop();
				releaseMediaRecorder();
				Intent intent = new Intent();
				setResult(RESULT_OK, intent.putExtra(MMSDKConstants.KEY_INTENT_EXTRA_VIDEO_PATH, 
													 videoPath));
				recordTimer.cancel();
				recordTimer = null;
				finish();
			} else {
				releaseCamera();
				if(!prepareMediaRecorder()) {
					Toast.makeText(MediaRecorderActivity.this, 
								   "Fail in prepareMediaRecorder()!", 
								   Toast.LENGTH_LONG)
								   .show();
					finish();
				}
				
				mediaRecorder.start();
				recording = true;
				recordButton.setText("STOP");
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
			releaseMediaRecorder();
			return false;
		} catch(IOException ex) {
			releaseMediaRecorder();
			return false;
		}
		
		return true;
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
				recordTime.setText(String.format(" %ds ", elapsedTime));
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		releaseMediaRecorder();
	}
	
	
}
