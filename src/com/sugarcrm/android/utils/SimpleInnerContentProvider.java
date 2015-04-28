package com.sugarcrm.android.utils;

import static com.sugarcrm.android.utils.RIDs.*;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sugarcrm.android.R;
import com.sugarcrm.android.database.DataBaseController;
import com.sugarcrm.android.http.ParameterBundle;
import com.sugarcrm.android.http.SugarHttpClient;

public class SimpleInnerContentProvider 
{
	private static final String TAG = "SimpleInnerContentProvider";
	
	public Object getContent(int requestCode, ParameterBundle params) {
		Log.i(TAG, "providing content, request code: "+requestCode);
		Object content = null;
		content = getFromDataBase(params);
		try {
			if(content == null) { //если данных нет в базе
				content = getFromServer(requestCode, params);
				insertRequestResult(params, content); //пишем результат запроса в базу
				content = convertResultFromJson(requestCode, (JsonElement)content); //не должен возвращать null
			}
		if(content == null) {
			Log.e(TAG, "error code: "+R.string.error_unknown);
			return R.string.error_unknown; 
		}
		
		} catch (JsonIOException|JsonSyntaxException|IllegalStateException e) {
			Log.e(TAG, "error code: "+R.string.error_parseex);
			return R.string.error_parseex;
		} catch (IOException e) {
			Log.e(TAG, "error code: "+R.string.error_connex);
			return R.string.error_connex;
		} 
		
		return content;
	}
	
	private Object convertResultFromJson(int dataType, JsonElement content) {
		JsonDataModelConverter converter = new JsonDataModelConverter();
		Object result = converter
				.convertToDataModel(dataType, (JsonElement) content);
		
		return result;
	}
	
	private void insertRequestResult(ParameterBundle params, Object content) {
		if(content!=null) {
			DataBaseController.getInstance().insert(params, content);
		}
	}
	
	private Object getFromDataBase(ParameterBundle params) {
		return DataBaseController.getInstance().query(params);
	}
	
	private Object getFromServer(int requestCode, ParameterBundle params)
			throws ClientProtocolException, IOException {
		
		Object content = null;
		final SugarHttpClient client = SugarHttpClient.getInstance();

		switch(requestCode) {
			case MODULE_DATA:
				content = client.getAvailableModules(params);
				break;
			case UPCOMMING_ACTIVITES: 
				content = client.getUpcomingActivities(params);
				break;
			case SEARCH_ENTRY_LIST:
				content = client.searchByModule(params); 
				break;
			case ENTRY_LIST: case HOME_ENTRY_LIST:
				content = client.getEntryList(params);
				break;
			case CARD_FORM_ENTRY:
				content = client.getEnry(params);
				break;
			case MODULES_FIELDS:
				content = client.getModuleFields(params);
				break;
			case MODULES_LAYOUT:
				content = client.getModuleLayout(params);
				break;
			case LANGUAGE_DEFINITION:
				content = client.getLanguageDefinition(params);
				break;
			case LAYOUT_FIELDS:
				break;
		}

		return content;
	}
}