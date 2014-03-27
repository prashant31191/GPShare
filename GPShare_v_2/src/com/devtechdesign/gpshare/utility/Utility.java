package com.devtechdesign.gpshare.utility;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable; 
import org.json.JSONObject; 
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore; 

import com.devtechdesign.gpshare.AsyncWebRequestExecutor;
import com.devtechdesign.gpshare.Images.GetLargeGalleryPicSmlGallery;
import com.devtechdesign.gpshare.Images.ImageConversionFactory;
import com.devtechdesign.gpshare.map.ImageMapGallery;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class Utility extends Application {

	public static AsyncFacebookRunner mAsyncRunner;
	public static JSONObject mFriendsList;
	public static String userUID = null;
	public static String objectID = null;
	public static Facebook mFacebook;
	public static ImageConversionFactory model;
	public static GetLargeGalleryPicSmlGallery galleryModel;
	public static ImageMapGallery imgMapGallery;

	// public static AndroidHttpClient httpclient = null;

	public static AsyncWebRequestExecutor gpsAyncRunner;
	// public static AndroidHttpClient httpclient = null;
	public static Hashtable<String, String> currentPermissions = new Hashtable<String, String>();

	private static int MAX_IMAGE_DIMENSION = 720;
	public static final String HACK_ICON_URL = "http://www.facebookmobileweb.com/hackbook/img/facebook_icon_large.png";

	public static Bitmap getOnlineBitmap(String url) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
			bis.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (
			// httpclient != null) {
			// httpclient.close();
			// }
		}
		return bm;
	}

	public static Bitmap getBitmapAndScale(String url, int requiredSize) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, o);
			// The new size we want to scale to

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			bm = BitmapFactory.decodeStream(is, null, o2);
			return bm;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return bm;

		}
	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	public static byte[] scaleImage(Context context, Uri photoUri, String stringPhotoUri) throws IOException {

		Bitmap newbitMap = GetSingleImg.GetSingleImg("", 600, stringPhotoUri);

		/*
		 * if the orientation is not 0 (or -1, which means we don't know), we
		 * have to do a rotation.
		 */
		// if (orientation > 0) {
		// Matrix matrix = new Matrix();
		// matrix.postRotate(orientation);
		//
		// srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
		// srcBitmap.getWidth(),
		// srcBitmap.getHeight(), matrix, true);
		// }

		// String type = context.getContentResolver().getType(photoUri);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// if (type.equals("image/png")) {
		// srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		// } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
		newbitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		// }
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return bMapArray;
	}

	public static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}
}
