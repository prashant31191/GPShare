package com.devtechdesign.gpshare.profile.imgs;

import com.devtechdesign.gpshare.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ImageWithProgress extends RelativeLayout {

	private ProgressBar progressBar;
	private ImageView imgViewmain;

	public ImageWithProgress(Context context) {
		super(context);
		setupView(context);
	}

	private void setupView(Context context) {

		LayoutInflater li = LayoutInflater.from(context);
		View v = li.inflate(R.layout.image_with_progress, null, false);

		setImgViewmain((ImageView) v.findViewById(R.id.imgMain));
		setProgressBar((ProgressBar) v.findViewById(R.id.progressBar));

	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public ImageView getImgViewmain() {
		return imgViewmain;
	}

	public void setImgViewmain(ImageView imgViewmain) {
		this.imgViewmain = imgViewmain;
	}

}
