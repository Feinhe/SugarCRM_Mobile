package com.sugarcrm.android.login;

import com.sugarcrm.android.activity.HomeActivity;
import com.sugarcrm.android.database.DataBaseController;
import com.sugarcrm.android.fragment.FragmentModelBox;
import com.sugarcrm.android.R;
import com.sugarcrm.android.R.layout;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoginActivity extends ActionBarActivity implements LoginModel.LoginObserver
{
	private static final String TAG = "LoginActivity";
	public static final String TAG_BOX = "TAG_BOX";
	
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
		
		retainModel();
		
		mLoginModel.registerLoginObserver(this);
		
		new Handler().post(new Runnable() {
			public void run() {
				DataBaseController.getInstance().deleteTempDataTable();
			}
		});
	}
	
	private void retainModel() {
		final FragmentModelBox retainedWorkerFragment =
				(FragmentModelBox) getSupportFragmentManager().findFragmentByTag(TAG_BOX);

		if (retainedWorkerFragment != null) {
			mLoginModel = (LoginModel) retainedWorkerFragment.getModel();
		} else {
			final FragmentModelBox workerFragment = FragmentModelBox.newInstance(new LoginModel());
			
			getSupportFragmentManager().beginTransaction()
					.add(workerFragment, TAG_BOX)
					.commit();

			mLoginModel = (LoginModel) workerFragment.getModel();
		}
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
		
		String user = mUser.getText().toString();
		String pass = mPass.getText().toString();
		String url = mUrl.getText().toString();
		

		
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
	public void onLoginStarted(final LoginModel model) {
		Log.i(TAG, "onLoginStarted");
		showProgress(true);
	}

	@Override
	public void onLoginSucceeded(final LoginModel model) {
		Log.i(TAG, "onLoginSucceeded");
		Toast.makeText(this, "Logined in!", Toast.LENGTH_SHORT).show();
		finish();
		Intent i = new Intent(this, HomeActivity.class);
		startActivity(i);
		//TODO launch next activity
	}

	@Override
	public void onLoginFailed(final LoginModel model) {
		Log.e(TAG, "onLoginFailed");
		showProgress(false);
		Toast.makeText(this, mLoginModel.mErrorMsg, Toast.LENGTH_SHORT).show();
		mLoginModel.mErrorMsg = null;
	}

	private void showProgress(final boolean show) {
		mUser.setEnabled(!show);
		mPass.setEnabled(!show);
		mUrl.setEnabled(!show);
		mLogin.setEnabled(!show);
		mProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}
}
