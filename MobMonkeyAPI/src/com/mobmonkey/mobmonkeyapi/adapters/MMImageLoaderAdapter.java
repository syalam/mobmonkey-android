package com.mobmonkey.mobmonkeyapi.adapters;

import com.mobmonkey.mobmonkeyapi.utils.MMCallback;
import com.mobmonkey.mobmonkeyapi.utils.MMImageLoaderTask;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMImageLoaderAdapter {
	public static void loadImage(MMCallback mmCallback, String imageUrl) {
		new MMImageLoaderTask(mmCallback).execute(imageUrl);
	}
}
