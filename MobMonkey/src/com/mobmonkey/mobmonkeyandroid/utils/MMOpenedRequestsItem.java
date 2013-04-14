package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;

public class MMOpenedRequestsItem {

	public String title, dis, message, time;
	public int icon, mediaType;
	
	public MMOpenedRequestsItem() {
		super();
	}
	
	public MMOpenedRequestsItem(String title, String dis, String message, String time, int icon, int mediaType) throws IOException {
		super();
		this.title = title;
		this.dis = dis;
		this.message = message;
		this.time = time;
		this.icon = icon;
		
		this.mediaType = mediaType;
	}
}
