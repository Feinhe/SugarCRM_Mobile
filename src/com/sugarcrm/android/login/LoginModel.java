package com.sugarcrm.android.login;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;

import com.sugarcrm.android.R;
import com.sugarcrm.android.app.SugarApp;
import com.sugarcrm.android.http.SugarHttpClient;
import com.sugarcrm.android.utils.Utils;

import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoginModel 
{
	private static final String TAG = "LogInModel";

	private final LoginObservable mObservable = new LoginObservable();
	private LoginTask mLoginTask;
	private boolean mIsWorking;
	public String mErrorMsg;
	
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

	public void registerLoginObserver(final LoginObserver observer) {
		mObservable.registerObserver(observer);
		if (mIsWorking) {
			observer.onLoginStarted(this);
		}
	}

	public void unregisterLoginObserver(final LoginObserver observer) {
		mObservable.unregisterObserver(observer);
	}

	private class LoginTask extends AsyncTask<Void, Void, Boolean> 
	{
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
			SugarHttpClient client = SugarHttpClient.getInstance();
			try {
				String result = client.login(mUser, mPass, mUrl);
				Log.i(TAG, "login result: "+result);
				if(result.contains("err_")) {
					mErrorMsg = result;
					return false;
				}
			} catch (IOException e) {
				Log.i(TAG, "login result: connection problem");
				mErrorMsg = Utils.getString(R.string.error_connex);
				return false;
			} catch (NoSuchAlgorithmException|IllegalStateException e) {
				Log.i(TAG, "login result: result parse exception");
				mErrorMsg = Utils.getString(R.string.error_unknown);
				return false;
			} 
			
			return true;
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
		void onLoginStarted(LoginModel model);

		void onLoginSucceeded(LoginModel model);

		void onLoginFailed(LoginModel model);
	}

	private class LoginObservable extends Observable<LoginObserver> {
		public void notifyStarted() {
			for (final LoginObserver observer : mObservers) {
				observer.onLoginStarted(LoginModel.this);
			}
		}

		public void notifySucceeded() {
			for (final LoginObserver observer : mObservers) {
				observer.onLoginSucceeded(LoginModel.this);
			}
		}

		public void notifyFailed() {
			for (final LoginObserver observer : mObservers) {
				observer.onLoginFailed(LoginModel.this);
			}
		}
	}
}
