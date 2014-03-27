package com.devtechdesign.gpshare.Images;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.profile.imgs.Gallery;
import com.devtechdesign.gpshare.utility.GAnal;
import com.devtechdesign.gpshare.utility.Utility;

import dev.tech.gpsharelibs.EGInterface;

public class ImageGallery extends LinearLayout implements EGInterface {

	private Context context;

	public ImageGallery(Context context) {
		super(context);
		this.context = context;
	}

	/** Called when the activity is first created. */
	public static int THEME = R.style.EarthGallery;
	private Cursor mCursor;
	GridView imagegrid;
	private ArrayList<Image> imgList = new ArrayList<Image>();
	private GPShare GPShare;
	GAnal GAnal;
	private LinearLayout linearLayoutGalleryHolder;
	private LinearLayout lLayout;

	public Gallery getView() {

		LayoutInflater li = LayoutInflater.from(context);
		lLayout = (LinearLayout) li.inflate(R.layout.image_gallery, null, false);

		GAnal = new GAnal(context);

		linearLayoutGalleryHolder = (LinearLayout) findViewById(R.id.linearLayoutGalleryHolder);
		Gallery pLayout = new Gallery(context, null, -2, 3000);
		// linearLayoutGalleryHolder.addView(pLayout);

		GPContextHolder.setImgGalleryAct(this);
		return pLayout;
	}

	public void goToCamera(View v) {

		Intent toLaunch = new Intent("android.intent.action.MAIN");
		toLaunch.setPackage(PKG_PUDDING);
		// toLaunch.addCategory("android.intent.category.LAUNCHER");
		context.startActivity(toLaunch);
	}

	public void GTrack(String event) {
		GAnal.recordClick(event);
	}

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter(ImageGallery imageGallery) {

			if (Utility.model == null) {
				Utility.model = new ImageConversionFactory();
			}

			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return imgList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Image currentImage = (Image) this.getItem(position);

			currentImage = imgList.get(position);

			ImageView picturesView;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.image_gallery_grid_item, null);
				picturesView = (ImageView) convertView.findViewById(R.id.grid_item);
				// Bitmap bMap = null;
				// picturesView.setImageBitmap(bMap);
				// new AsyncImgDl(bMap, currentImage.getimgPath(), picturesView,
				// "local");

				picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				picturesView.setPadding(2, 2, 2, 2);
				picturesView.setLayoutParams(new GridView.LayoutParams(150, 150));
				convertView.setTag(new ImageHolder(picturesView));
			} else {
				// Because we use a ViewHolder, we avoid having to call
				// findViewById().
				ImageHolder viewHolder = (ImageHolder) convertView.getTag();
				picturesView = viewHolder.getpicturesView();
			}

			picturesView.setImageBitmap(getBitmapForGallery(currentImage));

			return convertView;
		}
	}

	private Bitmap getBitmapForGallery(Image currentImage) {

		File file = new File(currentImage.getimgPath());
		String fileName = file.getName();

		if (!currentImage.getImgEarthGal()) {
			return Utility.model.getLocalImage(currentImage.getimgCreateDate(), Environment.getExternalStorageDirectory() + "/GPShare/Photos/Thumbs/" + fileName);
		} else {
			return Utility.model.getLocalImage(currentImage.getimgCreateDate(), currentImage.getimgPath());
		}
	}

	/** Holds child views for one row. */
	class ImageHolder {
		private ImageView picturesView;

		public ImageHolder() {
		}

		public ImageHolder(ImageView picturesView) {
			this.picturesView = picturesView;
		}

		public ImageView getpicturesView() {
			return picturesView;
		}

		public void setpicturesView(ImageView picturesView) {
			this.picturesView = picturesView;
		}
	}
}
