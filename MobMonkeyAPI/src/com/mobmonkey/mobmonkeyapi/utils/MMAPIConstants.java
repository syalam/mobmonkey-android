package com.mobmonkey.mobmonkeyapi.utils;

/**
 * Constants class that stores all the data values for MobMonkeyAPI
 * @author Dezapp, LLC
 *
 */
public final class MMAPIConstants {
	
	/**
	 * Private class to prevent the instantiation of this class outfside the scope of this class
	 */
	private MMAPIConstants() {
		throw new AssertionError();
	}
	
	// MobMonkey default data values
	public static final String DEFAULT_STRING = "";
	public static final String DEFAULT_SPACE = " ";
	public static final String DEFAULT_NEWLINE = "\n";
	public static final String COMMA_SPACE = ", ";
	public static final int DEFAULT_INT = -1;
	public static final double DEFAULT_DOUBLE = 1.0d;

	public static final String MOBMONKEY_URL = "http://staging.mobmonkey.com/rest/";
	public static final String TEST_MOBMONKEY_URL = "http://staging.mobmonkey.com/rest/";
	public static final String TEST_MOBMONKEY_GCM_URL = "http://staging.mobmonkey.com";
	
	public static final String TAB_TITLE_TRENDING_NOW = "Trending Now";
	public static final String TAB_TITLE_INBOX = "Inbox";
	public static final String TAB_TITLE_SEARCH = "Search";
	public static final String TAB_TITLE_FAVORITES = "Favorites";
	public static final String TAB_TITLE_SETTINGS = "Settings";
	
	public static final String TEXT_MALE = "Male";
	public static final String TEXT_FEMALE = "Female";
	public static final int NUM_MALE = 1;
	public static final int NUM_FEMALE = 0;
	
	public static final String FACEBOOK_REQ_PERM_EMAIL = "email";

	public static final String MEDIA_TYPE_VIDEO = "video";
	public static final String MEDIA_TYPE_IMAGE = "image";
	public static final String MEDIA_TYPE_TEXT = "text";
	
	public static final int HISTORY_SIZE = 10;
	public static final int DAYS_PREVIOUS = 6;

	// SharePreferences Key
	public static final String USER_PREFS = "User Preferences";
	public static final String SHARED_PREFS_KEY_HISTORY = "history";
	public static final String SHARED_PREFS_KEY_TOP_LEVEL_CATEGORIES = "Top level categories";
	public static final String SHARED_PREFS_KEY_ALL_CATEGORIES = "All categories";
	public static final String SHARED_PREFS_KEY_SEARCH_RADIUS = "Search radius";
	public static final String SHARED_PREFS_KEY_NARROW_BY_LIVE_VIDEO = "Narrow by live video";
	public static final String SHARED_PREFS_KEY_CATEGORY_LIST = "categoryList";
	public static final String SHARED_PREFS_KEY_BOOKMARKS = "bookmarks";
	
	// Intent extra key values
	public static final String KEY_INTENT_EXTRA_CATEGORY = "Category";
	public static final String KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE = "Search result title";
	public static final String KEY_INTENT_EXTRA_SEARCH_RESULTS = "Search results";
	public static final String KEY_INTENT_EXTRA_DISPLAY_MAP = "Display map";
	public static final String KEY_INTENT_EXTRA_LOCATION = "Location";
	public static final String KEY_INTENT_EXTRA_LOCATION_DETAILS = "Location details";
	public static final String KEY_INTENT_EXTRA_MESSAGE = "Message";
	public static final String KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME = "Schedule request";
	public static final String KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING = "Repeating";
	public static final String KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING_RATE = "Repeating rate";
	public static final String KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED = "Top Viewed";
	
	// MobMonkey server call key values
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
	
	// MobMonkey server url
	public static final String URL_BOOKMARKS = "bookmarks";
	public static final String URL_TOPVIEWED = "topviewed";
	
	// MobMonkey server response key values
	public static final String KEY_RESPONSE_STATUS = "status";
	public static final String KEY_RESPONSE_DESC = "description";
	public static final String KEY_RESPONSE_ID = "id";
	
	public static final String CONTENT_TYPE_APP_JSON = "application/json";
	public static final String DEVICE_TYPE = "Android";
	public static final String OAUTH_PROVIDER_FACEBOOK = "facebook";
	public static final String OAUTH_PROVIDER_TWITTER = "twitter";
	
