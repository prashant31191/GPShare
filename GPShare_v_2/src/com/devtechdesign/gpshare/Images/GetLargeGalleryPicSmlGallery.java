package com.devtechdesign.gpshare.Images;

import java.io.File;
import java.util.Hashtable;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.widget.BaseAdapter;

import com.devtechdesign.gpshare.elements.Image;

import dev.tech.images.Util;

/*
 * Fetch friends profile pictures request via AsyncTask
 */
public class GetLargeGalleryPicSmlGallery {

	Hashtable<String, Image> smalleGalleryImages;
	Hashtable<String, String> positionRequested;
	LargeGalleryPic listener;
	int runningCount = 0;
	Stack<ItemPair> queue;

	/*
	 * 50 max async tasks at any given time.
	 */
	final static int MAX_ALLOWED_TASKS = 50;

	public GetLargeGalleryPicSmlGallery() {
		smalleGalleryImages = new Hashtable<String, Image>();
		positionRequested = new Hashtable<String, String>();
		queue = new Stack<ItemPair>();
	}

	/*
	 * Inform the listener when the image has been downloaded. listener is
	 * FriendsList here.
	 */
	public void setListener(LargeGalleryPic listener) {
		this.listener = listener;
		reset();
	}

	public void reset() {
		positionRequested.clear();
		runningCount = 0;
		queue.clear();
	}

	//
	// /*
	// * If the profile picture has already been downloaded and cached, return
	// it
	// * else execute a new async task to fetch it - if total async tasks >15,
	// * queue the request.
	// */
	// public Bitmap getImage(String uid, String url) {
	//
	// if (!url.substring(0, 4).equals("http")) {
	// url = "http://" + url;
	// }
	// Image imgOb = smalleGalleryImages.get(uid);
	// Bitmap image = imgOb.getBitmap();
	//
	// if (image != null) {
	//
	// return image;
	// }
	// if (!positionRequested.containsKey(uid)) {
	//
	// positionRequested.put(uid, "");
	// if (runningCount >= MAX_ALLOWED_TASKS) {
	// queue.push(new ItemPair(uid, url));
	// } else {
	// runningCount++;
	// new GetSmallGalPicAsyncTask().execute(uid, url);
	// }
	// }
	// return null;
	// }

	@SuppressLint("NewApi")
	public Bitmap getLocalImage(Image img) {

		Image imgOb = smalleGalleryImages.get(img.getimgCreateDate());
		try {

			Bitmap image = imgOb.getBitmap();
			return image;

		} catch (NullPointerException e) {

		}
		if (!positionRequested.containsKey(img.getimgCreateDate())) {

			positionRequested.put(img.getimgCreateDate(), "");

			if (runningCount >= MAX_ALLOWED_TASKS) {
				queue.push(new ItemPair(img.getimgCreateDate(), img));
			} else {
				runningCount++;
				new GetSmallGalPicAsyncTask().execute(img.getimgCreateDate(), img);
			}

		}
		return null;
	}

	public void getNextImage() {
		if (!queue.isEmpty()) {
			ItemPair item = queue.pop();
			new GetSmallGalPicAsyncTask().execute(item.uid, item.img);
		}
	}

	private class GetSmallGalPicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
		String uid;
		Image img;

		@Override
		protected Bitmap doInBackground(Object... params) {
			this.uid = (String) params[0];
			img = (Image) params[1];

			return Util.decodeFileImg(new File(img.getimgPath()), 50);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			runningCount--;
			if (result != null) {
				img.setBitmap(result);
				smalleGalleryImages.put(uid, img);
				listener.notifyDataSetChanged(img);
				getNextImage();
			}
		}
	}

	class ItemPair {
		String uid;
		Image img;

		public ItemPair(String uid, Image img) {
			this.uid = uid;
			this.img = img;
		}
	}

}
