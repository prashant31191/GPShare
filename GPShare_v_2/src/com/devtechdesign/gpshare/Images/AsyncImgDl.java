package com.devtechdesign.gpshare.Images;

import java.io.File;


import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;

import dev.tech.images.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class AsyncImgDl implements SoapInterface {

	Bitmap bMap;
	String imgUrl;
	ImageView iv;
	int imgSize;
	Canvas canvas;
	Point screenPoint;
	private ProgressBar progressBar;

	public AsyncImgDl(Bitmap bMap, String imgUrl, ImageView iv, int type) {

		this.bMap = bMap;
		this.imgUrl = imgUrl;
		this.iv = iv;

		switch (type) {
		case ONLINE_IMG:
			imgUrl = Transactions.correctUrl(imgUrl);
			new getOnlineImageNoScale().execute(imgUrl);
			return;
		case PROFILE_IMG:
			imgUrl = Transactions.correctUrl(imgUrl);
			new GetProfilePicAsyncTask().execute(imgUrl);
			return;
		case OFFLINE_IMG:
			new GetLocalPicAsyncTask().execute(imgUrl);
			return;
		}

	}

	public AsyncImgDl(Bitmap bMap2, String getimgPath, int i, Canvas canvas, Point screenPoint) {
		imgSize = i;
		this.canvas = canvas;
		this.screenPoint = screenPoint;
		new GetLocalPicAsyncTaskWithSize().execute(getimgPath);
	}

	public AsyncImgDl(ProgressBar progressBar, Bitmap bMap, String imgUrl, ImageView iv, int type) {
		this.bMap = bMap;
		this.imgUrl = imgUrl;
		this.iv = iv;
		this.progressBar = progressBar;

		switch (type) {
		case ONLINE_IMG:
			imgUrl = Transactions.correctUrl(imgUrl);
			new getOnlineImageNoScale().execute(imgUrl);
			return;
		case PROFILE_IMG:
			imgUrl = Transactions.correctUrl(imgUrl);
			new GetProfilePicAsyncTask().execute(imgUrl);
			return;
		case OFFLINE_IMG:
			new GetLocalPicAsyncTask().execute(imgUrl);
			return;
		}
	}

	private class GetProfilePicAsyncTask extends AsyncTask<Object, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Object... params) {

			String url = (String) params[0];
			Bitmap resizedbitmap = null;
			Bitmap tempbm = Utility.getOnlineBitmap(url);
			if (tempbm != null) {
				resizedbitmap = Bitmap.createScaledBitmap(tempbm, 80, 80, true);
			}
			return resizedbitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (iv != null) {
				if (result != null)
					iv.setImageBitmap(result);
			}
		}
	}

	private class getOnlineImageNoScale extends AsyncTask<Object, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Object... params) {

			String url = (String) params[0];
			return Utility.getOnlineBitmap(url);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (iv != null) {
				if (result != null) {
					hideProgressBar();
					iv.setImageBitmap(result);
				}
			}
		}
	}

	public class GetLocalPicAsyncTaskWithSize extends AsyncTask<Object, Void, Bitmap> {
		String uri;

		@Override
		protected Bitmap doInBackground(Object... params) {
			if (params != null) {
				this.uri = (String) params[0];

				return Util.decodeFileImg(new File(uri), imgSize);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap markerImage) {
			hideProgressBar();
			canvas.drawBitmap(markerImage, screenPoint.x, screenPoint.y, null);
		}
	}

	private class GetLocalPicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
		String uri;
		private Integer imgSize;

		@Override
		protected Bitmap doInBackground(Object... params) {
			if (params != null) {
				this.uri = (String) params[0];
				if (params.length > 1) {
					imgSize = Integer.valueOf((String) params[1]);
				} else {
					imgSize = 200;
				}

				return Util.decodeFileImg(new File(uri), imgSize);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			hideProgressBar();
			iv.setImageBitmap(result);
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	private void hideProgressBar() {
		if (progressBar != null)
			progressBar.setVisibility(View.GONE);
	}
}
