package com.mobmonkey.mobmonkeysdk.utils;

import java.io.File;

import android.os.Environment;

/**
 * Constants class that stores all the data values for MobMonkeyAPI
 * @author Dezapp, LLC
 *
 */
public final class MMSDKConstants {
	
	/**
	 * Private class to prevent the instantiation of this class outfside the scope of this class
	 */
	private MMSDKConstants() {
		throw new AssertionError();
	}
	
	// MobMonkey default data values
	public static final String DEFAULT_STRING_EMPTY = "";
	public static final String DEFAULT_STRING_COMMA = ",";
	public static final String DEFAULT_STRING_COMMA_NEWLINE = ",\n";
	public static final String DEFAULT_STRING_COMMA_SPACE = ", ";
	public static final String DEFAULT_STRING_NEWLINE = "\n";
	public static final String DEFAULT_STRING_NULL = "null";
	public static final String DEFAULT_STRING_SPACE = " ";
	public static final int DEFAULT_INT = -1;
	public static final int DEFAULT_INT_ZERO = 0;
	public static final double DEFAULT_DOUBLE = 1.0d;

	public static final String DECIMAL_FORMAT_SIX = "#.######";
	public static final String DATE_FORMAT_MMMM_DD_HH_SEMICOLON_MMA = "MMMM dd hh:mma";
	public static final String DATE_FORMAT_MMM_DD_COMMA_YYYY = "MMM dd, yyyy";
	
	public static final String MOBMONKEY_URL = "http://api.mobmonkey.com/rest/";
	public static final String TEST_MOBMONKEY_GCM_URL = "http://staging.mobmonkey.com";
	
	public static final String MOBMONKEY_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + "MobMonkey";
	public static final String MOBMONKEY_RECORDED_VIDEO_FILENAME = Environment.getExternalStorageDirectory() + File.separator + "MobMonkey" + File.separator + "mmvideo.mp4";
	
	// Uri path values
	public static final String URI_PATH_ASSIGNEDREQUESTS = "assignedrequests";
	public static final String URI_PATH_ANSWEREDREQUESTS = "fulfilledrequests";
	public static final String URI_PATH_CATEGORY = "category";
	public static final String URI_PATH_CHECKIN = "checkin";
	public static final String URI_PATH_COUNTS = "counts";
	public static final String URI_PATH_FAVORITES = "bookmarks";
	public static final String URI_PATH_INBOX = "inbox";
	public static final String URI_PATH_LOCATION = "location";
	public static final String URI_PATH_MEDIA = "media";
	public static final String URI_PATH_OPENREQUESTS = "openrequests";
	public static final String URI_PATH_PAID_SUBSCRIPTION = "paidsubscription";
	public static final String URI_PATH_REGISTEREMAIL = "registeremail";
	public static final String URI_PATH_REQUEST = "request";
	public static final String URI_PATH_REQUESTMEDIA = "requestmedia";
	public static final String URI_PATH_SEARCH = "search";
	public static final String URI_PATH_SIGNIN = "signin";
	public static final String URI_PATH_SIGNOUT = "signout";
	public static final String URI_PATH_TOP_VIEWED = "topviewed";
	public static final String URI_PATH_TRENDING = "trending";
	public static final String URI_PATH_USER = "user";
	
	// Uri query parameter keys
	public static final String URI_QUERY_PARAM_KEY_CATEGORY_IDS = "categoryIds";
	public static final String URI_QUERY_PARAM_KEY_COUNTS_ONLY = "countsonly";
	public static final String URI_QUERY_PARAM_KEY_EMAIL = "email";
	public static final String URI_QUERY_PARAM_KEY_FAVORITES_ONLY = "bookmarksonly";
	public static final String URI_QUERY_PARAM_KEY_LATITUDE = "latitude";
	public static final String URI_QUERY_PARAM_KEY_LONGITUDE = "longitude";
	public static final String URI_QUERY_PARAM_KEY_MY_INTEREST = "myinterests";
	public static final String URI_QUERY_PARAM_KEY_NEARBY = "nearby";
	public static final String URI_QUERY_PARAM_KEY_PARTNER_ID = "partnerId";
	public static final String URI_QUERY_PARAM_KEY_RADIUS = "radius";
	public static final String URI_QUERY_PARAM_KEY_TIME_SPAN = "timeSpan";
	
