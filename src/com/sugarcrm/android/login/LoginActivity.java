package com.sugarcrm.android.login;

import com.sugarcrm.android.R;
import com.sugarcrm.android.R.layout;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;

public class LoginActivity extends ActionBarActivity implements LoginModel.LoginObserver
{
	private static final String TAG = "LoginActivity";
	private static final String TAG_WORKER = "TAG_WORKER";
	
	private EditText mUser;
	private EditText mPass;
	private EditText mUrl;
	private View mLogin;
	private View mProgress;
	
	private LoginModel mLoginModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mUser = (EditText) findViewById(R.id.edittext_user);
		mPass = (EditText) findViewById(R.id.edittext_password);
		mUrl = (EditText) findViewById(R.id.edittext_url);
		mLogin = findViewById(R.id.button_login);
		mProgress = findViewById(R.id.login_progress_wheel);
		
		mLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				attemptLogin();
			}
		});
		
		final LoginFragmentBox retainedWorkerFragment =
				(LoginFragmentBox) getSupportFragmentManager().findFragmentByTag(TAG_WORKER);

		if (retainedWorkerFragment != null) {
			mLoginModel = retainedWorkerFragment.getLoginModel();
		} else {
			final LoginFragmentBox workerFragment = new LoginFragmentBox();
			
			getSupportFragmentManager().beginTransaction()
					.add(workerFragment, TAG_WORKER)
					.commit();

			mLoginModel = workerFragment.getLoginModel();
		}

		mLoginModel.registerLoginObserver(this);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		mLoginModel.unregisterLoginObserver(this);
		
		if (isFinishing()) {
			mLoginModel.stopLogin();
		}
	}
	
	public void attemptLogin() {
		// Reset errors.
		mUser.setError(null);
		mPass.setError(null);
		mUrl.setError(null);
		
		final String user = mUser.getText().toString();
		final String pass = mPass.getText().toString();
		final String url = mUrl.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		// Check for a valid fields
		if (TextUtils.isEmpty(url)) {
			mUrl.setError(getString(R.string.error_empty_field));
			focusView = mUrl;
			cancel = true;
		} if (TextUtils.isEmpty(pass)) {
			mPass.setError(getString(R.string.error_empty_field));
			focusView = mPass;
			cancel = true;
		} if (TextUtils.isEmpty(user)) {
			mUser.setError(getString(R.string.error_empty_field));
			focusView = mUser;
			cancel = true;
		} 
		
		if (cancel) focusView.requestFocus();
		else mLoginModel.login(user, pass, url);
	}

	@Override
	public void onLoginStarted(final LoginModel LoginModel) {
		Log.i(TAG, "onLoginStarted");
		showProgress(true);
	}

	@Override
	public void onLoginSucceeded(final LoginModel LoginModel) {
		Log.i(TAG, "onLoginSucceeded");
		finish();
		//TODO launch next activity
	}

	@Override
	public void onLoginFailed(final LoginModel LoginModel) {
		Log.i(TAG, "onLoginFailed");
		showProgress(false);
		Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
	}

	private void showProgress(final boolean show) {
		mUser.setEnabled(!show);
		mPass.setEnabled(!show);
		mUrl.setEnabled(!show);
		mLogin.setEnabled(!show);
		mProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}
}
