package com.mobmonkey.mobmonkey;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkey.utils.MMProgressDialog;
import com.mobmonkey.mobmonkeyapi.adapters.MMUserAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * @author Dezapp, LLC
 * 
 */
public class TwitterAuthScreen extends Activity {
	private static final String TAG = "TwitterAuthScreen: ";
	
	private SharedPreferences userPrefs;
	private SharedPreferences.Editor userPrefsEditor;
	
	private WebView wvTwitterAuth;
	
	private Twitter twitter;
	private RequestToken requestToken;
	private AccessToken twitterAccessToken;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_hold);
		setContentView(R.layout.twitter_auth_screen);
		init();
		startTwitterAuth();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
	}

	/**
	 * Initialize all the variables to be used in {@link TwitterAuthScreen}
	 */
	private void init() {
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		wvTwitterAuth = (WebView) findViewById(R.id.wvtwitterauth);
	}
	
	/**
	 * Function creates the {@link Twitter} object with the Twitter consumer key and consumer secret. It creates {@link RequestToken} to obtain the url for authentication and load the url in a {@link WebView}.
	 * NOTE: Not launching a browser with the authentication url due to the fact the current {@link Activity} need to have launchMode=singleTask in the manifest to handle callback url from Twitter. The 
	 * 		{@link WebViewClient} will handle the callback url for Twitter authentication.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void startTwitterAuth() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(MMConstants.TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(MMConstants.TWITTER_CONSUMER_SECRET);
		builder.setOAuthAccessToken(null);
		builder.setOAuthAccessTokenSecret(null);
		Configuration configuration = builder.build();
		
		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();
		
		try {
			requestToken = twitter.getOAuthRequestToken(MMAPIConstants.TWITTER_CALLBACK_URL);
			wvTwitterAuth.getSettings().setJavaScriptEnabled(true);
			wvTwitterAuth.setWebViewClient(new MobMonkeyWebViewClient());
			wvTwitterAuth.loadUrl(requestToken.getAuthenticationURL());
			MMProgressDialog.dismissDialog();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that parse Uri converted Twitter authentication callback url. It will obtain the {@link AccessToken} for {@link Twitter} from the OAuth_verifier it will receive after authentication. 
	 * @param uri
	 */
	private void parseUri(Uri uri) {
		try {
			if(uri.getQueryParameter(MMAPIConstants.TWITTER_OAUTH_VERIFIER) != null) {
				twitterAccessToken = twitter.getOAuthAccessToken(requestToken, uri.getQueryParameter(MMAPIConstants.TWITTER_OAUTH_VERIFIER));
				MMUserAdapter.signInUserTwitter(new TwitterAuthCallback(), twitterAccessToken.getToken(), twitterAccessToken.getScreenName(), MMConstants.PARTNER_ID);
				
				int requestCode = getIntent().getIntExtra(MMAPIConstants.REQUEST_CODE, MMAPIConstants.DEFAULT_INT);
				
				// Depend on which Activity it was called from, it will display the appropriate signin/signup message
				if(requestCode == MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH) {
					MMProgressDialog.displayDialog(TwitterAuthScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_in_twitter));
				} else if(requestCode == MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER_AUTH) {
					MMProgressDialog.displayDialog(TwitterAuthScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_up_twitter));
				}
			} else {
				finish();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Custom {@link WebViewClient} specifically to handle the callback url of Twitter
	 * @author Dezapp, LLC
	 *
	 */
	private class MobMonkeyWebViewClient extends WebViewClient {
		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(url.contains(MMAPIConstants.TWITTER_CALLBACK_URL)) {
				parseUri(Uri.parse(url));
				return true;
			}
			return false;
		}
	}
	
    /**
     * Custom {@link MMCallback} specifically for {@link TwitterAuthScreen} to be processed after receiving response from MobMonkey server.
     * @author Dezapp, LLC
     *
     */
	private class TwitterAuthCallback implements MMCallback {
		public void processCallback(Object obj) {
			MMProgressDialog.dismissDialog();
			
			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString(MMAPIConstants.KEY_RESPONSE_ID).equals(MMAPIConstants.RESPONSE_ID_SUCCESS)) {
					setResult(MMAPIConstants.RESULT_CODE_SUCCESS);
					userPrefsEditor.putString(MMAPIConstants.KEY_USER, twitterAccessToken.getScreenName());
					userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, twitterAccessToken.getToken());
					userPrefsEditor.putBoolean(MMAPIConstants.KEY_OAUTH_USER, true);
					userPrefsEditor.commit();
				} else if(response.getString(MMAPIConstants.KEY_RESPONSE_ID).equals(MMAPIConstants.RESPONSE_ID_NOT_FOUND)) {
					Intent resultIntent = new Intent();
					resultIntent.putExtra(MMAPIConstants.KEY_OAUTH_PROVIDER_USER_NAME, twitterAccessToken.getScreenName());
					resultIntent.putExtra(MMAPIConstants.KEY_OAUTH_TOKEN, twitterAccessToken.getToken());
					setResult(MMAPIConstants.RESULT_CODE_NOT_FOUND, resultIntent);
				} else {
					Toast.makeText(TwitterAuthScreen.this, response.getString(MMAPIConstants.KEY_RESPONSE_DESC), Toast.LENGTH_LONG).show();
				}
				finish();
				overridePendingTransition(R.anim.slide_hold, R.anim.slide_right_out);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
