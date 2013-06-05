/**
 * 
 */
package com.mobmonkey.mobmonkeyandroid.utils;

import java.util.Stack;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.fragments.FavoritesFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.InboxFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SearchLocationsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.SettingsFragment;
import com.mobmonkey.mobmonkeyandroid.fragments.TrendingNowFragment;

/**
 * @author KV_87
 *
 */
public abstract class MMBaseActivity extends SherlockFragmentActivity {
	

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mFragmentTitles;
	private FragmentManager fragmentManager;

	private Stack<MMFragment> fragmentStack;
	protected static final String TAG = "Navigation Drawer Base: ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + "onCreate");

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			this.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		fragmentStack = new Stack<MMFragment>();
		fragmentManager = getSupportFragmentManager();

		// Fire off nav drawer sequence
		initNavigationDrawerLayout(savedInstanceState);

		// Automatically go to Trending Screen
		if (savedInstanceState == null) {
			selectFragmentFromDrawer(0);
		}
	}

	protected void initNavigationDrawerLayout(Bundle savedInstanceState) {
		mTitle = mDrawerTitle = getTitle();
		mFragmentTitles = getResources().getStringArray(
				R.array.drawer_fragments_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mFragmentTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.navigation_bar));

		// DrawerToggle setup
		mDrawerToggle = initActionBarDrawerToggle();
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			selectFragmentFromDrawer(0);
		}

	}

	protected ActionBarDrawerToggle initActionBarDrawerToggle() {

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

	protected void selectFragmentFromDrawer(int position) {

		MMFragment mmFragment = null;

		Intent i = null;
		Class<?> activityToStart = null;
		//Context context = MainScreen.this;
		boolean isActivity = false;

		switch (position) {
		case 0:
			// mmFragment = new Fragment_1();
			// i = new Intent(MainScreen.this, TrendingNowActivity.class);
			// activityToStart = TrendingNowActivity.class;
			mmFragment = new TrendingNowFragment();
			// isActivity = true;
			break;
		case 1:
			mmFragment = new InboxFragment();
			break;
		case 2:
			mmFragment = new SearchLocationsFragment();
			break;
		case 3:
			mmFragment = new FavoritesFragment();
			break;
		case 4:
			mmFragment = new SettingsFragment();
			isActivity = false;
			break;
		default:
			break;
		}

		if (isActivity) {
			// startActivity(new Intent(MainScreen.this, activityToStart));
		} else {

			mDrawerList.setItemChecked(position, true);
			setTitle(mFragmentTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			performTransaction(mmFragment);

		}

	}

	protected void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
//		fragmentTransaction.setCustomAnimations(R.anim.slide_right_in,
//				R.anim.slide_left_out);
		fragmentTransaction.replace(R.id.content_frame,
				fragmentStack.push(mmFragment));
		fragmentTransaction.commit();
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

	/*
	 * // ActionBar Setup
	 * 
	 * @Override public boolean onPrepareOptionsMenu(Menu menu) { boolean
	 * drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	 * menu.findItem(R.id.menu_settings).setVisible(!drawerOpen); return
	 * super.onPrepareOptionsMenu(menu); }
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getSupportMenuInflater().inflate(R.menu.activity_main, menu); return
	 * true; }
	 */

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

}
