package com.sugarcrm.android.utils;

import com.sugarcrm.android.R;
import com.sugarcrm.android.activity.ActivityWithModelBase;
import com.sugarcrm.android.fragment.ErrorLoadingFragment;
import com.sugarcrm.android.fragment.FragmentModelBox;
import com.sugarcrm.android.fragment.LoadingFragment;
import com.sugarcrm.android.model.RequestModel;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public class FragmentUtils 
{
	public static void addLoadingInfoFragment(FragmentManager fmanager, int parentViewId) {
		if(fmanager.findFragmentByTag(LoadingFragment.TAG) == null) {
			fmanager.beginTransaction()
				.add(parentViewId, new LoadingFragment(), LoadingFragment.TAG)
				.commit();
		}
	}
	
	public static void addErrorInfoFragment(FragmentManager fmanager, int parentViewId) {
		if(fmanager.findFragmentByTag(ErrorLoadingFragment.TAG) == null) {
			fmanager.beginTransaction()
				.add(parentViewId, new ErrorLoadingFragment(), ErrorLoadingFragment.TAG)
				.commit();
		}
	}
	
	public static void removeFragmentByTag(FragmentManager fmanager, String tag) {
		Fragment fragment = fmanager.findFragmentByTag(tag);
		if(fragment!=null) {
			fmanager.beginTransaction()
				.remove(fragment)
				.commit();
		}
	}
}
