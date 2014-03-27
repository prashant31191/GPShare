package com.devtechdesign.gpshare;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FacebookError;

import android.os.Bundle;

public class Util {

	public static String openUrl(String restserverUrl, String httpMethod, Bundle params) {
 
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null; 
		String url = null;

		try {

			if (httpMethod.contains("Get")) {
				url = restserverUrl + httpMethod + encodeUrl(params);
				System.out.println("url: " + url);
				response = httpclient.execute(new HttpGet(url));
			} else {
				
				url = restserverUrl + httpMethod; 
				System.out.println("url: " + url);  
				HttpPost request = new HttpPost(url);
				 
				String json = (String) params.get("json"); 
				StringEntity e = new StringEntity(json, "UTF-8");
				request.setEntity(e);
				request.setHeader("content-type", "application/json");
				response = httpclient.execute(request);
				System.out.println("response.statusline: " + response.getStatusLine());
				 
			}

			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				System.out.println("response.statusline: " + response.getStatusLine());
				System.out.println("response.out: " + out.toString());
				return out.toString();
			} else {
				// Closes the connection.// 
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		}
		return null;
	}
 
	public static JSONObject parseJson(String response) throws JSONException, GPShareError {
		// Edge case: when sending a POST request to /[post_id]/likes
		// the return value is 'true' or 'false'. Unfortunately
		// these values cause the JSONObject constructor to throw
		// an exception.
		if (response.equals("false")) {
			throw new GPShareError("request failed");
		}
		if (response.equals("true")) {
			response = "{value : true}";
		}
		JSONObject json = new JSONObject(response);

		// errors set by the server are not consistent
		// they depend on the method and endpoint
		if (json.has("error")) {
			JSONObject error = json.getJSONObject("error");
			throw new GPShareError(error.getString("message"), error.getString("type"), 0);
		}
		if (json.has("error_code") && json.has("error_msg")) {
			throw new GPShareError(json.getString("error_msg"), "", Integer.parseInt(json.getString("error_code")));
		}
		if (json.has("error_code")) {
			throw new GPShareError("request failed", "", Integer.parseInt(json.getString("error_code")));
		}
		if (json.has("error_msg")) {
			throw new GPShareError(json.getString("error_msg"));
		}
		if (json.has("error_reason")) {
			throw new GPShareError(json.getString("error_reason"));
		}
		return json;
	}

	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first) {
				first = false;
				sb.append("/");
			} else {
				sb.append("/");
			}
			System.out.println("parameters.getString(key): " + parameters.getString(key));
			sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(parameters.getString(key)));
			System.out.println("sb.toString(): " + sb.toString());
		}
		return sb.toString();
	}
}