	// Tab tags
	public static final String TAB_TITLE_CURRENT_TAG = "Current tab tag";
	public static final String TAB_TITLE_TRENDING_NOW = "Trending Now";
	public static final String TAB_TITLE_INBOX = "Inbox";
	public static final String TAB_TITLE_SEARCH = "Search";
	public static final String TAB_TITLE_FAVORITES = "Favorites";
	public static final String TAB_TITLE_SETTINGS = "Settings";
	
	public static final String TEXT_MALE = "male";
	public static final String TEXT_FEMALE = "female";
	public static final int NUM_MALE = 1;
	public static final int NUM_FEMALE = 0;
	
	public static final int FAVORITES_FRAGMENT_LIST = 0;
	public static final int FAVORITES_FRAGMENT_MAP = 1;
	
	public static final String FACEBOOK_REQ_PERM_EMAIL = "email";
	public static final String FACEBOOK_REQ_PERM_BIRTHDAY = "user_birthday";
	public static final String FACEBOOK_REQ_PERM_GENDER = "gender";

	// Media and media type
	public static final String MEDIA_LIVESTREAMING = "livestreaming";
	public static final String MEDIA_VIDEO = "video";
	public static final String MEDIA_IMAGE = "image";
	public static final String MEDIA_TEXT = "text";
	
	public static final int MEDIA_TYPE_LIVESTREAMING = 0;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	// Schedule Request date wheel
	public static final int HISTORY_SIZE = 10;
	public static final int DAYS_PREVIOUS = 6;

	// SharePreferences Key
	public static final String USER_PREFS = "User Preferences";
	public static final String SHARED_PREFS_KEY_ALL_CATEGORIES = "All categories";
	public static final String SHARED_PREFS_KEY_CATEGORY_LIST = "categoryList";
	public static final String SHARED_PREFS_KEY_FAVORITES = "favorites";
	public static final String SHARED_PREFS_KEY_HISTORY = "history";
	public static final String SHARED_PREFS_KEY_NARROW_BY_LIVE_VIDEO = "Narrow by live video";
	public static final String SHARED_PREFS_KEY_SEARCH_RADIUS = "Search radius";
	public static final String SHARED_PREFS_KEY_TOP_LEVEL_CATEGORIES = "Top level categories";
	public static final String SHARED_PREFS_KEY_TOS_FACEBOOK = "ToS Facebook";
	public static final String SHARED_PREFS_KEY_TOS_TWITTER = "ToS Twitter";
	
	// Intent extra/Bundle key values
	public static final String KEY_INTENT_EXTRA_CATEGORY = "Category";
	public static final String KEY_INTENT_EXTRA_DISPLAY_MAP = "Display map";
	public static final String KEY_INTENT_EXTRA_IMAGE_MEDIA = "Image media";
	public static final String KEY_INTENT_EXTRA_INBOX_REQUESTS = "Inbox requests";
	public static final String KEY_INTENT_EXTRA_LOCATION = "Location";
	public static final String KEY_INTENT_EXTRA_LOCATION_DETAILS = "Location details";
	public static final String KEY_INTENT_EXTRA_MEDIA_TYPE = "Media type";
	public static final String KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_WIDTH = "Media thumbnail width";
	public static final String KEY_INTENT_EXTRA_MEDIA_THUMBNAIL_HEIGHT = "Media thumbnail height";
	public static final String KEY_INTENT_EXTRA_MESSAGE = "Message";
	public static final String KEY_INTENT_EXTRA_SCHEDULE_REQUEST_TIME = "Schedule request";
	public static final String KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING = "Repeating";
	public static final String KEY_INTENT_EXTRA_SCHEDULE_REQUEST_REPEATING_RATE = "Repeating rate";
	public static final String KEY_INTENT_EXTRA_SEARCH_RESULTS = "Search results";
	public static final String KEY_INTENT_EXTRA_SEARCH_RESULT_TITLE = "Search result title";
	public static final String KEY_INTENT_EXTRA_TOS_DISPLAY_BUTTON = "ToS display button";
	public static final String KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED = "Top Viewed";
	public static final String KEY_INTENT_EXTRA_VIDEO_PATH = "returnedVideo";
	public static final String KEY_INTENT_EXTRA_ADD_CATEGORY = "add category";
	public static final String KEY_INTENT_EXTRA_ADD_CATEGORY_IDS = "add categoryIds";
	
