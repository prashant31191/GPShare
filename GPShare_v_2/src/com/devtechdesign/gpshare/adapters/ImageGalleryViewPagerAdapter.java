package com.devtechdesign.gpshare.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.Images.ImageFragment;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.aRoute;

public class ImageGalleryViewPagerAdapter extends FragmentPagerAdapter {
	private Activity act;
	ArrayList<Image> imageAry;

	public ImageGalleryViewPagerAdapter(Activity act, Context context, FragmentManager fm, ArrayList<Image> imageAry) {
		super(fm);
		this.act = act;
		this.imageAry = imageAry;
	}

	@Override
	public Fragment getItem(int position) {

		Fragment f = new Fragment();
		ImageView view = new ImageView(act);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		Image img = imageAry.get(position);
		aRoute cRoute = new aRoute(null);
		cRoute.setX(img.getimgX());
		cRoute.setY(img.getimgY());
		cRoute.setImgUrl(img.getimgPath());
		cRoute.setCreateDate(img.getimgCreateDate());
		cRoute.setPlaceId(img.getimgPlace());

		Globals.setCurrentRoute(cRoute);

		f = ImageFragment.newInstance(img.getimgPath());

		// galleryAct.zoomToPic(img.getimgX(), img.getimgY());

		return f;
	}

	@Override
	public int getCount() {
		return imageAry.size();
	}
}