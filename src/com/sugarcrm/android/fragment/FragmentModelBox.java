package com.sugarcrm.android.fragment;

import com.sugarcrm.android.model.RequestModel;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FragmentModelBox extends Fragment {
	private Object mModel;
	
	public static FragmentModelBox newInstance(Object model) {
		FragmentModelBox fragment = new FragmentModelBox();
		fragment.mModel = model;
		
		return fragment;
	}
	
	public FragmentModelBox() { }

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public Object getModel() {
		return mModel;
	}
}
