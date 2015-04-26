package com.sugarcrm.android.model;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.util.SparseArray;

import com.sugarcrm.android.R;
import com.sugarcrm.android.http.ParameterBundle;
import com.sugarcrm.android.utils.SimpleInnerContentProvider;

import static com.sugarcrm.android.utils.RequestCodeParser.*;

public abstract class AutoTaskRequestModel extends RequestModel 
{
	private FailRequestHolder mFailHolder; 
	
	@Override
	protected void provideCurrentModelState() {
		super.provideCurrentModelState();
		if(mFailHolder!=null) {
			mObservables.notifyFailed(mFailHolder.requestCode, mFailHolder.resultCode);
		}
	}
	
	@Override
	protected void onPostExecute(int requestCode, ParameterBundle params, int resultCode) { 
		if(resultCode != RESULT_CODE_OK) {
			mFailHolder = new FailRequestHolder(requestCode, resultCode);
			mTaskObservables.stopAllTasks();
		}
	}
	
	private class FailRequestHolder 
	{
		int requestCode;
		int resultCode;
		
		FailRequestHolder(int request, int result) {
			requestCode = request;
			resultCode = result;
		}
	}
	
	@Override
	protected void startRequest(int requestCode, ParameterBundle params) {
		ArrayList<Integer> requestCodeEnum = getCodeEnum(requestCode);

		final SimpleInnerContentProvider provider = new SimpleInnerContentProvider();
		final SparseArray<Field> fields = getFieldsForRequest();
		
		for(Integer rcode : requestCodeEnum) {
			if(fields.indexOfKey(rcode)>=0) { //если есть поле для записи результата запроса
				RequestTask task = new RequestTask(rcode, params, new SplittExecuter() {
					@Override
					public Integer doInBackground(final int requestCode, ParameterBundle params) {
						Field rightField = fields.get(requestCode);
						try {
							Object content = provider.getContent(requestCode, params);
							if(!isError(content)) {
								rightField.set(AutoTaskRequestModel.this, content);
							} else return (Integer) content;
						} catch (IllegalArgumentException|IllegalAccessException e) {
							return R.string.error_unknown;
						}
						
						return RESULT_CODE_OK;
					}
				}); task.execute();	
			}
		}
	}
	
	private boolean isError(Object content) {
		return content instanceof Integer;
	}
	
	protected abstract Object[] getFieldsInfo();
	
	private SparseArray<Field> getFieldsForRequest() {
		SparseArray<Field> fields = new SparseArray<Field>();
		
		Object[] fieldsInfo = getFieldsInfo();
		
		for(int i=0; i<fieldsInfo.length; i+=2) {
			String fieldName = (String) fieldsInfo[i];
			Integer fieldType = (Integer) fieldsInfo[i+1];
			try {
				Field field = getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				fields.put(fieldType, field);
			} catch (NoSuchFieldException e) { }
		}
		
		return fields;
	}
}
