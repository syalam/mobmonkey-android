package com.mobmonkey.mobmonkey.utils;

import java.io.IOException;

public class MMInboxItem {

	public String title;
	public String counter;
	
	public MMInboxItem() {
		super();
	}
	
	public MMInboxItem(String title, String counter) throws IOException {
		super();
		this.title = title;
		
		this.counter = counter;
	}
}
