package com.sugarcrm.android.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class DataModel_MODULES implements Parcelable
{
	private Bundle mModules;
	
	public DataModel_MODULES(Bundle modules) {
		mModules = modules;
	}
	
	public String getModuleLabel(String key) {
		return mModules.getString(key);
	}
	
	public String getModuleKey(String label) {
		for(String key : mModules.keySet()) {
			if(label.equals(mModules.get(key)))
				return key;
		} return null;
	}
	
	public String[] createLabelArray() {
		String[] mArr = new String[mModules.keySet().size()];
		int i = 0;
		for(String key : mModules.keySet()) {
			mArr[i] = mModules.getString(key);
			i++;
		} return mArr;
	}
	
	/* PARCELABLE */

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBundle(mModules);
	}
	
	public static final Parcelable.Creator<DataModel_MODULES> CREATOR = 
			new Parcelable.Creator<DataModel_MODULES>() 
	{
        public DataModel_MODULES createFromParcel(Parcel in) {
            return new DataModel_MODULES(in); 
        }

        public DataModel_MODULES[] newArray(int size) {
            return new DataModel_MODULES[size];
        }
    };
    
	private DataModel_MODULES(Parcel in) {
		mModules = in.readBundle();
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
