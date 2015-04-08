package com.sugarcrm.android.model;

import com.sugarcrm.android.http.ParameterBundle;

import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

public abstract class RequestModel 
{
	private static final String TAG = "DataRequestModel";
	
	public static final int MODULE_DATA = 2; 			//доступные модули
	public static final int UPCOMMING_ACTIVITES = 4; 	//грядущие мероприятия
	public static final int ENTRY_LIST = 6; 			//разделенные списки(по пагинации) каждого модуля
	public static final int SEARCH_ENTRY_LIST = 8; 		//возвращаемый при поиске (находим по строке запроса - 2map)
	public static final int HOME_ENTRY_LIST = 10;		//ентрилст для домашней странички
	public static final int CARD_FORM_ENTRY = 12;		//инфа в карточке
	public static final int MODULES_FIELDS = 14; 		//поля каждого модуля
	public static final int MODULES_LAYOUT = 16; 		//лейаут каждого модуля
	public static final int LAYOUT_FIELDS = 18; 		//поля лейаута каждого модуля(для удобной выборки)
	public static final int LANGUAGE_DEFINITION = 20; 	//локализация полей
	
	public static final int RESULT_CODE_OK = 0;
	
	private final RequestObservable mObservables = new RequestObservable();
	private final TaskObservable mTaskObservables = new TaskObservable();
	
	public RequestModel() {
		Log.i(TAG, "new Instance");
	}
	
	public void registerRequestObserver(final ContentRequestObserver observer) {
		mObservables.registerObserver(observer);
		if(isContentDownloaded()) {
			mObservables.notifyContentDownloaded();
		}
	}
	
	public void unregisterRequestObserver(final ContentRequestObserver observer) {
		mObservables.unregisterObserver(observer);
	}

	public final void sendRequest(int requestCode, ParameterBundle params) {
		mObservables.notifyStarted(requestCode);
		startRequest(requestCode, params);
	}
	
	abstract void startRequest(int requestCode, ParameterBundle params);
	
	
	public final void stopTaskInRequest(int requestCode) {
		mTaskObservables.stopTask(requestCode);
	}
	
	public final void stopRequest() {
		mTaskObservables.stopAllTasks();
	}
	
	public boolean isContentDownloaded() {
		return true;
	}
	
	public interface ContentRequestObserver {
		void onRequestStarted(int requestCode, RequestModel model);
		
		void onRequestSucceeded(int requestCode, RequestModel model);
		
		void onRequestFailed(int requestCode, RequestModel model);
		
		void onAsyncTaskCompletion(int resultCode, int requestCode, RequestModel model);
		
		void onContentDownloaded(RequestModel model);
	}
	
	abstract Integer doSplittBackground(int requestCode, ParameterBundle params);
	
	protected void onPostExecute(int requestCode, int resultCode) { }
	
	private class RequestTask extends AsyncTask<Void, Void, Integer> 
	{
		private int mRequestCode;
		private ParameterBundle mParams;
		
		public int getRequestCode() {
			return mRequestCode;
		}
		
		public RequestTask(int requestCode, ParameterBundle params) {
			mRequestCode = requestCode;
			mParams = params;
		}
		
		@Override
		protected void onPreExecute() {
			mTaskObservables.registerObserver(this);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Integer resultCode = RequestModel.this.doSplittBackground(mRequestCode, mParams);
			mObservables.notifyAsyncTaskCompletion(resultCode, mRequestCode);
			return resultCode;
		}

		@Override
		protected void onPostExecute(Integer resultCode) {
			mTaskObservables.unregisterObserver(this);
			
			if (resultCode == RESULT_CODE_OK) {
				mObservables.notifySucceeded(mRequestCode);
				if(isContentDownloaded()) {
					mObservables.notifyContentDownloaded();
				}
			} else {
				mObservables.notifyFailed(mRequestCode);
			} RequestModel.this.onPostExecute(mRequestCode, resultCode);
		}
	}
	
	private class TaskObservable extends Observable<RequestTask> 
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
	}
	
	private class RequestObservable extends Observable<ContentRequestObserver> 
	{
		public void notifyStarted(int requestCode) {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onRequestStarted(requestCode, RequestModel.this);
			}
		}

		public void notifySucceeded(int requestCode) {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onRequestSucceeded(requestCode, RequestModel.this);
			}
		}

		public void notifyFailed(int requestCode) {
			for (final ContentRequestObserver observer : mObservers) {
				observer.onRequestFailed(requestCode, RequestModel.this);
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
