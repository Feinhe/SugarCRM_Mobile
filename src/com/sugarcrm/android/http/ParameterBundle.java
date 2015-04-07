package com.sugarcrm.android.http;

import java.util.ArrayList;
import java.util.Map;

public class ParameterBundle 
{
	public String session_id;
	public String assigned_user_id;
	public String module_name;
	public String id;
	public String query;
	public String order_by;
	public String search_string;
	
	public Integer offset;
	public Integer max_results;
	public Integer deleted;
	
	public String[] select_fields;
	public String[] types;
	public String[] views;
	public String[] modules;
	public ArrayList<Map<String, Object>> link_name_to_fields_array;

	public Boolean favorites;
	public Boolean track_view;
	public Boolean acl_check;
	public Boolean unified_search_only;
	public Boolean md5;
}
