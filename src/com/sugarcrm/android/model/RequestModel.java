package com.sugarcrm.android.model;

import com.sugarcrm.android.http.ParameterBundle;

import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

public abstract class RequestModel 
{
	protected static final String TAG = "RequestModel";
	
	public static final int RESULT_CODE_OK = 0;
	
	private boolean mIsWorking; 
	
	protected final RequestObservable mObservables = new RequestObservable();
	protected final TaskObservable mTaskObservables = new TaskObservable();
	
	public RequestModel() {
		Log.i(TAG, "new Instance");
	}
	
	public void registerRequestObserver(final ContentRequestObserver observer) {
		mObservables.registerObserver(observer);
		provideCurrentModelState();
	}
	
	public void unregisterRequestObserver(final ContentRequestObserver observer) {
		mObservables.unregisterObserver(observer);
	}
	
	protected void provideCurrentModelState() {
		if(mIsWorking) {
			mObservables.notifyStarted();
		} 
		
		if(isContentDownloaded()) {
			mObservables.notifyContentDownloaded();
		}
	}

	public final void sendRequest(int requestCode, ParameterBundle params) {
		if(mIsWorking) {
			return;
		}
		
		mIsWorking = true;
		mObservables.notifyStarted();
		Log.i(TAG, "started");
		startRequest(requestCode, params);
	}
	
	abstract protected void startRequest(int requestCode, ParameterBundle params);
	
	
	public final void stopTaskInRequest(int requestCode) {
		mTaskObservables.stopTask(requestCode);
		if(mTaskObservables.isEmpty()) {
			mIsWorking = false;
		}
	}
	
	public final void stopRequest() {
		Log.i(TAG, "stopped");
		mIsWorking = false;
		mTaskObservables.stopAllTasks();
	}
	
	abstract boolean isContentDownloaded();
	
	public interface ContentRequestObserver {
		void onRequestStarted(RequestModel model);
		
		void onTaskSucceeded(int requestCode, RequestModel model);
		
		void onRequestFailed(int errStringId, int requestCode, RequestModel model);
		
		void onAsyncTaskCompletion(int resultCode, int requestCode, RequestModel model);
		
		void onContentDownloaded(RequestModel model);
	}
	
	interface SplittExecuter {
		Integer doInBackground(int requestCode, ParameterBundle params);
	}
	
	protected void onPostExecute(int requestCode, ParameterBundle params, int resultCode) { }
	
	class RequestTask extends AsyncTask<Void, Void, Integer> 
	{
		private int mRequestCode;
		private ParameterBundle mParams;
		private SplittExecuter mSplittExec;
		
		public int getRequestCode() {
			return mRequestCode;
		}
		
		public RequestTask(int requestCode, ParameterBundle params, SplittExecuter executer) {
			mRequestCode = requestCode;
			mParams = params;
			mSplittExec = executer;
		}
		
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "task started, request code: "+mRequestCode);
			mTaskObservables.registerObserver(this);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Integer resultCode = mSplittExec.doInBackground(mRequestCode, mParams);
			mObservables.notifyAsyncTaskCompletion(resultCode, mRequestCode);
			return resultCode;
		}

		@Override
		protected void onPostExecute(Integer resultCode) {
			mTaskObservables.unregisterObserver(this);
			Log.d(TAG, "task finished, request code: "+mRequestCode+", result code: "+resultCode);
			if(mTaskObservables.isEmpty()) {
				mIsWorking = false;
			}
			
			if (resultCode == RESULT_CODE_OK) {
				mObservables.notifySucceeded(mRequestCode);
				if(isContentDownloaded()) {
					mObservables.notifyContentDownloaded();
				} 
			} else {
				mObservables.notifyFailed(mRequestCode, resultCode);
			} RequestModel.this.onPostExecute(mRequestCode, mParams, resultCode);
		}
	}
	
	protected class TaskObservable extends Observable<RequestTask> 
	{
		public void stopTask(int requestCode) {
			for (final RequestTask task : mObservers) {
				if(task.getRequestCode() == requestCode) {
					task.cancel(true);
					unregisterObserver(task);
				}
			}
		}
		
		public void stopAllTasks() {
			for (final RequestTask task : mObservers) {
				task.cancel(true);
			} unregisterAll();
		}
		
		public boolean isEmpty() {
			return mObservers.isEmpty();
		}
	}
	
	protected class RequestObservable extends Observable<ContentRequestObserver> 
	{
		public void notifyStarted() {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onRequestStarted(RequestModel.this);
			}
		}

		public void notifySucceeded(int requestCode) {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onTaskSucceeded(requestCode, RequestModel.this);
			}
		}

		public void notifyFailed(int requestCode, int resultCode) {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onRequestFailed(resultCode, requestCode, RequestModel.this);
			}
		}
		
		public void notifyContentDownloaded() {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onContentDownloaded(RequestModel.this);
			}
		}
		
		public void notifyAsyncTaskCompletion(int resultCode, int requestCode) {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onAsyncTaskCompletion(resultCode, requestCode, RequestModel.this);
			}
		}
	}
}
