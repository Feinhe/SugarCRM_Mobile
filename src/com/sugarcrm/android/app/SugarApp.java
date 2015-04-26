package com.sugarcrm.android.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Application;
import android.content.Context;

public class SugarApp extends Application
{
    private static SugarApp instance;
    private static Gson mGson = new GsonBuilder().create();

    public SugarApp() {
    	super();
        instance = this;
    }
    
    public static Gson getGson() {
    	return mGson;
    }

    public static Context getContext() {
        return instance;
    }
}