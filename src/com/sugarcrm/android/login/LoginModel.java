package com.sugarcrm.android.login;

import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

public class LoginModel 
{
	private static final String TAG = "LogInModel";

	private final LoginObservable mObservable = new LoginObservable();
	private LoginTask mLoginTask;
	private boolean mIsWorking;

	public LoginModel() {
		Log.i(TAG, "new Instance");
	}

	public void login(final String user, final String pass, final String url) {
		if (mIsWorking) {
			return;
		}

		mObservable.notifyStarted();

		mIsWorking = true;
		mLoginTask = new LoginTask(user, pass, url);
		mLoginTask.execute();
	}

	public void stopLogin() {
		if (mIsWorking) {
			mLoginTask.cancel(true);
			mIsWorking = false;
		}
	}

	public void registerLoginObserver(final LoginObserver LoginObserver) {
		mObservable.registerObserver(LoginObserver);
		if (mIsWorking) {
			LoginObserver.onLoginStarted(this);
		}
	}

	public void unregisterLoginObserver(final LoginObserver LoginObserver) {
		mObservable.unregisterObserver(LoginObserver);
	}

	private class LoginTask extends AsyncTask<Void, Void, Boolean> {
		private String mUser;
		private String mPass;
		private String mUrl;
		
		public LoginTask(final String user, final String pass, final String url) {
			mUser = user;
			mPass = pass;
			mUrl = url;
		}

		@Override
		protected Boolean doInBackground(final Void... params) {
			return true; //TODO sugar login
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mIsWorking = false;

			if (success) {
				mObservable.notifySucceeded();
			} else {
				mObservable.notifyFailed();
			}
		}
	}

	public interface LoginObserver {
		void onLoginStarted(LoginModel LogInModel);

		void onLoginSucceeded(LoginModel LogInModel);

		void onLoginFailed(LoginModel LogInModel);
	}

	private class LoginObservable extends Observable<LoginObserver> {
		public void notifyStarted() {
			for (final LoginObserver LoginObserver : mObservers) {
				LoginObserver.onLoginStarted(LoginModel.this);
			}
		}

		public void notifySucceeded() {
			for (final LoginObserver LoginObserver : mObservers) {
				LoginObserver.onLoginSucceeded(LoginModel.this);
			}
		}

		public void notifyFailed() {
			for (final LoginObserver LoginObserver : mObservers) {
				LoginObserver.onLoginFailed(LoginModel.this);
			}
		}
	}
}
