package com.sugarcrm.android.utils;

import static com.sugarcrm.android.utils.RIDs.*;
import static com.sugarcrm.android.utils.Utils.*;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sugarcrm.android.R;
import com.sugarcrm.android.model.DataModel_ENTRY;
import com.sugarcrm.android.model.DataModel_MODULES;

public class JsonDataModelConverter 
{
	public Object convertToDataModel(int modelFormat, JsonElement jsonData) {
		switch(modelFormat) {
			case MODULE_DATA:
				return convertModules(jsonData);
			case UPCOMMING_ACTIVITES: 
				return convertUpcommingActivities(jsonData);
			case SEARCH_ENTRY_LIST:
				return convertSearchEntryList(jsonData);
			case ENTRY_LIST:
				return convertEntryList(jsonData);
			case HOME_ENTRY_LIST:
				return convertHomeEntryList(jsonData);
			case CARD_FORM_ENTRY:
				return convertCardFormEntry(jsonData);
			case MODULES_FIELDS:
				return convertModules(jsonData);
			case MODULES_LAYOUT:
				return convertModules(jsonData);
			case LANGUAGE_DEFINITION:
				return convertModules(jsonData);
			case LAYOUT_FIELDS:
				return convertModules(jsonData);
		}
		
		return jsonData;
	}
	
	/* jmvn = json member variable name */
	
	private Object convertModules(JsonElement jsonData) {
		String[] excludeModules = getStringArray(R.array.exclude_modules);
		String jmvn_modules = getString(R.string.jmvn_modules);
		String jmvn_module_key = getString(R.string.jmvn_module_key);
		String jmvn_module_label = getString(R.string.jmvn_module_label);
		
		Bundle modules = new Bundle();
		
		JsonArray jarr = jsonData.getAsJsonObject().getAsJsonArray(jmvn_modules);
		for(JsonElement obj : jarr) {
			String key = obj.getAsJsonObject().get(jmvn_module_key).getAsString();
			if(Utils.getObjectIndexInArr(excludeModules, obj) == -1) {
				String label = obj.getAsJsonObject().get(jmvn_module_label).getAsString();
				modules.putString(key, label);
			}
		} DataModel_MODULES result = new DataModel_MODULES(modules);
		
		return result;
	}
	
	private Object convertCardFormEntry(JsonElement jsonData) {
		String jmvn_entry_list = getString(R.string.jmvn_entry_list);
		
		JsonObject jobj = jsonData.getAsJsonObject();
		JsonObject entry = jobj.getAsJsonArray(jmvn_entry_list)
				.get(0).getAsJsonObject();
		
		Object result = parseEntry(entry);
	
		return result;
	}
	
			private DataModel_ENTRY parseEntry(JsonObject jsonEntry) {
				String jmvn_id = getString(R.string.jmvn_id); 
				String jmvn_module_name = getString(R.string.jmvn_module_name); 
				String jmvn_name_value_list = getString(R.string.jmvn_name_value_list); 
				
				String id = jsonEntry.get(jmvn_id).getAsString();
				String module_name = jsonEntry.get(jmvn_module_name).getAsString();
				JsonObject jsonNameValueList = jsonEntry.getAsJsonObject(jmvn_name_value_list);
				
				Bundle name_value_list = fillInNameValueList(new Bundle(), jsonNameValueList);
				DataModel_ENTRY result = new DataModel_ENTRY(id, module_name, name_value_list);
				
				return result;
			}
			
			private Bundle fillInNameValueList(Bundle bundle, JsonObject name_value_list) {
				String jmvn_value = getString(R.string.jmvn_value);  
				
				Set<Entry<String, JsonElement>> keySet = name_value_list.entrySet();
				for(Entry<String, JsonElement> entry : keySet) {
					JsonElement jvalue = entry.getValue();
					
					String name = entry.getKey();
					String value = jvalue.getAsJsonObject().get(jmvn_value).getAsString();
					
					bundle.putString(name, value);
				}
				
				return bundle;
			}
	private Object convertEntryList(JsonElement jsonData) {
		
		return jsonData;
	}
	
	private Object convertUpcommingActivities(JsonElement jsonData) {
		return jsonData;
	}
	private Object convertSearchEntryList(JsonElement jsonData) {
		return jsonData;
	}
	
	private Object convertHomeEntryList(JsonElement jsonData) {
		return jsonData;
	}
}
