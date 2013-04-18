package com.mobmonkey.mobmonkeyandroid.utils;

import android.graphics.Bitmap;
import android.view.View.OnClickListener;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMediaItem {
	private Bitmap imageMedia;
	private boolean isVideo = false;
	private boolean isImage = false;
	private OnClickListener playOnClickListener;
	private OnClickListener imageOnClickListener;
	private OnClickListener shareMediaOnClickListener;

	/**
	 * @return the imageMedia
	 */
	public Bitmap getImageMedia() {
		return imageMedia;
	}

	/**
	 * @param imageMedia the image to set
	 */
	public void setImageMedia(Bitmap imageMedia) {
		this.imageMedia = imageMedia;
	}

	/**
	 * @return the isVideo
	 */
	public boolean isVideo() {
		return isVideo;
	}
	
	/**
	 * @param isVideo the isVideo to set
	 */
	public void setIsVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	
	/**
	 * @return the isImage
	 */
	public boolean isImage() {
		return isImage;
	}

	/**
	 * @param isImage the isImage to set
	 */
	public void setIsImage(boolean isImage) {
		this.isImage = isImage;
	}

	/**
	 * @return the playOnClickListener
	 */
	public OnClickListener getPlayOnClickListener() {
		return playOnClickListener;
	}

	/**
	 * @param playOnClickListener the playOnClickListener to set
	 */
	public void setPlayOnClickListener(OnClickListener playOnClickListener) {
		this.playOnClickListener = playOnClickListener;
	}

	/**
	 * @return the imageOnClickListener
	 */
	public OnClickListener getImageOnClickListener() {
		return imageOnClickListener;
	}

	/**
	 * @param imageOnClickListener the imageOnClickListener to set
	 */
	public void setImageOnClickListener(OnClickListener imageOnClickListener) {
		this.imageOnClickListener = imageOnClickListener;
	}

	/**
	 * @return the shareMediaOnClickListener
	 */
	public OnClickListener getShareMediaOnClickListener() {
		return shareMediaOnClickListener;
	}

	/**
	 * @param shareMediaOnClickListener the shareMediaOnClickListener to set
	 */
	public void setShareMediaOnClickListener(
			OnClickListener shareMediaOnClickListener) {
		this.shareMediaOnClickListener = shareMediaOnClickListener;
	}
}