	// MobMonkey server call key values
	public static final String KEY_ACCEPTEDTOS = "acceptedtos";
	public static final String KEY_AUTH = "MobMonkey-auth";
	public static final String KEY_BIRTHDATE = "birthday";
	public static final String KEY_CATEGORY_IDS = "categoryIds";	
	public static final String KEY_CITY = "city";
	public static final String KEY_CONTENT_TYPE = "Content-Type";
	public static final String KEY_DEVICE_ID = "deviceId";
	public static final String KEY_DEVICE_TYPE = "deviceType";
	public static final String KEY_EMAIL_ADDRESS = "eMailAddress";
	public static final String KEY_FIRST_NAME = "firstName";	
	public static final String KEY_GENDER = "gender";	
	public static final String KEY_LAST_NAME = "lastName";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LOCALITY = "locality";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_NAME = "name";
	public static final String KEY_OAUTH_PROVIDER = "OauthProvider";
	public static final String KEY_OAUTH_PROVIDER_USER_NAME = "OauthProviderUserName";
	public static final String KEY_OAUTH_TOKEN = "oauthToken";
	public static final String KEY_OAUTH_TOKEN_SECRET = "OauthTokenSecret";
	public static final String KEY_PARTNER_ID = "MobMonkey-partnerId";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_PHONE_NUMBER = "phoneNumber";
	public static final String KEY_POST_CODE = "postcode";
	public static final String KEY_PROVIDER = "provider";
	public static final String KEY_PROVIDER_USERNAME = "providerUserName";
	public static final String KEY_RADIUS_IN_YARDS = "radiusInYards";
	public static final String KEY_REGION = "region";
	public static final String KEY_STATE = "state";
	public static final String KEY_STREET_ADDRESS = "streetAddress";
	public static final String KEY_USE_OAUTH = "useOAuth";
	public static final String KEY_OAUTH_USER = "oAuthUser";
	public static final String KEY_USER = "MobMonkey-user";
	public static final String KEY_ZIP = "zip";
	
	// MobMonkey server call header key values
	public static final String CONTENT_TYPE_APP_JSON = "application/json";
	public static final String DEVICE_TYPE = "Android";
	public static final String OAUTH_PROVIDER_FACEBOOK = "facebook";
	public static final String OAUTH_PROVIDER_TWITTER = "twitter";
	
	// Twitter authentication with twitter4j values
	public static final String TWITTER_CALLBACK_URL = "mobmonkey://com.mobmonkey.mobmonkeyandroid?";
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

	public static final int REQUEST_CODE_TOS_FACEBOOK = 1000;
	public static final int REQUEST_CODE_TOS_TWITTER = 2000;
	public static final int REQUEST_CODE_SIGN_IN_TWITTER_AUTH = 2001;
	public static final int REQUEST_CODE_SIGN_UP_TWITTER_AUTH = 2002;
	public static final int REQUEST_CODE_SIGN_UP_TWITTER = 2003;
	public static final int REQUEST_CODE_TURN_ON_GPS_LOCATION = 5000;
	public static final int REQUEST_CODE_IMAGE = 10000;
	public static final int REQUEST_CODE_VIDEO = 10001;
	public static final int REQUEST_CODE_ADD_MESSAGE = 100000;
	public static final int REQUEST_CODE_ADD_CATEGORY = 10000011;
	public static final int REQUEST_CODE_SCHEDULE_REQUEST = 100001;
	
	// Result code values
	public static final int RESULT_CODE_SUCCESS = 200;
	public static final int RESULT_CODE_NOT_FOUND = 404;
	
