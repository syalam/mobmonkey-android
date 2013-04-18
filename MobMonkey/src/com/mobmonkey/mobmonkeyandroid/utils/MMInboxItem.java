package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;

public class MMInboxItem {

	public String title;
	public String counter;
	public int containCounter;
	
	public MMInboxItem() {
		super();
	}
	
	public MMInboxItem(String title, String counter, int containCounter) throws IOException {
		super();
		this.title = title;
		
		this.counter = counter;
		this.containCounter = containCounter;
	}
}
