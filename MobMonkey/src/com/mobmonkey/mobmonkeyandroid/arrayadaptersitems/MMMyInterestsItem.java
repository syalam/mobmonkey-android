package com.mobmonkey.mobmonkeyandroid.arrayadaptersitems;

import org.json.JSONObject;

/**
 * @author Dezapp, LLC
 *
 */
public class MMMyInterestsItem {
	private int interestIconId;
	private String interestName;
	private JSONObject interestJObj;
	private int interestIndicatorIconId;
	
	/**
	 * @return the interestIconId
	 */
	public int getInterestIconId() {
		return interestIconId;
	}
	
	/**
	 * @param interestIconId the interestIconId to set
	 */
	public void setInterestIconId(int interestIconId) {
		this.interestIconId = interestIconId;
	}
	
	/**
	 * @return the interestName
	 */
	public String getInterestName() {
		return interestName;
	}

	/**
	 * @param interestName the interestName to set
	 */
	public void setInterestName(String interestName) {
		this.interestName = interestName;
	}

	/**
	 * @return the interestJObj
	 */
	public JSONObject getInterestJObj() {
		return interestJObj;
	}
	
	/**
	 * @param interestJObj the interestJObj to set
	 */
	public void setInterestJObj(JSONObject interestJObj) {
		this.interestJObj = interestJObj;
	}
	
	/**
	 * @return the interestIndicatorIconId
	 */
	public int getInterestIndicatorIconId() {
		return interestIndicatorIconId;
	}
	
	/**
	 * @param interestIndicatorIconId the interestIndicatorIconId to set
	 */
	public void setInterestIndicatorIconId(int interestIndicatorIconId) {
		this.interestIndicatorIconId = interestIndicatorIconId;
	}
}
