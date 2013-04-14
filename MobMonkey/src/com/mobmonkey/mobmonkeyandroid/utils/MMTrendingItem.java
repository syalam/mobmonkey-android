package com.mobmonkey.mobmonkeyandroid.utils;

import java.io.IOException;

public class MMTrendingItem {

	public String title;
	public String counter;
	
	public MMTrendingItem() {
		super();
	}
	
	public MMTrendingItem(String title, String counter) throws IOException {
		super();
		this.title = title;
		
		this.counter = counter;
	}
}
