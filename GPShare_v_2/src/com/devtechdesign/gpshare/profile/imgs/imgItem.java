package com.devtechdesign.gpshare.profile.imgs;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.map.LargeGalleryMapPic;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;

import dev.tech.images.Util;

public class imgItem extends LinearLayout implements SoapInterface {

	ArrayList<Image> imgs;
	private RelativeLayout lLayout;
	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private ImageView img4;
	Context context;
	int i = 1;
	private CheckBox cb1;
	private CheckBox cb2;
	private CheckBox cb3;
	private CheckBox cb4;

	private String uri1;
	private String uri2;
	private String uri3;
	private String uri4;

	public imgItem(Context context, ArrayList<Image> imgs) {
		super(context);
		this.imgs = imgs;
		this.context = context;
		LayoutInflater li = LayoutInflater.from(context);
		lLayout = (RelativeLayout) li.inflate(R.layout.image_profile_img, null, false);

		setImg1((ImageView) lLayout.findViewById(R.id.img1));
		img2 = (ImageView) lLayout.findViewById(R.id.img2);
		img3 = (ImageView) lLayout.findViewById(R.id.img3);
		img4 = (ImageView) lLayout.findViewById(R.id.img4);

		setCb1((CheckBox) lLayout.findViewById(R.id.cb1));
		setCb2((CheckBox) lLayout.findViewById(R.id.cb2));
		setCb3((CheckBox) lLayout.findViewById(R.id.cb3));
		setCb4((CheckBox) lLayout.findViewById(R.id.cb4));

		loadImages(imgs);

		addView(lLayout);

	}

	private void setImageStates(ImageView iv, Bitmap bitmap) {
		StateListDrawable states = new StateListDrawable();
		Drawable drawableBm = new BitmapDrawable(getResources(), bitmap);
		states.addState(new int[] { android.R.attr.state_pressed }, getResources().getDrawable(R.drawable.layer_list_img_selector));
		states.addState(new int[] { android.R.attr.state_focused }, getResources().getDrawable(R.drawable.layer_list_img_selector));
		states.addState(new int[] { -android.R.attr.state_pressed }, drawableBm);

		iv.setBackgroundDrawable(states);

	}

	private void loadImages(ArrayList<Image> imgs) {
		for (Image img : imgs) {
			new GetLocalPicAsyncTask().execute(img);
		}
		if (thumbsCreated > 0) {
			Toast.makeText(getContext(), thumbsCreated + " missing Thumbnail images were generated. Please restart app to view now", 2).show();
		}
	}

	int thumbsCreated = 0;

	private String getThumbPath(String path) {
		File tFile = new File("sdcard/gpshare/photos/thumbs/" + new File(path).getName());//
		if (!tFile.exists()) {
			new SaveThumbPhotoTask().execute(path);
		}
		return tFile.getAbsolutePath();
	}

