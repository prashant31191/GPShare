package com.devtechdesign.gpshare.Images;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.profile.ProfileLayout;
import com.devtechdesign.gpshare.utility.Base64;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.NotificationHelper;
import com.devtechdesign.gpshare.utility.SoapInterface;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

public class UploadPicture extends AsyncTask<Integer, Integer, Void> implements SoapInterface {
	private final String sent = "X";
	private int uloadStatus = 1;
	private NotificationHelper mNotificationHelper;
	private int currentIndex;
	private ArrayList<String> files;
	private Handler handler_;
	Context context;
	private char fileTypePrefix;
	private String fileType, currentUser;
	private ContentResolver contentResolver;
	private GPsharePrefs Prefs;

	public UploadPicture(Context myContext, ArrayList<String> myFiles, int index, Handler handler) {

		handler_ = handler;
		context = myContext;
		files = myFiles;
		currentIndex = index;
		contentResolver = myContext.getContentResolver();
		fileTypePrefix = files.get(currentIndex).charAt(0);
		Prefs = new GPsharePrefs(myContext, SharedPref);
		if (fileTypePrefix == 'R') {
			fileType = "Route";
		} else if (fileTypePrefix == '$') {
			fileType = "Image";
		}
		mNotificationHelper = new NotificationHelper(myContext, currentIndex, files.get(currentIndex), fileType);
	}

	protected void onPreExecute() {
		mNotificationHelper.createNotification();
	}

	@Override
	protected Void doInBackground(Integer... integers) {
		int progress = 0;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		int bufferSize;
		byte[] buffer;
		String uploadFlder = "/Geocam/Upload";
		String pollingFolder;
		String imgFolder = "/Geocam/Images";
		String routeFolder = "/Geocam/Routes";
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		File uploadFolder;
		new File(extStorageDirectory + imgFolder);
		if (files.get(currentIndex).charAt(0) != 'X') {

			String args = null;
			if ((files.get(currentIndex).charAt(0) == 'R')) {
				pollingFolder = routeFolder;
				uploadFolder = new File(extStorageDirectory + pollingFolder);
				args = extStorageDirectory + pollingFolder + "/" + files.get(currentIndex);
			} else {
				pollingFolder = uploadFlder;
				uploadFolder = new File(extStorageDirectory + pollingFolder);
				args = files.get(currentIndex);
			}

			try {
				File sourceFile = new File(args);
				FileInputStream fileInputStream = new FileInputStream(sourceFile);

				@SuppressWarnings("deprecation")
				String fileName = URLEncoder.encode("P" + Globals.getpersonId() + "_.jpg");

				URL url = new URL(GPSHARE_DOMAIN + "/map/Uploader.ashx?file=" + fileName);

				// URL url = new
				// URL("http://10.0.2.2:2815/WebHandler.ashx?file=" + fileName);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");

				dos = new DataOutputStream(conn.getOutputStream());
				bufferSize = (int) sourceFile.length();
				buffer = new byte[bufferSize];

				fileInputStream.read(buffer, 0, bufferSize);
				String ba1 = Base64.encodeToString(buffer, Base64.DEFAULT);
				byte[] utfBytes = ba1.getBytes("UTF-8");
				fileInputStream.close();
				int totalBytes = 0;
				int length = utfBytes.length / 10;
				for (byte b : utfBytes) {
					totalBytes += 1;
					dos.write(b);
					if (totalBytes % (length) == 0 && totalBytes <= utfBytes.length) {
						dos.flush();
						progress++;
						Thread.sleep(100);
						publishProgress(progress);
					}
				}
				Thread.sleep(100);
				dos.flush();
				Thread.sleep(100);
				int responseStatus = conn.getResponseCode();
				dos.close();

				if (responseStatus == 200) {
					renameImage(uploadFolder, files.get(currentIndex), sent);
					uloadStatus = 0;
					makeToast("File uploaded!.");
				}

			} catch (IOException v) {
				makeToast("Error in input/output!. Some files could not be sent.");
				v.printStackTrace();
			} catch (Exception e) {
				makeToast("Error in http connection. Some files could not be sent.");
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				makeToast("Profile pic cannot be uploaded at this time.");
			}
		}
		return null;
	}

	private Bitmap convertToBitmap(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		return bitmap;
	}

	private void updateImageSrcMainAct() {

		try {
			String profileImageUrl = GPSHARE_DOMAIN + "/map/images/profile/" + "P" + Prefs.LoadPreferences("personId") + "_.jpg";
			Prefs.savePreferences("imgProfileUrl", profileImageUrl);
			ProfileLayout profileLayout = GPContextHolder.getProfileLayout();
			if (profileLayout != null)
				profileLayout.loadProfileImage(profileImageUrl);
		} catch (NullPointerException e) {

		}
	}

	protected void onProgressUpdate(Integer... progress) {
		mNotificationHelper.progressUpdate(progress[0]);
	}

	public void makeToast(final String message) {
		handler_.post(new Runnable() {

			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	protected void onPostExecute(Void result) {
		mNotificationHelper.completed(uloadStatus);
		currentIndex++;
		updateImageSrcMainAct();
		if (currentIndex < files.size()) {
			new UploadPicture(context, files, currentIndex, handler_).execute(0);
		}
	}

	private void renameImage(File sourceFolder, String fileName, String prefix) {
		File file = new File(sourceFolder, fileName);
		File renamed = new File(sourceFolder, prefix + fileName + fileType);
		file.renameTo(renamed);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}