	// Twitter authentication with twitter4j values
	public static final String TWITTER_CALLBACK_URL = "mobmonkey://com.mobmonkey.mobmonkey?";
	public static final String TWITTER_OAUTH_TOKEN = "oauth_token";
	public static final String TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	
	// MobMonkey server response status values
	public static final String RESPONSE_STATUS_SUCCESS = "Success";
	public static final String RESPONSE_STATUS_FAILURE = "Failure";
	public static final String RESPONSE_ID_SUCCESS = "200";
	public static final String RESPONSE_ID_NOT_FOUND = "404";
	public static final String RESPONSE_ID_BAD_REQUEST = "500";	
	
	// Request code values
	public static final String REQUEST_CODE = "Request code";
	
	public static final int REQUEST_CODE_SIGN_IN_TWITTER_AUTH = 1000;
	public static final int REQUEST_CODE_SIGN_UP_TWITTER_AUTH = 1001;
	public static final int REQUEST_CODE_SIGN_UP_TWITTER = 2000;
	public static final int REQUEST_CODE_TURN_ON_GPS_ADD_LOCATION = 10000;
	public static final int REQUEST_CODE_TURN_ON_GPS_SEARCH_TEXT = 10001;
	public static final int REQUEST_CODE_TURN_ON_GPS_SEARCH_ALL_NEARBY = 10002;
	public static final int REQUEST_CODE_TURN_ON_GPS_HISTORY = 10003;
	public static final int REQUEST_CODE_TURN_ON_GPS_SEARCH_CATEGORY = 10004;
	public static final int REQUEST_CODE_ADD_MESSAGE = 100000;
	public static final int REQUEST_CODE_SCHEDULE_REQUEST = 100001;
	
	// Result code values
	public static final int RESULT_CODE_SUCCESS = 200;
	public static final int RESULT_CODE_NOT_FOUND = 404;
	
	// JSON key values
	public static final String JSON_KEY_ADDRESS = "address";
	public static final String JSON_KEY_ADDRESS_EXT = "address_ext";
	public static final String JSON_KEY_BOOKMARK_COUNT = "bookmarkCount";
	public static final String JSON_KEY_CATEGORY_ID = "categoryId";
	public static final String JSON_KEY_CATEGORY_IDS = "categoryIds";
	public static final String JSON_KEY_COUNTRY_CODE = "countryCode";
	public static final String JSON_KEY_DESCRIPTION = "description";	
	public static final String JSON_KEY_DISTANCE = "distance";
	public static final String JSON_KEY_DURATION = "duration";
	public static final String JSON_KEY_FREQUENCY_IN_MS = "frequencyInMS";
	public static final String JSON_KEY_INTEREST_COUNT = "interestCount";
	public static final String JSON_KEY_LATITUDE = "latitude";
	public static final String JSON_KEY_LOCALITY = "locality";
	public static final String JSON_KEY_LOCATION_ID = "locationId";
	public static final String JSON_KEY_LONGITUDE = "longitude";
	public static final String JSON_KEY_MESSAGE = "message";
	public static final String JSON_KEY_MONKEYS = "monkeys";	
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_NEARBY_COUNT = "nearbyCount";
	public static final String JSON_KEY_NEIGHBORHOOD = "neighborhood";
	public static final String JSON_KEY_PARENTS = "parents";
	public static final String JSON_KEY_PHONENUMBER = "phoneNumber";
	public static final String JSON_KEY_POSTCODE = "postcode";
	public static final String JSON_KEY_PROVIDER_ID = "providerId";
	public static final String JSON_KEY_RADIUS_IN_YARDS = "radiusInYards";
	public static final String JSON_KEY_RECURRING = "recurring";
	public static final String JSON_KEY_REGION = "region";
	public static final String JSON_KEY_SCHEDULEDATE = "scheduleDate";
	public static final String JSON_KEY_TOP_VIEWED_COUNT = "topviewedCount";
	public static final String JSON_KEY_WEBSITE = "website";
	
	// Search Radius (in yards)
	public static final int SEARCH_RADIUS_HALF_MILE = 880;
	public static final int SEARCH_RADIUS_ONE_MILE = 1760;
	public static final int SEARCH_RADIUS_FIVE_MILE = 8800;
	public static final int SEARCH_RADIUS_TEN_MILE = 17600;
	public static final int SEARCH_RADIUS_TWENTY_MILE = 35200;
	
	// Search day 
	public static final String SEARCH_TIME_DAY = "day";
	public static final String SEARCH_TIME_WEEK = "week";
	public static final String SEARCH_TIME_MONTH = "month";
	
	public static final String INTENT_FILTER_DISPLAY_MESSAGE = "com.mobmonkey.mobmonkey.DISPLAY_MESSAGE";
}
