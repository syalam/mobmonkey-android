package com.mobmonkey.mobmonkeysdk.utils;

/**
 * MobMonkey custom callback interface
 * @author Dezapp, LLC
 *
 */
public interface MMCallback {

	/**
	 * Function to be invoke for the {@link MMCallback}
	 * @param obj {@link Object} to be processed
	 */
	public void processCallback(Object obj);
}
