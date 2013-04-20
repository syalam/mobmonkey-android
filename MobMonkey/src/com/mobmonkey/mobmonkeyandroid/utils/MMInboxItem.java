package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;

public class MMInboxItem {

	public String title;
	public int counter;
	public int containCounter;
	
	public MMInboxItem() {
		super();
	}
	
	public MMInboxItem(String title, int counter, int containCounter) throws IOException {
		this.title = title;		
		this.counter = counter;
		this.containCounter = containCounter;
	}
}
