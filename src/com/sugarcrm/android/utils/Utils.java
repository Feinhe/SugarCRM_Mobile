package com.sugarcrm.android.utils;

import com.google.gson.JsonArray;
import com.sugarcrm.android.R;
import com.sugarcrm.android.app.SugarApp;

public class Utils 
{	
	public static String getString(int resourceId) {
		return SugarApp.getContext().getResources().getString(resourceId);
	}
	
	public static String[] getStringArray(int resourceId) {
		return SugarApp.getContext().getResources().getStringArray(resourceId);
	}
	
	public static int getObjectIndexInArr(Object[]arr, Object obj) {
		for(int i=0; i<arr.length; i++) {
			if(arr[i].equals(obj)) {
				return i;
			}
		} return -1;
	}
}
