package com.mobmonkey.mobmonkeyandroid;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.audiofx.BassBoost.Settings;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.mobmonkey.mobmonkeyandroid.fragments.TopViewedFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.TrendingNowFragment;
import com.mobmonkey.mobmonkeyandroid.listeners.MMOnTrendingFragmentItemClickListener;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;

/**
 * @author Dezapp, LLC
 * 
 */
public class TrendingNowActivity extends SherlockFragmentActivity implements
		MMOnTrendingFragmentItemClickListener {
	FragmentManager fragmentManager;
	Stack<MMFragment> fragmentStack;

	// DrawLayout fields
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mFragmentTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_container);

		fragmentManager = getSupportFragmentManager();
		fragmentStack = new Stack<MMFragment>();

		if (findViewById(R.id.llfragmentcontainer) != null) {
			if (savedInstanceState != null) {
				return;
			}
			// Fire off nav drawer sequence
			initNavigationDrawerLayout(savedInstanceState);

			TrendingNowFragment trendingNowFragment = new TrendingNowFragment();
			fragmentManager
					.beginTransaction()
					.add(R.id.llfragmentcontainer,
							fragmentStack.push(trendingNowFragment)).commit();
		}
	}

	private void initNavigationDrawerLayout(Bundle savedInstanceState) {
		mTitle = mDrawerTitle = getResources().getString(R.string.tv_title_trendingnow);
		mFragmentTitles = getResources().getStringArray(
				R.array.drawer_fragments_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_drawer);
		mDrawerList = (ListView) findViewById(R.id.activity_drawer_list);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mFragmentTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		 getSupportActionBar().setHomeButtonEnabled(true);
		 setTitle(getResources().getString(R.string.tv_title_trendingnow));

		// DrawerToggle setup
		mDrawerToggle = initActionBarDrawerToggle();
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	private ActionBarDrawerToggle initActionBarDrawerToggle() {
		return new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View v) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View v) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}

		};

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(title);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

/*	// ActionBar Setup
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.menu_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}*/

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		case R.id.menu_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Click listener to pick up user selection from navigation drawer
	 * 
	 * @author KV_87
	 * 
	 */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			selectFragmentFromDrawer(position);
		}
	}

	private void selectFragmentFromDrawer(int position) {
    	
		MMFragment mmFragment = null;
		
		Intent i = null;
		Class<?> activityToStart = null;
		Context context = TrendingNowActivity.this;
		
		switch(position) {
			case 0:
				//mmFragment = new Fragment_1();
				//i = new Intent(MainScreen.this, TrendingNowActivity.class);
				activityToStart = TrendingNowActivity.class;
				break;
			case 1:
				activityToStart = InboxActivity.class;
				break;
			case 2:
				activityToStart = SearchLocationsActivity.class;
				break;
			case 3:
				activityToStart = FavoritesActivity.class;
				break;
			default:
				activityToStart = SettingsActivity.class;
				break;
		}
		
		startActivity(new Intent(TrendingNowActivity.this, activityToStart));
		//performTransaction(mmFragment);    	
		
    }

	/*******************************************************************/

	@Override
	public void onTrendingFragmentItemClick(int position) {
		MMFragment mmFragment = null;

		switch (position) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			mmFragment = new TopViewedFragment();
			break;
		case 3:
			break;
		}

		performTransaction(mmFragment);
	}

	/**
	 * Handler when back button is pressed, it will not close and destroy the
	 * current {@link Activity} but instead it will remain on the current
	 * {@link Activity}
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (fragmentStack.size() > 1) {
			MMFragment mmFragment = fragmentStack.pop();

			mmFragment.onFragmentBackPressed();

			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_left_in,
					R.anim.slide_right_out);
			fragmentTransaction.replace(R.id.llfragmentcontainer,
					fragmentStack.peek());
			fragmentTransaction.commit();
		}

		moveTaskToBack(true);
		return;
	}

	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_right_in,
				R.anim.slide_left_out);
		fragmentTransaction.replace(R.id.llfragmentcontainer,
				fragmentStack.push(mmFragment));
		fragmentTransaction.commit();
	}
}
