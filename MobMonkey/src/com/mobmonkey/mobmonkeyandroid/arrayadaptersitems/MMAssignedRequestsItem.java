package com.mobmonkey.mobmonkeyandroid.arrayadaptersitems;

import java.io.IOException;

public class MMAssignedRequestsItem {

	public String title, dis, message, time;
	public int icon, mediaType;
	
	public MMAssignedRequestsItem() {
		super();
	}
	
	public MMAssignedRequestsItem(String title, String dis, String message, String time, int icon, int mediaType) throws IOException {
		super();
		this.title = title;
		this.dis = dis;
		this.message = message;
		this.time = time;
		this.icon = icon;
		
		this.mediaType = mediaType;
	}
}
