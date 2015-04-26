package com.sugarcrm.android.model;

import com.google.gson.JsonElement;


import static com.sugarcrm.android.utils.RIDs.*;

public class HomeRequestModel extends AutoTaskRequestModel
{
	private DataModel_MODULES mModules;

	public DataModel_MODULES getModules() {
		return mModules;
	}
	
	@Override
	protected Object[] getFieldsInfo() {
		return new Object[] {"mModules", MODULE_DATA};
	}

	@Override
	boolean isContentDownloaded() {
		return mModules!=null;
	}
}
