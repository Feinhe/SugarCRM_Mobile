package com.sugarcrm.android.fragment;

import static com.sugarcrm.android.utils.FragmentUtils.addLoadingInfoFragment;
import static com.sugarcrm.android.utils.FragmentUtils.removeFragmentByTag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sugarcrm.android.activity.ActivityWithModelBase;
import com.sugarcrm.android.model.RequestModel;
import com.sugarcrm.android.model.RequestModel.ContentRequestObserver;

public abstract class FragmentWithModelBase extends Fragment implements ContentRequestObserver
{
	protected RequestModel mRequestModel;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		retainModel();
		View fragmentView = initilizeUI(inflater, parent, savedInstanceState);
		mRequestModel.registerRequestObserver(this);
		return fragmentView;
	}
	
	@Override
	public void onDestroyView() {
		mRequestModel.unregisterRequestObserver(this);
		mRequestModel.stopRequest();
	}
	
	private final void retainModel() {
		final FragmentModelBox retainedFragmentBox =
				(FragmentModelBox) getChildFragmentManager().findFragmentByTag(ActivityWithModelBase.TAG_BOX);

		if (retainedFragmentBox != null) {
			mRequestModel = (RequestModel) retainedFragmentBox.getModel();
		} else {
			mRequestModel = createModel();
			final FragmentModelBox fragmentBox = FragmentModelBox.newInstance(mRequestModel);
			getChildFragmentManager().beginTransaction()
					.add(fragmentBox, ActivityWithModelBase.TAG_BOX)
					.commit();
		}
	}
	
	/* ABSTRACT */
	
	abstract protected View initilizeUI(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);
	abstract protected RequestModel createModel();
	abstract protected int getRootViewId();
	abstract public void resolveDownloadedContent(RequestModel model);
	
	/*MODEL CALLBACKS*/
	
	@Override
	public void onRequestStarted(RequestModel model) {
		addLoadingInfoFragment(getChildFragmentManager(), getRootViewId());
	}

	@Override
	public void onTaskSucceeded(int requestCode, RequestModel model) { }

	@Override
	public void onRequestFailed(int errStringId, int requestCode, RequestModel model) {
		addLoadingInfoFragment(getChildFragmentManager(), getRootViewId());
	}

	@Override
	public void onAsyncTaskCompletion(int resultCode, int requestCode, RequestModel model) { }

	@Override
	public void onContentDownloaded(RequestModel model) {
		removeFragmentByTag(getChildFragmentManager(), LoadingFragment.TAG);
		resolveDownloadedContent(model);
	}
}
