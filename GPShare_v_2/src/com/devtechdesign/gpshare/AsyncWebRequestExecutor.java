package com.devtechdesign.gpshare;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import android.os.Bundle;

public class AsyncWebRequestExecutor {

	protected GpshareWeb	gps;

	public AsyncWebRequestExecutor(GpshareWeb gps) {
		this.gps = gps;
	}

	public void request(final String graphPath, final Bundle parameters, final String httpMethod,
			final GPSRequestListener getRoutesRquestListener, final Object state) {
		new Thread() {
			@Override
			public void run() {
				try {
					String resp = gps.request(graphPath, parameters, httpMethod);  
					getRoutesRquestListener.onComplete(resp, state);
				} catch (FileNotFoundException e) {
					getRoutesRquestListener.onFileNotFoundException(e, state);
				} catch (MalformedURLException e) {
					getRoutesRquestListener.onMalformedURLException(e, state);
				} catch (IOException e) {
					getRoutesRquestListener.onIOException(e, state);
				}
			}
		}.start();
	}

	public static interface GPSRequestListener {

		/**
		 * Called when a request completes with the given response.
		 * 
		 * Executed by a background thread: do not update the UI in this method.
		 */
		public void onComplete(String response, Object state);

		/**
		 * Called when a request has a network or request error.
		 * 
		 * Executed by a background thread: do not update the UI in this method.
		 */
		public void onIOException(IOException e, Object state);

		/**
		 * Called when a request fails because the requested resource is invalid
		 * or does not exist.
		 * 
		 * Executed by a background thread: do not update the UI in this method.
		 */
		public void onFileNotFoundException(FileNotFoundException e, Object state);

		/**
		 * Called if an invalid graph path is provided (which may result in a
		 * malformed URL).
		 * 
		 * Executed by a background thread: do not update the UI in this method.
		 */
		public void onMalformedURLException(MalformedURLException e, Object state);
	}
}
