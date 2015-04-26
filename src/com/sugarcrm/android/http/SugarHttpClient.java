package com.sugarcrm.android.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sugarcrm.android.R;
import com.sugarcrm.android.app.SugarApp;

import android.util.Log;

public class SugarHttpClient 
{
	public static String TAG = "SugarHttpClient";
	
	private int DEFAULT_CONNECTION_TIMEOUT = 50000;
	private int DEFAULT_SOCKET_TIMEOUT = 50000;
	
	private SugarHttpClient() { Log.i(TAG, "initilized"); }
	private static class SingletonHolder {
		private static final SugarHttpClient INSTANCE = new SugarHttpClient();
	}
	 
	public static SugarHttpClient getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private String mUrl;
	private String mUrlStaticPart;
	private String mSessionId;
	private String mUserId;
	
	public String getUrl() {
		return mUrl;
	}
	
	public String getUrlStaticPart() {
		return mUrlStaticPart;
	}
	
	public String getSessionId() {
		return mSessionId;
	}
	
	public String getUserId() {
		return mUserId;
	}
	
    public String login(String user_name, String password, String url)
    		throws NoSuchAlgorithmException, IOException {
    	mUrlStaticPart = SugarApp.getContext().getResources().getString(R.string.sugar_static_url_part);
    	mUrl = url+mUrlStaticPart;
    	
    	//хеш пароля
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    String passwordHash = new BigInteger(1, md5.digest(password.getBytes())).toString(16);
	    
	    //формирование запроса
	    Map<String, String> userCredentials = new LinkedHashMap<String, String>();  
	    userCredentials.put("user_name", user_name);
	    userCredentials.put("password", passwordHash);

	    Map<String, Object> rest = new LinkedHashMap<String, Object>();
	    rest.put("user_auth", userCredentials);
	    rest.put("application_name", "RestClient");
	    
	    HttpEntity entity = sendRequest("login", SugarApp.getGson().toJson(rest), mUrl);
		JsonObject result = new JsonParser().parse(new InputStreamReader(entity.getContent()))
				.getAsJsonObject();
		
		if(result.has("id")) {
			mSessionId =  result.get("id").getAsString();
			mUserId = getUserId(mSessionId);
			return mSessionId;
		} else {
			return "err_"+result.get("description").getAsString();
    	}
    }
    
    public void logout() throws ClientProtocolException, IOException {
	    Map<String, Object> rest = new LinkedHashMap<String, Object>();
	    rest.put("session", "session_id");

	    sendRequest("logout", SugarApp.getGson().toJson(rest), mUrl);
    }
    
    public String getUserId(String session_id) 
    		throws ClientProtocolException, IOException {
	    Map<String, Object> rest = new LinkedHashMap<String, Object>();
	    rest.put("session", session_id);
	    
	    HttpEntity entity = sendRequest("get_user_id", SugarApp.getGson().toJson(rest), mUrl);
	    
	    String user_id = new JsonParser().parse(new InputStreamReader(entity.getContent()))
	    		.getAsJsonPrimitive()
	    		.getAsString();

    	return user_id;
    }
    
