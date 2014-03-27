package com.devtechdesign.gpshare.Images;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.data.db.Transactions;

import dev.tech.images.Util;

public class ImageUrls {

	@SuppressWarnings("null")
	public List<Bitmap> getImageUrls(String[] imgUrls) {

		List<Bitmap> bitmaps = new ArrayList<Bitmap>();

		Bitmap bmp = null;
		File root = Environment.getExternalStorageDirectory();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;

		// String[] fileNames = dir.list(new FilenameFilter()
		// {
		// public boolean accept (File dir, String name) {
		// if (new File(dir,name).isDirectory())
		// return false;
		// return name.toLowerCase().endsWith(".jpg");
		// }
		// });
		// String[] imgUrls = dir.list();

		Globals.setimgUrlList(imgUrls);

		int i = 0;
		int picPerPlaceCount = imgUrls.length;
		// getPicCount(imgUrls,placeName);

		if (picPerPlaceCount != 0) {

			String[] tempImageNames = new String[picPerPlaceCount];

			for (String bitmapFileName : imgUrls) {
				// bmp = BitmapFactory.decodeFile(dir.getPath() + "/" +
				// bitmapFileName);
				// dir.getPath() + "/"
				tempImageNames[i] = bitmapFileName;
				File imgFile = new File(bitmapFileName);
				System.out.println("Before decoding!");
				bmp = Util.decodeFileImg(imgFile, 70);
				bitmaps.add(bmp);
				i++;

			}

			Globals.setImgNames(tempImageNames);

		}
		return bitmaps;
	}

	private static int getPicCount(String[] imgUrls, String placeName) {

		int i = 0;
		// count pics for the given place
		for (String bitmapFileName : imgUrls) {
			try {
				String[] splitPlaceName = bitmapFileName.split(",\\s*");
				System.out.println("XXXXXXXXXbitmapFileName: " + bitmapFileName + "!!!!!!!!!!!!!!!");

				System.out.println("XXXXXXXXXsplitPlaceName: " + splitPlaceName[4] + " placename: " + placeName + "!!!!!!!!!!!!!!!");

				if ((splitPlaceName[4].replace(".jpg", "")).equals(placeName)) {

					System.out.println("XXXXXXXXXsplitPlaceName: " + splitPlaceName[4].replace(".jpg", "") + " placename: " + placeName + "!!!!!!!!!!!!!!!");

					i++;
				}

			} catch (Exception e) {

			}
		}
		System.out.println("XXXXXXXXXaftercounting!!!!!!!!!!!!!!!");
		return i;
	}
}
