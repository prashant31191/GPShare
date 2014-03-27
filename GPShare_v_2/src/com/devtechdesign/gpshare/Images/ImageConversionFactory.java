package com.devtechdesign.gpshare.Images;

import java.io.File;
import java.util.Hashtable;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.utility.Utility;

import dev.tech.images.Util;

/*
 * Fetch friends profile pictures request via AsyncTask
 */
public class ImageConversionFactory {

	Hashtable<String, Bitmap>	friendsImages;
	Hashtable<String, String>	positionRequested;
	BaseAdapter					listener;
	int							runningCount		= 0;
	Stack<ItemPair>				queue;

	/*
	 * 50 max async tasks at any given time.
	 */
	final static int			MAX_ALLOWED_TASKS	= 20;

	public ImageConversionFactory() {
		friendsImages = new Hashtable<String, Bitmap>();
		positionRequested = new Hashtable<String, String>();
		queue = new Stack<ItemPair>();
	}

	/*
	 * Inform the listener when the image has been downloaded. listener is
	 * FriendsList here.
	 */
	public void setListener(BaseAdapter listener) {
		this.listener = listener;
		reset();
	}

	public void reset() {
		positionRequested.clear();
		runningCount = 0;
		queue.clear();
	}

	/*
	 * If the profile picture has already been downloaded and cached, return it
	 * else execute a new async task to fetch it - if total async tasks >15,
	 * queue the request.
	 */
	public Bitmap getImage(String uid, String url) {

		url = Transactions.correctUrl(url);

		Bitmap image = friendsImages.get(uid);

		if (image != null) {

			return image;
		}
		if (!positionRequested.containsKey(uid)) {

			positionRequested.put(uid, "");
			if (runningCount >= MAX_ALLOWED_TASKS) {
				queue.push(new ItemPair(uid, url));
			} else {
				runningCount++;
				new GetProfilePicAsyncTask().execute(uid, url);
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public Bitmap getLocalImage(String uid, String Uri) {

		Bitmap image = friendsImages.get(uid); 
		if (new File(Uri).exists()) {
			if (image != null) { 
				return image;
			}
			if (!positionRequested.containsKey(uid)) {

				positionRequested.put(uid, "");

				if (runningCount >= MAX_ALLOWED_TASKS) {
					queue.push(new ItemPair(uid, Uri));
				} else {
					runningCount++;
					new GetLocalPicAsyncTask().execute(uid, Uri);
				} 
			}
		}
		return null;
	}

	public void getNextProfilePicImage() {
		if (!queue.isEmpty()) {
			ItemPair item = queue.pop();
			new GetProfilePicAsyncTask().execute(item.uid, item.url);
		}
	}
	
	public void getNextLocalImage() {
		if (!queue.isEmpty()) {
			ItemPair item = queue.pop();
			new GetLocalPicAsyncTask().execute(item.uid, item.url);
		}
	}

	/*
	 * Start a AsyncTask to fetch the request
	 */
	private class GetProfilePicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
		String	uid;

		@Override
		protected Bitmap doInBackground(Object... params) {
			this.uid = (String) params[0];
			String url = (String) params[1];
			Bitmap resizedbitmap = null;

			Bitmap tempbm = Utility.getOnlineBitmap(url);

			if (tempbm != null) {
				resizedbitmap = Bitmap.createScaledBitmap(tempbm, 80, 80, true);
			}

			return resizedbitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			try {
				runningCount--;
				if (result != null) {
					friendsImages.put(uid, result);
					listener.notifyDataSetChanged();
					getNextProfilePicImage();
				}
			} catch (NullPointerException e) {

			}
		}
	}

	private class GetLocalPicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
		String	uid;

		@Override
		protected Bitmap doInBackground(Object... params) {
			this.uid = (String) params[0];
			String uri = (String) params[1];

			return Util.decodeFileImg(new File(uri),200);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			try {
				runningCount--;
				if (result != null) {
					friendsImages.put(uid, result);
					listener.notifyDataSetChanged();
					getNextLocalImage();
				}

			} catch (NullPointerException e) {
				System.out.println("uid: " + uid + " listener: " + listener + " result: " + result);
			}
		}
	}

	class ItemPair {
		String	uid;
		String	url;

		public ItemPair(String uid, String url) {
			this.uid = uid;
			this.url = url;
		}
	}

}
