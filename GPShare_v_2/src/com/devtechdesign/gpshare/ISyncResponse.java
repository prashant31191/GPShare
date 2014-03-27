package com.devtechdesign.gpshare;

public interface ISyncResponse {

	public void onPreSync();

	public void onSyncFinished();

	public void onSyncError();

}
