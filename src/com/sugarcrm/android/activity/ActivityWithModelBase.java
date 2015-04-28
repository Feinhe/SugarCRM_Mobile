package com.sugarcrm.android.activity;

import static com.sugarcrm.android.utils.FragmentUtils.*;

import com.sugarcrm.android.R;
import com.sugarcrm.android.fragment.FragmentModelBox;
import com.sugarcrm.android.fragment.LoadingFragment;
import com.sugarcrm.android.http.ParameterBundle;
import com.sugarcrm.android.login.LoginActivity;
import com.sugarcrm.android.model.HomeRequestModel;
import com.sugarcrm.android.model.RequestModel;
import com.sugarcrm.android.model.RequestModel.ContentRequestObserver;
import com.sugarcrm.android.utils.RIDs;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

public abstract class ActivityWithModelBase extends ActionBarActivity implements ContentRequestObserver
{
	public static final String TAG_BOX = "TAG_BOX";
	
	protected RequestModel mRequestModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		retainModel();
		initilizeUI(savedInstanceState);
		mRequestModel.registerRequestObserver(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRequestModel.unregisterRequestObserver(this);
		if (isFinishing()) {
			mRequestModel.stopRequest();
		}
		
		mRequestModel = null;
	}
	
	private final void retainModel() {
		final FragmentModelBox retainedFragmentBox =
				(FragmentModelBox) getSupportFragmentManager().findFragmentByTag(TAG_BOX);

		if (retainedFragmentBox != null) {
			mRequestModel = (RequestModel) retainedFragmentBox.getModel();
		} else {
			mRequestModel = createModel();
			final FragmentModelBox fragmentBox = FragmentModelBox.newInstance(mRequestModel);
			getSupportFragmentManager().beginTransaction()
					.add(fragmentBox, TAG_BOX)
					.commit();
		}
	}
	
	protected final void sendRequest(int requestCode, ParameterBundle params) {
		mRequestModel.sendRequest(requestCode, params);
	}
	
	/* ABSTRACT */
	
	abstract protected void initilizeUI(Bundle savedInstanceState);
	abstract protected RequestModel createModel();
	abstract protected int getRootViewId();
	abstract public void resolveDownloadedContent(RequestModel model);
	
	/*MODEL CALLBACKS*/
	
	@Override
	public void onRequestStarted(RequestModel model) {
		addLoadingInfoFragment(getSupportFragmentManager(), getRootViewId());
	}

	@Override
	public void onTaskSucceeded(int requestCode, RequestModel model) { }

	@Override
	public void onRequestFailed(int errStringId, int requestCode, RequestModel model) {
		removeFragmentByTag(getSupportFragmentManager(), LoadingFragment.TAG);
		addErrorInfoFragment(getSupportFragmentManager(), getRootViewId());
	}

	@Override
	public void onAsyncTaskCompletion(int resultCode, int requestCode, RequestModel model) { }

	@Override
	public void onContentDownloaded(RequestModel model) {
		removeFragmentByTag(getSupportFragmentManager(), LoadingFragment.TAG);
		resolveDownloadedContent(model);
	}
}
