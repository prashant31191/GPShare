package com.devtechdesign.gpshare.profile.imgs;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.elements.Image;

public class ImgGvItem extends RelativeLayout {

	private ImageView ivImage;
	private CheckBox checkBox;
	private boolean checkBoxChecked;
	private int id;
	private Image img;

	public ImgGvItem(Context context) {
		super(context);

		setLayoutParams(new GridView.LayoutParams(350, 350));
		checkBox = new CheckBox(new ContextThemeWrapper(context, R.style.cb_img_select));
		checkBox.setLayoutParams(new GridView.LayoutParams(200, 200));
		checkBox.setVisibility(View.GONE);
		ivImage = new ImageView(context);
		ivImage.setLayoutParams(new GridView.LayoutParams(345, 345));
		ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ivImage.setPadding(5, 5, 5, 5);
		addView(ivImage);
		addView(checkBox);

	}

	public void setImageBm(Bitmap bitmap) {
		ivImage.setImageBitmap(bitmap);
	}

	public boolean isCheckBoxChecked() {
		return checkBoxChecked;
	}

	public void setCheckBoxChecked(boolean checkBoxChecked) {
		this.checkBoxChecked = checkBoxChecked;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public ImageView getIvImage() {
		return ivImage;
	}

	public void setIvImage(ImageView ivImage) {
		this.ivImage = ivImage;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setImgObject(Image img) {
		this.setImg(img);
	}

	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}
}
