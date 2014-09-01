package com.cs275.Groupit;

import java.util.Map;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.*;
import com.cs275.Groupit.controllers.*;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.facebook.model.GraphUser;

public class Dashboard extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	Map<String, Object> user;
	JSONArray groups;
	FacebookHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		
		//final TextView userName = (TextView)(mNavigationDrawerFragment.getActivity().findViewById(R.id.name));
		//userName.setText("Some Text");

	}
	
    @Override
    public void onBackPressed() {
    	MyGroups myGroups = new MyGroups(this);
    	myGroups.isRoot=true;
    	ViewGroup container = (ViewGroup)findViewById(R.id.container);
    	container.removeAllViews();
    	myGroups.inflate(getLayoutInflater(), container);
        super.onBackPressed();
    }

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
		/*helper = new FacebookHelper(FacebookHelper.getSession(this));
		groups = helper.getGroups(this, new FacebookHelper.Callback(){
			@Override
			public void finished(Object g) {
				//Log.d("Debug", ((GraphUser)g).toString());
			}
		});
		user = helper.getUser(this, new FacebookHelper.Callback(){
			@SuppressWarnings("unchecked")
			@Override
			public void finished(Object u) {
				Log.d("Debugger", ((Map<String, Object>)u).toString());
				//TextView userName = (TextView)(PlaceholderFragment.root.findViewById(R.id.name));
				//userName.setText(((Map<String, Object>)u).get("first_name").toString());
			}
		});*/
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.dashboard, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public static View root;
		private static int sectionSelected=0;
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			sectionSelected=sectionNumber;
			return fragment;
		}

		public PlaceholderFragment() { 
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			Controller v = null;
			switch(sectionSelected){
			case 1:
				v = new MyGroups(getActivity());
				break;
			case 2:
				v = new FindGroup(getActivity());
				break;
			case 3:
				v= new CreateGroup(getActivity());
				break;
			case 4:
				v = new Messaging(getActivity());
				break;
			}
			if (v!=null){
				container.removeAllViews();
				root=v.inflate(inflater, container);
			}
			
			
			return root;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((Dashboard) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

}
