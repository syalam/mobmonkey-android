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

import com.facebook.Session;
import com.mobmonkey.mobmonkey.utils.MMConstants;
import com.mobmonkey.mobmonkeyapi.adapters.MMSignInAdapter;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;
import com.mobmonkey.mobmonkeyapi.utils.MMCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
	
	SharedPreferences userPrefs;
	SharedPreferences.Editor userPrefsEditor;

	ProgressDialog progressDialog;
	
	WebView wvTwitterAuth;
	
	Twitter twitter;
	RequestToken requestToken;
	AccessToken twitterAccessToken;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("SetJavaScriptEnabled") @Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreate");
		super.onCreate(savedInstanceState);
		
		userPrefs = getSharedPreferences(MMAPIConstants.USER_PREFS, MODE_PRIVATE);
		userPrefsEditor = userPrefs.edit();
		
		setContentView(R.layout.twitter_auth_screen);
		wvTwitterAuth = (WebView) findViewById(R.id.wvtwitterauth); 
		wvTwitterAuth.getSettings().setJavaScriptEnabled(true);
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(MMConstants.TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(MMConstants.TWITTER_CONSUMER_SECRET);
		builder.setOAuthAccessToken(null);
		builder.setOAuthAccessTokenSecret(null);
		Configuration configuration = builder.build();
		
		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();
		
		try {
			requestToken = twitter.getOAuthRequestToken(MMAPIConstants.TWITTER_CALLBACK_URL_SIGN_IN);
			wvTwitterAuth.setWebViewClient(new MobMonkeyWebViewClient());
			wvTwitterAuth.loadUrl(requestToken.getAuthenticationURL());
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	private void parseUri(Uri uri) {
		try {
			if(uri.getQueryParameter(MMAPIConstants.TWITTER_OAUTH_VERIFIER) != null) {
				twitterAccessToken = twitter.getOAuthAccessToken(requestToken, uri.getQueryParameter(MMAPIConstants.TWITTER_OAUTH_VERIFIER));
				MMSignInAdapter.signInUserTwitter(new TwitterAuthCallback(), twitterAccessToken.getToken(), twitterAccessToken.getScreenName(), MMConstants.PARTNER_ID);
				
				int requestCode = getIntent().getIntExtra(MMAPIConstants.REQUEST_CODE, MMAPIConstants.DEFAULT_INT);
				
				if(requestCode == MMAPIConstants.REQUEST_CODE_SIGN_IN_TWITTER_AUTH) {
					progressDialog = ProgressDialog.show(TwitterAuthScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_in_twitter), true, false);
				} else if(requestCode == MMAPIConstants.REQUEST_CODE_SIGN_UP_TWITTER_AUTH) {
					progressDialog = ProgressDialog.show(TwitterAuthScreen.this, MMAPIConstants.DEFAULT_STRING, getString(R.string.pd_signing_up_twitter), true, false);
				}
			} else {
				finish();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	private class MobMonkeyWebViewClient extends WebViewClient {
		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(url.contains(MMAPIConstants.TWITTER_CALLBACK_URL_SIGN_IN)) {
				parseUri(Uri.parse(url));
				return true;
			}
			return false;
		}
	}
	
	private class TwitterAuthCallback implements MMCallback {
		public void processCallback(Object obj) {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
			
			try {
				JSONObject response = new JSONObject((String) obj);
				if(response.getString(MMAPIConstants.KEY_RESPONSE_ID).equals(MMAPIConstants.RESPONSE_ID_SUCCESS)) {
					setResult(MMAPIConstants.RESULT_CODE_SUCCESS);
					userPrefsEditor.putString(MMAPIConstants.KEY_USER, twitterAccessToken.getScreenName());
					userPrefsEditor.putString(MMAPIConstants.KEY_AUTH, twitterAccessToken.getToken());
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
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, TAG + "response: " + (String) obj);
		}
	}
}
