package com.mobmonkey.mobmonkey;

import java.util.Stack;

import com.mobmonkey.mobmonkey.fragments.TopViewedFragment;
import com.mobmonkey.mobmonkey.fragments.TrendingNowFragment;
import com.mobmonkey.mobmonkey.fragments.TrendingNowFragment.OnTrendingItemClickListener;
import com.mobmonkey.mobmonkey.utils.MMFragment;
import com.mobmonkey.mobmonkeyapi.utils.MMAPIConstants;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Dezapp, LLC
 *
 */
public class TrendingNowActivity extends FragmentActivity implements OnTrendingItemClickListener{
	FragmentManager fragmentManager;
	Stack<MMFragment> fragmentStack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragmentcontainer);
		
		fragmentManager = getSupportFragmentManager();
		fragmentStack = new Stack<MMFragment>();
		
		if(findViewById(R.id.llfragmentcontainer) != null) {
			if(savedInstanceState != null) {
				return;
			}
			
			TrendingNowFragment trendingNowFragment = new TrendingNowFragment();
			fragmentManager.beginTransaction().add(R.id.llfragmentcontainer, fragmentStack.push(trendingNowFragment)).commit();
		}
	}
	
	@Override
	public void onTrendingItemClick(int position, String trends) {
		MMFragment mmFragment = null;
		Bundle data = new Bundle();
		
		switch(position) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				mmFragment = new TopViewedFragment();
				data.putString(MMAPIConstants.KEY_INTENT_EXTRA_TRENDING_TOP_VIEWED, trends);
				break;
			case 3:
				break;
		}
		
		mmFragment.setArguments(data);
		performTransaction(mmFragment);
	}
	
	/**
	 * Handler when back button is pressed, it will not close and destroy the current {@link Activity} but instead it will remain on the current {@link Activity}
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {		
		if(fragmentStack.size() > 1) {
			MMFragment mmFragment = fragmentStack.pop();
			
			mmFragment.onFragmentBackPressed();
			
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.peek());
			fragmentTransaction.commit();
		}
		
		moveTaskToBack(true);
		return;
	}
	
	private void performTransaction(MMFragment mmFragment) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.llfragmentcontainer, fragmentStack.push(mmFragment));
		fragmentTransaction.commit();		
	}
}
