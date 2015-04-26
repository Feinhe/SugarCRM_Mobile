package com.sugarcrm.android.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class DataModel_ENTRY implements Parcelable
{
	private String mId;
	private String mModuleName;
	private Bundle mNameValueList;
	
	public DataModel_ENTRY(String id, String moduleName, Bundle nameValueList) {
		mId = id;
		mModuleName = moduleName;
		mNameValueList = nameValueList;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getModuleName() {
		return mModuleName;
	}
	
	public Bundle getNameValueList() {
		return mNameValueList;
	}
	
	/* PARCELABLE */
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		dest.writeString(mModuleName);
		dest.writeBundle(mNameValueList);
	}
	
	public static final Parcelable.Creator<DataModel_ENTRY> CREATOR = 
			new Parcelable.Creator<DataModel_ENTRY>() 
	{
        public DataModel_ENTRY createFromParcel(Parcel in) {
            return new DataModel_ENTRY(in); 
        }

        public DataModel_ENTRY[] newArray(int size) {
            return new DataModel_ENTRY[size];
        }
    };
    
	private DataModel_ENTRY(Parcel in) {
		mId = in.readString();
		mModuleName = in.readString();
		mNameValueList = in.readBundle();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
}
