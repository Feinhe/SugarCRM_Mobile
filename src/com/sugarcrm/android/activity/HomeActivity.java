package com.sugarcrm.android.activity;

import com.sugarcrm.android.R;
import com.sugarcrm.android.fragment.FragmentModelBox;
import com.sugarcrm.android.fragment.LoadingFragment;
import com.sugarcrm.android.fragment.ModuleListFragment;
import com.sugarcrm.android.fragment.ModuleListFragment.ModuleListItemSelectObserver;
import com.sugarcrm.android.http.ParameterBundle;
import com.sugarcrm.android.http.SugarHttpClient;
import com.sugarcrm.android.login.LoginActivity;

import com.sugarcrm.android.model.DataModel_MODULES;
import com.sugarcrm.android.model.HomeRequestModel;
import com.sugarcrm.android.model.RequestModel;
import com.sugarcrm.android.model.RequestModel.ContentRequestObserver;
import static com.sugarcrm.android.utils.FragmentUtils.*;
import com.sugarcrm.android.utils.RIDs;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HomeActivity extends ActivityWithModelBase implements ModuleListItemSelectObserver
{
	private static final String TAG = "HomeActivity";
	
	private DrawerLayout mDrawerLayout;
	
	private ModuleListFragment mModuleListFragment;
	private CharSequence mTitle;

	@Override
	protected RequestModel createModel() {
		return new HomeRequestModel();
	}
	
	@Override
	protected int getRootViewId() {
		return R.id.activity_home_root_frame;
	}
	
	protected void initilizeUI(Bundle savedInstanceState) {
		setContentView(R.layout.activity_home);

		mModuleListFragment = (ModuleListFragment) getSupportFragmentManager().findFragmentById(R.id.module_slide_list);
		mTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
		if(savedInstanceState == null) {
			sendRequest(RIDs.MODULE_DATA, getParams());
		}
	}
	
	private ParameterBundle getParams() {
		ParameterBundle params = new ParameterBundle();
		params.session_id = SugarHttpClient.getInstance().getSessionId();
		params.filter = "default";
		return params;
	}
	
	@Override
	public void resolveDownloadedContent(RequestModel model) {
		Log.i(TAG, "resolve content");
		mModuleListFragment.setUp(R.id.module_slide_list, 
				(DrawerLayout) findViewById(R.id.drawer_layout),
				((HomeRequestModel)model).getModules());
		
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	@Override
	public void onModuleItemSelected(int position) {
		//TODO
	}

	public void onSectionAttached(int number) {
		
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mModuleListFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.home, menu);
			restoreActionBar();
			return true;
		} return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} return super.onOptionsItemSelected(item);
	}
}
