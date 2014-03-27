package com.devtechdesign.gpshare.adapters;

import java.util.ArrayList;

import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.Images.FullScreenGalleryPager;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.map.LargeGalleryMapPic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ImagePagerAdapter extends PagerAdapter implements SoapInterface {

	Activity activity;
	ArrayList<Image> imageAry;

	LargeGalleryMapPic galleryAct = null;
	private String dataType;

	public ImagePagerAdapter(Activity act, ArrayList<Image> imageAry) {
		this.imageAry = imageAry;
		activity = act;
	}

	public int getCount() {
		return imageAry.size();
	}

	@Override
	public Object instantiateItem(View collection, int i) {

		RelativeLayout rl = new RelativeLayout(activity);
		ImageView view = new ImageView(activity);
		ProgressBar pb = new ProgressBar(activity);
		FullScreenGalleryPager fullGalleryAct = null;

		if (activity instanceof LargeGalleryMapPic) {
			galleryAct = (LargeGalleryMapPic) activity;
			dataType = galleryAct.getDataType();
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		// Defining the layout parameters of the TextView
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		rl.setLayoutParams(lp);
		rl.addView(view);
		rl.addView(pb);

		Image img = imageAry.get(i);
		aRoute cRoute = new aRoute(null);
		cRoute.setX(img.getimgX());
		cRoute.setY(img.getimgY());
		cRoute.setImgUrl(img.getimgPath());
		cRoute.setCreateDate(img.getimgCreateDate());
		cRoute.setPlaceId(img.getimgPlace());

		// scrollLargeGallery(i);

		Globals.setCurrentRoute(cRoute);

		Bitmap bMap = null;

		view.setImageBitmap(bMap);
		view.setTag(img.getimgPath());
		view.setOnClickListener(imageViewOnclick);

		((ViewPager) collection).addView(rl, 0);

		return rl;
	}

	private String getCorrectImgPath(Image image) {
		switch (image.getImgType()) {
		case ONLINE_IMG:

			return image.getLargeUrl();
		case OFFLINE_IMG:

			return image.getimgPath();
		}
		return null;
	}

	// private void scrollLargeGallery(int i) {
	// if (activity instanceof FullScreenGalleryPager) {
	// try {
	// GPContextHolder.getLargeGalleryPic().getMapGalleryViewPager().setCurrentItem(i
	// - 1);
	// } catch (NullPointerException e) {
	//
	// }
	// }
	// }

	public int findImageIndex(String imgFilePath) {
		int i = 0;
		for (Image img : imageAry) {
			if (img.getimgPath().equals(imgFilePath))
				return i;
			else
				i++;
		}
		return i;
	}

	public String getImgFilePath(int index) {
		Image img = imageAry.get(index);
		return img.getimgPath();
	}

	OnClickListener imageViewOnclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			System.out.println("system.out: " + v.getContext());
			if (v.getContext() instanceof LargeGalleryMapPic) {

				String imgPath = (String) v.getTag();

				Intent fullGalleryPagerIntent = new Intent(v.getContext(), FullScreenGalleryPager.class);
				fullGalleryPagerIntent.putExtra("currentImgPath", imgPath);
				fullGalleryPagerIntent.putExtra("dataType", dataType);
				galleryAct.startActivity(fullGalleryPagerIntent);
			} else {

			}
		}
	};

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	public void setImgFilePath(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	public ArrayList<Image> getImageAry() {
		return imageAry;
	}

	public void setImageAry(ArrayList<Image> imageAry) {
		this.imageAry = imageAry;
	}
}
