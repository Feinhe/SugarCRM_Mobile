package com.sugarcrm.android.utils;

import com.sugarcrm.android.R;
import com.sugarcrm.android.fragment.ErrorLoadingFragment;
import com.sugarcrm.android.fragment.LoadingFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public class FragmentUtils 
{
	public static void addLoadingInfoFragment(ActionBarActivity activity, int parentViewId) {
		FragmentManager fmanager = activity.getSupportFragmentManager();
		if(fmanager.findFragmentByTag(LoadingFragment.TAG) == null) {
			fmanager.beginTransaction()
				.add(parentViewId, new LoadingFragment(), LoadingFragment.TAG)
				.commit();
		}
	}
	
	public static void addErrorInfoFragment(ActionBarActivity activity, int parentViewId) {
		FragmentManager fmanager = activity.getSupportFragmentManager();
		if(fmanager.findFragmentByTag(ErrorLoadingFragment.TAG) == null) {
			fmanager.beginTransaction()
				.add(parentViewId, new ErrorLoadingFragment(), ErrorLoadingFragment.TAG)
				.commit();
		}
	}
	
	public static void removeFragmentByTag(ActionBarActivity activity, String tag) {
		FragmentManager fmanager = activity.getSupportFragmentManager();
		Fragment fragment = fmanager.findFragmentByTag(tag);
		if(fragment!=null) {
			fmanager.beginTransaction()
				.remove(fragment)
				.commit();
		}
	}
}
