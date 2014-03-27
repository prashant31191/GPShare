package com.devtechdesign.gpshare;

import java.util.ArrayList;

import com.devtechdesign.gpshare.Images.ImageConversionFactory;
import com.devtechdesign.gpshare.Images.ViewImage;
import com.devtechdesign.gpshare.utility.Utility;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class ProfilePicSelection extends Activity {
	/** Called when the activity is first created. */
	private Cursor imagecursor, actualimagecursor;
	private int image_column_index, actual_image_column_index;
	GridView imagegrid;
	private int count;
	private Button btnNotRightNow;
	private ArrayList<String> imageIds = new ArrayList<String>();
	/**
	 * Cursor used to access the results from querying for images on the SD
	 * card.
	 */
	private Cursor cursor;
	/*
	 * Column index for the Thumbnails Image IDs.
	 */
	private int columnIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_pic_selection);
		btnNotRightNow = (Button) findViewById(R.id.btnNotRightNow);
		// init_phone_image_grid();
		getImageIds();

		GridView sdcardImages = (GridView) findViewById(R.id.PhoneImageGrid);
		sdcardImages.setAdapter(new ProfilePicLvImageAdapter(this));

		sdcardImages.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {

				String imgId = imageIds.get(position);
				// String i =
				// actualimagecursor.getString(actual_image_column_index);

				Intent intent = new Intent(getApplicationContext(), ViewImage.class);
				intent.putExtra("imageId", imgId);
				startActivity(intent);

			}
		});

		btnNotRightNow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent mainControl = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainControl);
			}
		});
	}

	public String getFileName(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cur = getContentResolver().query(uri, projection, null, null, null);
		cur.moveToFirst();
		String path = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));

		return path;
	}

	@SuppressWarnings("deprecation")
	private void init_phone_image_grid() {

		// Set up an array of the Thumbnail Image ID column we want
		String[] projection = { MediaStore.Images.Thumbnails._ID };
		// Create the cursor pointing to the SDCard
		final String imageOrderBy = MediaStore.Images.Media._ID;
		cursor = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, imageOrderBy);
		// Get the column index of the Thumbnails Image ID
		columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

		GridView sdcardImages = (GridView) findViewById(R.id.PhoneImageGrid);
		sdcardImages.setAdapter(new ProfilePicLvImageAdapter(this));

	}

	private Cursor thumbnails;

	public void getImageIds() {

		// First request thumbnails what you whant
		String[] projection = new String[] { MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID };
		thumbnails = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);

		// Then walk thru result and obtain imageId from records
		for (thumbnails.moveToFirst(); !thumbnails.isAfterLast(); thumbnails.moveToNext()) {
			String imageId = thumbnails.getString(thumbnails.getColumnIndex(Thumbnails._ID));

			imageIds.add(imageId);

		}
		thumbnails.close();
	}

	private class ProfilePicLvImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ProfilePicSelection profilePicSelection;

		public ProfilePicLvImageAdapter(ProfilePicSelection profilePicSelection) {

			this.profilePicSelection = profilePicSelection;
			if (Utility.model == null) {
				Utility.model = new ImageConversionFactory();
			}

			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(profilePicSelection.getBaseContext());
		}

		public int getCount() {
			return imageIds.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			String currentImgId = imageIds.get(position);

			ImageView picturesView;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.image_gallery_grid_item, null);
				picturesView = (ImageView) convertView.findViewById(R.id.grid_item);
				picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				picturesView.setPadding(2, 2, 2, 2);
				picturesView.setLayoutParams(new GridView.LayoutParams(150, 150));
				convertView.setTag(new ImageHolder(picturesView, currentImgId));
				picturesView.setTag(new ImageHolder(picturesView, currentImgId));
			} else {
				ImageHolder viewHolder = (ImageHolder) convertView.getTag();
				picturesView = viewHolder.getpicturesView();
			}

			picturesView.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + currentImgId));

			// picturesView.setOnClickListener(new View.OnClickListener() {
			// public void onClick(View v) {
			//
			// ImageHolder viewHolder = (ImageHolder) v.getTag();
			// System.out.println("imgClick.imgId: " + viewHolder.getImageId());
			//
			// Intent intent = new Intent(getApplicationContext(),
			// ViewImage.class);
			// intent.putExtra("imageId", viewHolder.getImageId());
			// startActivity(intent);
			// // finish();
			//
			// }
			// });
			return convertView;
		}
	}

	/** Holds child views for one row. */
	class ImageHolder {
		private ImageView picturesView;
		private String imgId;

		public ImageHolder() {
		}

		public ImageHolder(ImageView picturesView, String imgId) {
			this.picturesView = picturesView;
			this.imgId = imgId;
		}

		public String getImageId() {
			return imgId;
		}

		public ImageView getpicturesView() {
			return picturesView;
		}

		public void setpicturesView(ImageView picturesView) {
			this.picturesView = picturesView;
		}

		public void setImageId(String imgId) {
			this.imgId = imgId;
		}
	}
}
