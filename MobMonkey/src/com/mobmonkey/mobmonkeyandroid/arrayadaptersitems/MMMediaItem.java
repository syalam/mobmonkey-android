package com.mobmonkey.mobmonkeyandroid.arrayadaptersitems;

import android.graphics.Bitmap;
import android.view.View.OnClickListener;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMediaItem {
	private String locationName;
	private Bitmap imageMedia;
	private String expiryDate;
	private boolean isVideo = false;
	private boolean isImage = false;
	private boolean isAccepted = false;
	private OnClickListener locationNameOnClickListener;
	private OnClickListener playOnClickListener;
	private OnClickListener imageOnClickListener;
	private OnClickListener shareMediaOnClickListener;
	private OnClickListener acceptMediaOnClickListener;
	private OnClickListener rejectMediaOnClickListener;
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

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

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
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

	public OnClickListener getAcceptMediaOnClickListener() {
		return acceptMediaOnClickListener;
	}

	public void setAcceptMediaOnClickListener(
			OnClickListener acceptMediaOnClickListener) {
		this.acceptMediaOnClickListener = acceptMediaOnClickListener;
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

	public OnClickListener getLocationNameOnClickListener() {
		return locationNameOnClickListener;
	}

	public void setLocationNameOnClickListener(
			OnClickListener locationNameOnClickListener) {
		this.locationNameOnClickListener = locationNameOnClickListener;
	}

	public OnClickListener getRejectMediaOnClickListener() {
		return rejectMediaOnClickListener;
	}

	public void setRejectMediaOnClickListener(
			OnClickListener rejectMediaOnClickListener) {
		this.rejectMediaOnClickListener = rejectMediaOnClickListener;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}
	
	
}
