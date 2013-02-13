package com.mobmonkey.mobmonkeyapi.utils;

/**
 * @author Dezapp, LLC
 *
 */
public final class MMAPIConstants {
	private MMAPIConstants() {
		throw new AssertionError();
	}
	
	public static final String DEFAULT_STRING = "";
	public static final int DEFAULT_INT = -1;
	public static final double DEFAULT_DOUBLE = 1.0d;
	
	public static final String URL = "http://api.mobmonkey.com/rest/";
	
	public static final String KEY_FIRST_NAME = "firstName";
	public static final String KEY_LAST_NAME = "lastName";
	public static final String KEY_EMAIL_ADDRESS = "eMailAddress";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_BIRTHDATE = "birthday";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_PHONE_NUMBER = "phoneNumber";
	public static final String KEY_CITY = "city";
	public static final String KEY_STATE = "state";
	public static final String KEY_ZIP = "zip";
	public static final String KEY_ACCEPTEDTOS = "acceptedtos";
	public static final String KEY_DEVICE_ID = "deviceId";
	public static final String KEY_DEVICE_TYPE = "deviceType";
	public static final String KEY_CONTENT_TYPE = "Content-Type";
	public static final String KEY_PARTNER_ID = "MobMonkey-partnerId";
	public static final String KEY_USER = "MobMonkey-user";
	public static final String KEY_AUTH = "MobMonkey-auth";
	public static final String KEY_OAUTH_PROVIDER = "OauthProvider";
	public static final String KEY_OAUTH_PROVIDER_USER_NAME = "OauthProviderUserName";
	public static final String KEY_OAUTH_TOKEN = "OauthToken";
	public static final String KEY_OAUTH_TOKEN_SECRET = "OauthTokenSecret";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_RADIUS_IN_YARDS = "radiusInYards";
	public static final String KEY_NAME = "name";
	public static final String KEY_CATEGORY_IDS = "categoryIds";
	
	public static final String CONTENT_TYPE_APP_JSON = "application/json";
	public static final String DEVICE_TYPE = "Android";
	public static final String OAUTH_PROVIDER_FACEBOOK = "facebook";
	public static final String OAUTH_PROVIDER_TWITTER = "twitter";
	
	public static final String TEXT_MALE = "Male";
	public static final String TEXT_FEMALE = "Female";
	public static final int NUM_MALE = 1;
	public static final int NUM_FEMALE = 0;
	
	public static final String TWITTER_CALLBACK_URL = "mobmonkey://com.mobmonkey.mobmonkey?";
	public static final String TWITTER_OAUTH_TOKEN = "oauth_token";
	public static final String TWITTER_OAUTH_VERIFIER = "oauth_verifier";
}
