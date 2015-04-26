package com.sugarcrm.android.fragment;

import com.sugarcrm.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ErrorLoadingFragment extends Fragment
{
	public static final String TAG = "ErrorLoadingFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_error_loading, container, false);
	}
}
