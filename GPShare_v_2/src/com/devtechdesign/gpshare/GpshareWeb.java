package com.devtechdesign.gpshare;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.os.Bundle;

public class GpshareWeb {

	private static final String RESTSERVER_URL = "http://www.devtechdesign.com/gpshare/svc/GPShare.svc/";

	public String request(String graphPath, Bundle params, String httpMethod)
			throws FileNotFoundException, MalformedURLException, IOException {

		return Util.openUrl(RESTSERVER_URL, httpMethod, params);
	}
}
