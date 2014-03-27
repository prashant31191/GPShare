package com.devtechdesign.gpshare.utility;

import java.io.File;

import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.Transactions;

import dev.tech.images.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NotificationHelper {
	private Context mContext;
	private int NOTIFICATION_ID;
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	private PendingIntent mContentIntent;
	private String fileName = "";
	private final int max = 10;
	private String currentFile;
	private String fileType;
	protected Builder mBuilder;

	public NotificationHelper(Context context, int ID, String currentFile, String FileType) {
		fileName = currentFile;
		NOTIFICATION_ID = ID;
		mContext = context;
		fileType = FileType;
		this.currentFile = currentFile;

	}

	public void createNotification() {
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		final int uploadIcon = android.R.drawable.stat_sys_upload;
		CharSequence tickerText = mContext.getString(R.string.download_ticker);
		long when = System.currentTimeMillis();
		// mNotification = new Notification(icon, tickerText, when);
		// mNotification.contentView = new
		// RemoteViews(mContext.getPackageName(), R.layout.notify);
		// mNotification.contentView.setProgressBar(R.id.progressBar, max, 0,
		// false);
		// mNotification.contentView.setTextViewText(R.id.text,
		// "Uploading GPShare Profile Pic");
		// Intent notificationIntent = new Intent();
		// mContentIntent = PendingIntent.getActivity(mContext, 0,
		// notificationIntent, 0);
		// mNotification.contentIntent = mContentIntent;
		// mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		new AsyncTask<Bitmap, Bitmap, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Bitmap... arg0) {
				Bitmap bm = Util.decodeFileImg(new File(currentFile), 100);
				return bm;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(uploadIcon).setLargeIcon(result).setContentTitle("Uploading GPShare Pic")
						.setStyle(new NotificationCompat.BigTextStyle().bigText("GPShare image upload"));
				mBuilder.setContentText("Upload");
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

				super.onPostExecute(result);
			}

		}.execute(null, null, null);
		return;
	}

	public void progressUpdate(int percentageComplete) {
		// mNotificationManager.notify(NOTIFICATION_ID, mNotification);
		// mNotification.contentView.setProgressBar(R.id.progressBar, max,
		// percentageComplete, false);
	}

	public void showToast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}

	public void completed(int status) {
		// mNotification.flags = Notification.FLAG_AUTO_CANCEL;

		if (status == 0)
			mBuilder.setContentText("Image uploaded successfully!");
		else
			mBuilder.setContentText("Image failed to upload");
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
