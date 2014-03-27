package com.devtechdesign.gpshare;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.devtechdesign.gpshare.AsyncWebRequestExecutor.GPSRequestListener;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

import android.util.Log;


/**
 * Skeleton base class for RequestListeners, providing default error handling.
 * Applications should handle these error conditions.
 */
public abstract class GPSBaseRequestListener implements GPSRequestListener {

    @Override
    public void onFileNotFoundException(FileNotFoundException e, final Object state) {
        Log.e("GPShare", e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onIOException(IOException e, final Object state) {
        Log.e("GPShare", e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onMalformedURLException(MalformedURLException e, final Object state) {
        Log.e("GPShare", e.getMessage());
        e.printStackTrace();
    }
}
