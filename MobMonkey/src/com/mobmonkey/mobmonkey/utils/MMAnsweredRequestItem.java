package com.mobmonkey.mobmonkey.utils;

import java.io.IOException;

public class MMAnsweredRequestItem {

	public String title, dis, message, time;
	public int icon, mediaType;
	
	public MMAnsweredRequestItem() {
		super();
	}
	
	public MMAnsweredRequestItem(String title, String dis, String message, String time, int icon, int mediaType) throws IOException {
		super();
		this.title = title;
		this.dis = dis;
		this.message = message;
		this.time = time;
		this.icon = icon;
		
		this.mediaType = mediaType;
	}
}