    public JsonElement getAvailableModules(ParameterBundle params)
    		throws ClientProtocolException, IOException {
    	Map<String, String> rest = new LinkedHashMap<String, String>();  
    	rest.put("session", params.session_id);
        rest.put("filter", params.filter); //"default"
   	    
   		HttpEntity entity = sendRequest("get_available_modules", SugarApp.getGson().toJson(rest), mUrl);
   		
   		JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));
   		
   		return result;
    }
    
    public JsonElement getLanguageDefinition(ParameterBundle params)
    		throws ClientProtocolException, IOException {
    	Map<String, Object> rest = new LinkedHashMap<String, Object>();  
    	rest.put("session_id", params.session_id);
    	rest.put("modules", params.modules);
    	rest.put("md5", params.md5);
    	
    	HttpEntity entity = sendRequest("get_language_definition", SugarApp.getGson().toJson(rest), mUrl);
    	
    	JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));

    	return result;
    }
    
    public JsonElement getEntryList(ParameterBundle params) throws ClientProtocolException, IOException 
	{
    	Map<String, Object> rest = new LinkedHashMap<String, Object>();
    	rest.put("session_id", params.session_id);
    	rest.put("module_name", params.module_name);
    	rest.put("query", params.query);
    	rest.put("order_by", params.order_by);
    	rest.put("offset", params.offset);
    	rest.put("select_fields", params.select_fields);
    	rest.put("link_name_to_fields_array", params.link_name_to_fields_array);
    	rest.put("max_results", params.max_results);
    	rest.put("deleted", params.deleted);
    	rest.put("favorites", params.favorites);

    	HttpEntity entity = sendRequest("get_entry_list", SugarApp.getGson().toJson(rest), mUrl);
    	
    	JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));
    	
    	return result;
    }
    
	public JsonElement getEnry(ParameterBundle params) throws ClientProtocolException, IOException
	{
	   	Map<String, Object> rest = new LinkedHashMap<String, Object>();
	   	rest.put("session_id", params.session_id);
	   	rest.put("module_name", params.module_name);
	   	rest.put("id", params.id);
	   	rest.put("select_fields", params.select_fields);
	   	rest.put("link_name_to_fields_array", params.link_name_to_fields_array);
	   	rest.put("track_view", params.track_view);
	
	   	HttpEntity entity = sendRequest("get_entry", SugarApp.getGson().toJson(rest), mUrl);
	   	
	   	JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));
		
	   	return result;
	}
	
    public JsonElement getModuleLayout(ParameterBundle params)
    		throws ClientProtocolException, IOException 
    {
		Map<String, Object> rest = new LinkedHashMap<String, Object>();  
		rest.put("session_id", params.session_id);
		rest.put("modules", params.modules);
		rest.put("types", params.types);
		rest.put("views", params.views);
		rest.put("acl_check", params.acl_check);
		rest.put("md5", params.md5);
		
		HttpEntity entity = sendRequest("get_module_layout", SugarApp.getGson().toJson(rest), mUrl);
		
		JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));

		return result;
	}
    
    public JsonElement getModuleFields(ParameterBundle params) 
    		throws ClientProtocolException, IOException
    {
    	Map<String, Object> rest = new LinkedHashMap<String, Object>();  
    	rest.put("session_id", params.session_id);
    	rest.put("module_name", params.module_name);
    	rest.put("select_fields", params.select_fields);

    	HttpEntity entity = sendRequest("get_module_fields", SugarApp.getGson().toJson(rest), mUrl);
    	
    	JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));
    	
    	return result;
    }
    
    public JsonElement getUpcomingActivities(ParameterBundle params) throws ClientProtocolException, IOException
    {
    	Map<String, Object> rest = new LinkedHashMap<String, Object>();  
    	rest.put("session_id", params.session_id);
    	
    	HttpEntity entity = sendRequest("get_upcoming_activities", SugarApp.getGson().toJson(rest), mUrl);

    	JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));
    	
    	return result;
    }
    
    public JsonElement searchByModule(ParameterBundle params) throws ClientProtocolException, IOException 
	{
		Map<String, Object> rest = new LinkedHashMap<String, Object>();  
		rest.put("session_id", params.session_id);
		rest.put("search_string", params.search_string);
		rest.put("modules", params.modules);
		rest.put("offset", params.offset);
		rest.put("max_results", params.max_results);
		rest.put("assigned_user_id", params.assigned_user_id);
		rest.put("select_fields", params.select_fields);
		rest.put("unified_search_only", params.unified_search_only);
		rest.put("favorites", params.favorites);
		
		HttpEntity entity = sendRequest("search_by_module", SugarApp.getGson().toJson(rest), mUrl);

		JsonElement result = new JsonParser().parse(new InputStreamReader(entity.getContent()));
		
		return result;
	}
    
    private HttpEntity sendRequest(String method, String rest_data, String url) 
    			throws ClientProtocolException, IOException {
    	Log.i(TAG, "sending request: "+method);
    	
    	MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
	    multipartEntity.addPart("method", new StringBody(method, ContentType.TEXT_PLAIN));
	    multipartEntity.addPart("input_type", new StringBody("JSON", ContentType.TEXT_PLAIN));
	    multipartEntity.addPart("response_type", new StringBody("JSON", ContentType.TEXT_PLAIN));
	    multipartEntity.addPart("rest_data", new StringBody(rest_data, ContentType.APPLICATION_JSON));
	    HttpEntity multipart = multipartEntity.build();
	    
	    HttpPost httpPost = new HttpPost(url);
	    httpPost.setEntity(multipart);
	    
	    HttpParams httpParameters = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParameters, DEFAULT_CONNECTION_TIMEOUT);
	    HttpConnectionParams.setSoTimeout(httpParameters, DEFAULT_SOCKET_TIMEOUT);
	    
	    DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParameters);
	    HttpResponse response = defaultHttpClient.execute(httpPost);
	    
	    return response.getEntity();
    }
}
