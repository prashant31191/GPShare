package com.devtechdesign.gpshare.Images;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.adapters.ImagePagerAdapter;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;

public class FullScreenGalleryPager extends Activity {

	private ImagePagerAdapter adapter;
	private ViewPager myPager;
	private String dataType;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.full_screen_gallery_pager);

		Bundle extras = getIntent().getExtras();
		dataType = extras.getString("dataType");

		if (!dataType.equals("online"))
			adapter = new ImagePagerAdapter(this, Transactions.getImageaFromPhoneDb(""));
		else
			adapter = new ImagePagerAdapter(this, GPContextHolder.getFriendProfileImagesLayout().getImgList());

		myPager = (ViewPager) findViewById(R.id.imgGalleryViewPager);
		myPager.setAdapter(adapter);

		String currentImgPath = extras.getString("currentImgPath");
		int i = adapter.findImageIndex(currentImgPath);
		myPager.setCurrentItem(i);
	}

	public ViewPager getMyPager() {
		return myPager;
	}

	public void setMyPager(ViewPager myPager) {
		this.myPager = myPager;
	}
}
