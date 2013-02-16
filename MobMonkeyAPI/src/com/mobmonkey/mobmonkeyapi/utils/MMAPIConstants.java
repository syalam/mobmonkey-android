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
	
	public static final String USER_PREFS = "User Preferences";
	
	public static final String MOBMONKEY_URL = "http://api.mobmonkey.com/rest/";
	
	public static final String TEXT_MALE = "Male";
	public static final String TEXT_FEMALE = "Female";
	public static final int NUM_MALE = 1;
	public static final int NUM_FEMALE = 0;
	
	public static final String FACEBOOK_REQ_PERM_EMAIL = "email";
	
	public static final String INTENT_EXTRA_SEARCH_RESULT_TITLE = "Search result title";
	public static final String INTENT_EXTRA_SEARCH_LOCATION = "Location";
	public static final String INTENT_EXTRA_SEARCH_RESULTS = "Search results";
	
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
	
	public static final String KEY_RESPONSE_STATUS = "status";
	public static final String KEY_RESPONSE_DESC = "description";
	public static final String KEY_RESPONSE_ID = "id";
	
	public static final String CONTENT_TYPE_APP_JSON = "application/json";
	public static final String DEVICE_TYPE = "Android";
	public static final String OAUTH_PROVIDER_FACEBOOK = "facebook";
	public static final String OAUTH_PROVIDER_TWITTER = "twitter";
	
	public static final String TWITTER_CALLBACK_URL = "mobmonkey://com.mobmonkey.mobmonkey?";
	public static final String TWITTER_CALLBACK_URL_SIGN_IN = "signin://com.mobmonkey.mobmonkey?";
	public static final String TWITTER_CALLBACK_URL_SIGN_UP = "signup://com.mobmonkey.mobmonkey?";
	public static final String TWITTER_OAUTH_TOKEN = "oauth_token";
	public static final String TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	
	public static final String RESPONSE_STATUS_SUCCESS = "Success";
	public static final String RESPONSE_STATUS_FAILURE = "Failure";
	public static final String RESPONSE_ID_SUCCESS = "200";
	public static final String RESPONSE_ID_NOT_FOUND = "404";
	public static final String RESPONSE_ID_BAD_REQUEST = "500";	
	
	public static final String REQUEST_CODE = "Request code";
	
	public static final int REQUEST_CODE_SIGN_IN_TWITTER_AUTH = 1000;
	public static final int REQUEST_CODE_SIGN_UP_TWITTER_AUTH = 1001;
	public static final int REQUEST_CODE_SIGN_UP_TWITTER = 2000;
	
	public static final int RESULT_CODE_SUCCESS = 200;
	public static final int RESULT_CODE_NOT_FOUND = 404;
}
