package com.sugarcrm.android.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class LoginFragmentBox extends Fragment {
	private final LoginModel mLogInModel;

	public LoginFragmentBox() {
		mLogInModel = new LoginModel();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public LoginModel getLoginModel() {
		return mLogInModel;
	}
}
