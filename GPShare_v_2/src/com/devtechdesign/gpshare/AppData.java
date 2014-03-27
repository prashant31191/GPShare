package com.devtechdesign.gpshare;

public class AppData {

	private static AppData			instance	= new AppData();
	private static ISyncResponse	jsResponser;

	private AppData() {
	};

	public static AppData getInstance() {
		return instance;
	}

	public ISyncResponse getJsResponder() {
		return jsResponser;
	}

	public static void setJsResponder(ISyncResponse jsResponser) {
		AppData.jsResponser = jsResponser;
	}
}
