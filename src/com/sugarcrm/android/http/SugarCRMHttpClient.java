package com.sugarcrm.android.http;

import java.io.IOException;

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

import android.util.Log;

public class SugarCRMHttpClient 
{
	public static String TAG = "SugarCRMHttpClient";
	
	private int DEFAULT_CONNECTION_TIMEOUT = 5000;
	private int DEFAULT_SOCKET_TIMEOUT = 5000;
	
	private SugarCRMHttpClient() { Log.i(TAG, "initilized"); }
	private static class SingletonHolder {
		private static final SugarCRMHttpClient INSTANCE = new SugarCRMHttpClient();
	}
	 
	public static SugarCRMHttpClient getInstance() {
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
	
    private HttpEntity sendRequest(String method, String rest_data, String url) 
    			throws ClientProtocolException, IOException {
    	Log.i(TAG, "sending request");
    	
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
