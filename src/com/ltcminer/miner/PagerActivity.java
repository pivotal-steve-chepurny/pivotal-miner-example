package com.ltcminer.miner;

import static com.ltcminer.miner.Constants.DEFAULT_BACKGROUND;
import static com.ltcminer.miner.Constants.PREF_BACKGROUND;
import static com.ltcminer.miner.Constants.PREF_TITLE;

import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class PagerActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager_screen);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(getApplicationContext(), MinerService.class);
    	startService(intent);
    	bindService(intent, MiningController.getServiceConnection(), Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {
		super.onStop();
		// TODO Auto-generated method stub
				
				SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
		    	if(settings.getBoolean(PREF_BACKGROUND,DEFAULT_BACKGROUND )==false)
		    	{
		    		if (MiningController.getMinerService().running==true) { MiningController.stopMining(); }
		    		Intent intent = new Intent(getApplicationContext(), MinerService.class);
		    		stopService(intent);
		    	}
				
				Log.i("LC", "Main: in onStop()");
		    	try {
		    		unbindService(MiningController.getServiceConnection()); 
		    	} catch (RuntimeException e) {
		    		Log.i("LC", "RuntimeException:"+e.getMessage());
		    		//unbindService generates a runtime exception sometimes
		    		//the service is getting unbound before unBindService is called
		    		//when the window is dismissed by the user, this is the fix
		    	}
		}
		


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new NewsFragment();
				break;
			case 1:
				fragment = new StatusFragment();
				break;
			case 2:
				fragment = new SettingsFragment();
				break;
			}
//			Bundle args = new Bundle();
//			args.putInt(SettingsSectionFragment.ARG_SECTION_NUMBER, position + 1);
//			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getResources().getString(R.string.title_news).toUpperCase(l);
			case 1:
				return getResources().getString(R.string.title_status).toUpperCase(l);
			case 2:
				return getResources().getString(R.string.title_settings).toUpperCase(l);
			}
			return null;
		}
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// no op
		
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// no op
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		MiningController.isBackgrounded = true;
	}
	
	@Override
	protected void onResume() {
		super.onPause();
		MiningController.isBackgrounded = false;
		mViewPager.setCurrentItem(1);
	}
	
	
	

}