	// JSON key values
	public static final String JSON_KEY_ACCEPTED = "accepted";
	public static final String JSON_KEY_ADDRESS = "address";
	public static final String JSON_KEY_ADDRESS_EXT = "address_ext";
	public static final String JSON_KEY_ASSIGNED_REQUESTS = "assignedrequests";
	public static final String JSON_KEY_ASSIGNED_READ_REQUESTS = "assignedReadRequests";
	public static final String JSON_KEY_ASSIGNED_UNREAD_REQUESTS = "assignedUnreadRequests";
	public static final String JSON_KEY_BOOKMARK_COUNT = "bookmarkCount";
	public static final String JSON_KEY_CATEGORY_ID = "categoryId";
	public static final String JSON_KEY_CATEGORY_IDS = "categoryIds";
	public static final String JSON_KEY_CONTENT_TYPE = "contentType";
	public static final String JSON_KEY_COUNTRY_CODE = "countryCode";
	public static final String JSON_KEY_DEFAULT_TEXTS = "defaultTexts";
	public static final String JSON_KEY_DESCRIPTION = "description";	
	public static final String JSON_KEY_DISTANCE = "distance";
	public static final String JSON_KEY_DURATION = "duration";
	public static final String JSON_KEY_EXPIRY_DATE = "expiryDate";
	public static final String JSON_KEY_FREQUENCY_IN_MS = "frequencyInMS";
	public static final String JSON_KEY_FULFILLED_DATE = "fulfilledDate";
	public static final String JSON_KEY_FULFILLED_READ_COUNT = "fulfilledReadCount";
	public static final String JSON_KEY_FULFILLED_UNREAD_COUNT = "fulfilledUnreadCount";
	public static final String JSON_KEY_ID = "id";
	public static final String JSON_KEY_IMAGES = "images";
	public static final String JSON_KEY_INTEREST_COUNT = "interestCount";
	public static final String JSON_KEY_IS_RECURRING = "isRecurring";
	public static final String JSON_KEY_LATITUDE = "latitude";
	public static final String JSON_KEY_LOCALITY = "locality";
	public static final String JSON_KEY_LOCATION_ID = "locationId";
	public static final String JSON_KEY_LONGITUDE = "longitude";
	public static final String JSON_KEY_MARK_AS_READ = "markAsRead";
	public static final String JSON_KEY_MEDIA = "media";
	public static final String JSON_KEY_MEDIA_DATA = "mediaData";
	public static final String JSON_KEY_MEDIA_ID = "mediaId";
	public static final String JSON_KEY_MEDIA_TYPE = "mediaType";
	public static final String JSON_KEY_MEDIA_URL = "mediaURL";
	public static final String JSON_KEY_MESSAGE = "message";
	public static final String JSON_KEY_MONKEYS = "monkeys";	
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_NAME_OF_LOCATION = "nameOfLocation";
	public static final String JSON_KEY_NEARBY_COUNT = "nearbyCount";
	public static final String JSON_KEY_NEIGHBORHOOD = "neighborhood";
	public static final String JSON_KEY_OPEN_REQUESTS = "OpenRequests";
	public static final String JSON_KEY_OPEN_REQUESTS_COUNT = "openrequests";
	public static final String JSON_KEY_PARENTS = "parents";
	public static final String JSON_KEY_PHONE_NUMBER = "phoneNumber";
	public static final String JSON_KEY_POSTCODE = "postcode";
	public static final String JSON_KEY_PROVIDER_ID = "providerId";
	public static final String JSON_KEY_RADIUS_IN_YARDS = "radiusInYards";
	public static final String JSON_KEY_RECURRING = "recurring";
	public static final String JSON_KEY_REQUEST_FULFILLED = "requestFulfilled";
	public static final String JSON_KEY_REGION = "region";
	public static final String JSON_KEY_REQUEST_DATE = "requestDate";
	public static final String JSON_KEY_REQUEST_ID = "requestId";
	public static final String JSON_KEY_REQUEST_TYPE = "requestType";
	public static final String JSON_KEY_SCHEDULE_DATE = "scheduleDate";
	public static final String JSON_KEY_STATUS = "status";
	public static final String JSON_KEY_TOP_VIEWED_COUNT = "topviewedCount";
	public static final String JSON_KEY_TOTAL_ITEMS = "totalItems";
	public static final String JSON_KEY_TYPE = "type";
	public static final String JSON_KEY_UPLOADED_DATE = "uploadedDate";
	public static final String JSON_KEY_VIDEOS = "videos";
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
	
	// Media content type
	public static final String MEDIA_CONTENT_JPG = "image/jpg";
	public static final String MEDIA_CONTENT_JPEG = "image/jpeg";
	public static final String MEDIA_CONTENT_PNG = "image/png";
	public static final String MEDIA_CONTENT_MP4 = "video/mp4";
	public static final String MEDIA_CONTENT_MPEG = "video/mpeg";
	public static final String MEDIA_CONTENT_QUICKTIME = "video/quicktime";
	
	// Repeat rate
	public static final String REQUEST_REPEAT_RATE_NONE = "none";
	public static final String REQUEST_REPEAT_RATE_DAILY = "daily";
	public static final String REQUEST_REPEAT_RATE_WEEKLY = "weekly";
	public static final String REQUEST_REPEAT_RATE_MONTHLY = "monthly";
	
	public static final int TIMEOUT_CONNECTION = 10 * 1000;
	public static final int TIMEOUT_SOCKET = 30 * 1000;
	
	public static final String CONNECTION_TIMED_OUT = "Connection timed out";
	public static final String OPERATION_TIMED_OUT = "The operation timed out";
	
	public static final String INTENT_FILTER_DISPLAY_MESSAGE = "com.mobmonkey.mobmonkeyandroid.DISPLAY_MESSAGE";
}
