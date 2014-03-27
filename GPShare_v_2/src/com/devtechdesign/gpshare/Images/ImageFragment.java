package com.devtechdesign.gpshare.Images;

import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.utility.SoapInterface;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment implements SoapInterface {

	ImageView iv;
	static String imgPath;

	public static Fragment newInstance(String imgPath) {
		ImageFragment pageFragment = new ImageFragment();
		ImageFragment.imgPath = imgPath;
		return pageFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gallery_fragment, container, false);
		iv = (ImageView) view.findViewById(R.id.gallery_image);

		Bitmap bMap = null;
		new AsyncImgDl(bMap, imgPath, iv, PROFILE_IMG);
		iv.setImageBitmap(bMap);

		return view;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}
