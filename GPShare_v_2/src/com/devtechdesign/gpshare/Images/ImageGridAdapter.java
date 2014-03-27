package com.devtechdesign.gpshare.Images;

import java.util.ArrayList; 
import java.util.LinkedList;

import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.utility.Utility;
import com.dtd.dbeagen.db.elements.aPlaceMark;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageGridAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<aPlaceMark> imgAry;

	public ImageGridAdapter(Context context, LinkedList<aPlaceMark> placeMarks) {
		super();
		this.mContext = context;
		if (Utility.model == null) {
			Utility.model = new ImageConversionFactory();
		}

		Utility.model.setListener(this);

		this.imgAry = placeMarks;
	}

	@Override
	public int getCount() {
		return imgAry.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int index, View arg1, ViewGroup arg2) {
		ImageView imageView;
		if (arg1 == null) {
			imageView = new ImageView(mContext);
			float density = mContext.getResources().getDisplayMetrics().density;
			float width = mContext.getResources().getDisplayMetrics().widthPixels;

			int size = (int) (width / 3);
			imageView.setLayoutParams(new GridView.LayoutParams(size, size));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) arg1;
		}
		aPlaceMark img = imgAry.get(index);
		Bitmap bmp = Utility.model.getLocalImage(img.getpicUrl(), img.getpicUrl());
		imageView.setImageBitmap(bmp);
		return imageView;
	}

}
