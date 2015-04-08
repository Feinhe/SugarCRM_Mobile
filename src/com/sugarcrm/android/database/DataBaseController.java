package com.sugarcrm.android.database;

import com.sugarcrm.android.model.RequestModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseController extends SQLiteOpenHelper implements RequestModel.ContentRequestObserver
{
	private static final String TAG = "DataBaseController";

	public DataBaseController(Context context, String name) {
		super(context, name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		Log.i(TAG, "onCreate()");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

	@Override
	public void onAsyncTaskCompletion(int resultCode, int requestCode, RequestModel model) {
		Log.i(TAG, "async callback, resultCode="+resultCode+", requestCode="+requestCode);
		if(resultCode == RequestModel.RESULT_CODE_OK) {
			
		}
	}
	
	@Override
	public void onRequestSucceeded(int requestCode, RequestModel model) { model.unregisterRequestObserver(this); }
	@Override
	public void onRequestFailed(int requestCode, RequestModel model) { model.unregisterRequestObserver(this); }
	@Override
	public void onContentDownloaded(RequestModel model) { }
	@Override
	public void onRequestStarted(int requestCode, RequestModel model) { }
}
