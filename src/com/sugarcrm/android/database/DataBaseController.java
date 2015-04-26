package com.sugarcrm.android.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sugarcrm.android.R;
import com.sugarcrm.android.app.SugarApp;
import com.sugarcrm.android.http.ParameterBundle;
import com.sugarcrm.android.model.RequestModel;
import com.sugarcrm.android.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseController
{
	private static final String TAG = "DataBaseController";
	
	public static final int INSET_RESULT_CODE_OK = 0;
	
	private DBOpenHelper mDBHhelper;
	private SQLiteDatabase mDataBase;
	
    private static class SingletonHolder {
    	private static final DataBaseController INSTANCE = new DataBaseController();
    }
    
     public static DataBaseController getInstance() {
        return SingletonHolder.INSTANCE;
    }
	
	private DataBaseController() {
		Log.i(TAG, "created");
		String dbName = Utils.getString(R.string.db_name);
		final DBOpenHelper dbHelper = new DBOpenHelper(SugarApp.getContext(), dbName, null, 1);
		mDataBase = dbHelper.getWritableDatabase();
	}
	
	public void deleteTempDataTable() {
		String table = Utils.getString(R.string.db_requests_table);
		mDataBase.delete(table, null, null);
	}
	
	public synchronized long insert(ParameterBundle params, Object data) {
		String table = Utils.getString(R.string.db_requests_table);
		String[] columns = Utils.getStringArray(R.array.db_requests_table_columns);
		
		ContentValues values = new ContentValues();
		values.put(columns[1], params.serializeJSON().toString());
		values.put(columns[2], data.toString());
		
		long insertResult = mDataBase.insert(table, null, values);
		Log.i(TAG, "insert result: "+insertResult);
		
		return insertResult;
	}
	
	public Object query(ParameterBundle params) {
		Log.i(TAG, "query");

		String table = Utils.getString(R.string.db_requests_table);
		String[] columns = Utils.getStringArray(R.array.db_requests_table_columns);
		
		JsonElement data = null;
		
		String serializedParams = params.serializeJSON().toString();
		String where = columns[1]+" = '"+serializedParams+"';";
		Cursor requestResult = null;
		try {
			requestResult = mDataBase.query(table, columns, where, null, null, null, null);
			if(requestResult.moveToFirst()) {
				Log.i(TAG, "query success!");
				data = new JsonParser().parse(requestResult.getString(2));
				requestResult.close();
			} return data;
		} catch(SQLiteException e) {
			Log.e(TAG, "SQLiteException: "+e.getMessage());
			return null;
		}
	}

	private class DBOpenHelper extends SQLiteOpenHelper
	{
		public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "DBHelper onCreate()");
			createTable(db, R.string.db_requests_table, R.array.db_requests_table_columns);
		}
		
		private void createTable(SQLiteDatabase db, int nameResource, int columsResource) {
			String tableName = Utils.getString(nameResource);
			String[] tableColumnNames = Utils.getStringArray(columsResource);

			db.execSQL(DBUtils.getTableCreateString(tableName, tableColumnNames));
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	}
}