	class SaveThumbPhotoTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			thumbsCreated++;
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... filePath) {

			// Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg[0] , 0,
			// jpeg[0].length);
			// bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
			File imgFile = new File(filePath[0]);

			Bitmap thumbBitmap = Util.decodeFileImg(imgFile, 100);
			String fPath = Environment.getExternalStorageDirectory() + "/GPShare/Photos/Thumbs/" + imgFile.getName();
			try {
				Util.saveThumbnail(fPath, thumbBitmap);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class GetLocalPicAsyncTask extends AsyncTask<Image, Void, Bitmap> {
		String uri;

		@Override
		protected Bitmap doInBackground(Image... obj) {

			Image img = (Image) obj[0];

			switch (img.getImgType()) {
			case ONLINE_IMG:

				Bitmap tempbm = Utility.getOnlineBitmap(img.getLargeUrl());
				return scaleImg(tempbm);
			}

			String thumbUri = getThumbPath(img.getimgPath());
			uri = img.getimgPath();
			return scaleImg(Util.decodeFileImg(new File(thumbUri), 50));
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			switch (i) {
			case 1:
				getImg1().setImageBitmap(result);
				getImg1().setTag(i);
				setUri1(uri);
				getImg1().setOnClickListener(imgTouch);
				getImg1().setOnLongClickListener(imgLongClick);
				i++;
				return;
			case 2:
				img2.setImageBitmap(result);
				img2.setTag(i);
				setUri2(uri);
				img2.setOnClickListener(imgTouch);
				img2.setOnLongClickListener(imgLongClick);
				i++;
				return;
			case 3:
				img3.setImageBitmap(result);
				img3.setTag(i);
				setUri3(uri);
				img3.setOnClickListener(imgTouch);
				img3.setOnLongClickListener(imgLongClick);
				i++;
				return;
			case 4:
				img4.setImageBitmap(result);
				img4.setTag(i);
				setUri4(uri);
				img4.setOnClickListener(imgTouch);
				img4.setOnLongClickListener(imgLongClick);
				i++;
				return;
			}
		}
	}

	private Bitmap scaleImg(Bitmap bm) {
		if (bm != null) {
			return Bitmap.createScaledBitmap(bm, 80, 78, true);
		}
		return bm;
	}

	private boolean multiSelect;

	public OnLongClickListener imgLongClick = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View arg0) {

			return false;
		}
	};

	OnClickListener imgTouch = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Integer i = (Integer) v.getTag();
			Image cImage = imgs.get(i - 1);

			if (!multiSelect)
				goToMapImageGallery(cImage);
		}
	};

	private void goToMapImageGallery(Image cImage) {

		Intent mapIntent = new Intent(context, LargeGalleryMapPic.class);

		mapIntent.putExtra("imgFilePath", cImage.getimgPath());
		mapIntent.putExtra("x", cImage.getimgX());
		mapIntent.putExtra("y", cImage.getimgY());
		mapIntent.putExtra("imgPath", cImage.getimgPath());
		mapIntent.putExtra("mapDataId", cImage.getimageId());
		mapIntent.putExtra("placeName", cImage.getimgPlace());
		mapIntent.putExtra("dataType", Transactions.determineDataType(cImage));
		mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		aRoute currentRoute = new aRoute(null);
		currentRoute.setImgUrl(cImage.getimgPath());
		currentRoute.setX(cImage.getimgX());
		currentRoute.setY(cImage.getimgY());
		currentRoute.setRouteId(cImage.getimageId());
		currentRoute.setPlaceName(cImage.getimgPlace());
		Globals.setCurrentRoute(currentRoute);

		context.startActivity(mapIntent);
	}

	public CheckBox getCb1() {
		return cb1;
	}

	public void setCb1(CheckBox cb1) {
		this.cb1 = cb1;
	}

	public CheckBox getCb2() {
		return cb2;
	}

	public void setCb2(CheckBox cb2) {
		this.cb2 = cb2;
	}

	public CheckBox getCb3() {
		return cb3;
	}

	public void setCb3(CheckBox cb3) {
		this.cb3 = cb3;
	}

	public CheckBox getCb4() {
		return cb4;
	}

	public void setCb4(CheckBox cb4) {
		this.cb4 = cb4;
	}

	public String getUri4() {
		return uri4;
	}

	public void setUri4(String uri4) {
		this.uri4 = uri4;
	}

	public String getUri3() {
		return uri3;
	}

	public void setUri3(String uri3) {
		this.uri3 = uri3;
	}

	public String getUri2() {
		return uri2;
	}

	public void setUri2(String uri2) {
		this.uri2 = uri2;
	}

	public String getUri1() {
		return uri1;
	}

	public void setUri1(String uri1) {
		this.uri1 = uri1;
	}

	public void setCbClickListeners(OnCheckedChangeListener btnCheckBoxOnclickListener) {
		getCb1().setOnCheckedChangeListener(btnCheckBoxOnclickListener);
		getCb2().setOnCheckedChangeListener(btnCheckBoxOnclickListener);
		getCb3().setOnCheckedChangeListener(btnCheckBoxOnclickListener);
		getCb4().setOnCheckedChangeListener(btnCheckBoxOnclickListener);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	public ImageView getImg1() {
		return img1;
	}

	public void setImg1(ImageView img1) {
		this.img1 = img1;
	}
}
