package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MMCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

	private SurfaceHolder holder;
	private Camera camera;
	
	public MMCameraSurfaceView(Context context, Camera camera) {
		super(context);
		
		this.camera = camera;
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// if preview does not exist
		if(holder.getSurface() == null) {
			return;
		}
		
		// stop preview before making changes
		try {
			camera.stopPreview();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		// start preview with new setting
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// surface created, draw preview for camera
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}

	
}
