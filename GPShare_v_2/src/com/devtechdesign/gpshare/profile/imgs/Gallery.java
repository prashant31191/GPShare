package com.devtechdesign.gpshare.profile.imgs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import com.devtechdesign.gpshare.map.LargeGalleryMapPic;
import com.devtechdesign.gpshare.profile.UploadButton;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.facebook.FBImgUlSvc;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;
import com.devtechdesign.gpshare.Images.CustomCameraAct;
import com.devtechdesign.gpshare.Images.ImageConversionFactory;
import com.devtechdesign.gpshare.Images.ImageGridAdapter;
import com.dtd.dbeagen.db.elements.aPlaceMark;

import dev.tech.auth.DTDLogin;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class Gallery extends Fragment implements SoapInterface {

	private Context context;
	private int beginRange;
	private int endRange;
	private Button btnViewMoreImages;
	private Button btnCamera;
	private ImageButton btnOptions;
	private Spinner imgGallerySpinner;
	protected ArrayList<imgItem> imgItemalist = new ArrayList<imgItem>();
	private Button btnViewAllImages;
	private UploadButton lytUploadImgButton;
	private boolean selectAll;
	private boolean multiSelect;
	private int imgCount;
	private ArrayList<aRoute> routeList;
	private ArrayList<Image> imgList;
	private boolean friendProfile = true;
	private LinearLayout lytImgProfileControls;
	private ArrayList<Image> imgAry;
	private GridView gridview;
	private ImageGridAdapter gridViewAdapter;
	private Activity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup lLayout = (ViewGroup) inflater.inflate(R.layout.profile_images_layout, container, false);

		gridview = (GridView) lLayout.findViewById(R.id.gridview);

		// setBtnViewMoreImages((Button)
		// lLayout.findViewById(R.id.btnViewMoreImages));
		// btnViewAllImages = (Button)
		// lLayout.findViewById(R.id.btnViewAllImages);
		// btnCamera = (Button) lLayout.findViewById(R.id.btnCamera);

		// lytImgProfileControls = (LinearLayout)
		// lLayout.findViewById(R.id.lytImgProfileControls);
		// btnOptions = (ImageButton) lLayout.findViewById(R.id.btnOptions);
		// imgGallerySpinner = (Spinner)
		// lLayout.findViewById(R.id.imgGallerySpinner);
		// ArrayAdapter<CharSequence> adapter =
		// ArrayAdapter.createFromResource(context, R.array.image_gallery_array,
		// android.R.layout.simple_spinner_item);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// imgGallerySpinner.setAdapter(adapter);

		// lytUploadImgButton = (UploadButton)
		// lLayout.findViewById(R.id.lytUploadImgButton);
		// lytUploadImgButton.setClickListener(btnUploadAllImagesToFaceBookOnClick);
		// getBtnViewMoreImages().setOnClickListener(btnViewMoreImagesListener);
		// btnCamera.setOnClickListener(btnCameraOnclick);
		// btnOptions.setOnClickListener(btnOptionsClick);
		// imgGallerySpinner.setOnItemSelectedListener(spinnerSelectionListener);

		GPShareDataModel.getInstance().placeMarks = DatabaseControl.getInstance().gpsDbc.getImages();

		gridViewAdapter = new ImageGridAdapter(this.getActivity(), GPShareDataModel.getInstance().placeMarks);
		gridview.setAdapter(gridViewAdapter);
		gridViewAdapter.notifyDataSetChanged();
		gridview.invalidateViews();

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				aPlaceMark cImage = GPShareDataModel.getInstance().placeMarks.get(position);

				// goToMapImageGallery(cImage);
			}
		});

		return lLayout;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		if (gridview != null)
			gridview.invalidateViews();
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		// try {
		// mCallback = (JournalItemSelectedListener) activity;
		// } catch (ClassCastException e) {
		// throw new ClassCastException(activity.toString() +
		// " must implement OnHeadlineSelectedListener");
		// }
	}

	public Gallery(Context context, ArrayList<Image> imgList, int beginRange, int endRange) {

		this.context = context;
		this.imgList = imgList;
		this.beginRange = beginRange;
		this.endRange = endRange;

	}

	private void selectAllCheckBoxes() {

	}

	private void startEarthGallery() {
		Intent egIntent = new Intent("android.intent.action.MAIN");
		egIntent.setComponent(new ComponentName("dev.tech.earthGallery", "dev.tech.earthGallery.MainActivity"));
		egIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(egIntent);

	}

	public void incrementUploadCount() {
		imgCount++;
	}

	OnClickListener btnViewMoreImagesListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (beginRange == -1) {
				beginRange = 30;
				endRange = 60;
			} else {
				beginRange = endRange;
				endRange += 30;
			}
			// setUpProfilePicLayout();

			// svImages.fullScroll(View.FOCUS_DOWN);
		}
	};

	OnClickListener btnCameraOnclick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			try {
				Globals.getcurrentCoords().toString();
			} catch (NullPointerException e) {
				Globals.setcurrentCoords("0.0,0.0");
			}

			GPContextHolder.getMainAct().GoogleA.recordClick("CamButton");

			Intent i = new Intent(v.getContext(), CustomCameraAct.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			v.getContext().startActivity(i);
		}
	};

	OnClickListener btnOptionsClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			imgGallerySpinner.performClick();
		}
	};

	public void viewAllImages() {
		imgItemalist.clear();

		beginRange = -2;
		endRange = 3000;
		hideToggleImageViewingButtons();
	}

	OnClickListener btnUploadAllImagesToFaceBookOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				for (int i = 0; i < thumbnails.length; i++) {

					if (thumbnails[i] != "")
						if (!new File(thumbnails[i]).exists())
							thumbnails[i] = "";
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			if (thumbnails.length > 0) {
				initiateFacebookUpload(thumbnails);
				uncheckAllBoxes();
				lytUploadImgButton.setVisibility(View.INVISIBLE);
			} else {
				Toast.makeText(context, "No Images were selected!!!", 2).show();
			}
		}
	};

	OnItemSelectedListener spinnerSelectionListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View v, int arg2, long arg3) {
			TextView tv = (TextView) v;
			if (tv.getText().equals("Multi-select")) {
				displayShareCheckBoxes();
			}
			if (tv.getText().equals("Select All")) {
				selectAll = true;
				checkAndDisplayAllBoxes();
			}
			if (tv.getText().equals("Deselect All")) {
				uncheckAllBoxes();
			}
			selectAllCheckBoxes();
			imgGallerySpinner.setSelection(0);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private void initiateFacebookUpload(String[] filePaths) {
		if (!Utility.mFacebook.isSessionValid()) {
			Toast.makeText(this.getActivity(), "You must be logged in through Facebook to use multi-image upload", 2).show();
			Intent loginIntent = new Intent(this.getActivity(), DTDLogin.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(loginIntent);
		} else {
			Intent intent = new Intent(context, FBImgUlSvc.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("fileNames", filePaths);
			context.startService(intent);
		}
	}

	private void displayShareCheckBoxes() {

		multiSelect = true;
		gridview.setSelection(gridview.getCount());

	}

	private void checkAndDisplayAllBoxes() {

		multiSelect = true;
		for (int ii = 0; ii < checkBoxesSelection.length; ii++) {
			checkBoxesSelection[ii] = true;
			imgCount++;
			lytUploadImgButton.setVisibility(View.VISIBLE);
			lytUploadImgButton.setUploadCountText(imgCount);
			gridViewAdapter.notifyDataSetChanged();
			gridview.invalidateViews();
		}

		for (int ii = 0; ii < thumbnails.length; ii++) {
			thumbnails[ii] = imgAry.get(ii).getimgPath();
		}
	}

	private void addAllItemsToImageArray() {

	}

	private void uncheckAllBoxes() {

		for (int ii = 0; ii < checkBoxesSelection.length; ii++) {
			checkBoxesSelection[ii] = false;
			imgCount--;
			selectAll = false;
			lytUploadImgButton.setUploadCountText(imgCount);
		}
		gridViewAdapter.notifyDataSetChanged();
		gridview.invalidateViews();
	}

	public void hideImgProfileControls() {
		lytImgProfileControls.setVisibility(View.GONE);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	public void hideToggleImageViewingButtons() {
		if (btnViewAllImages.getVisibility() == View.GONE) {
			btnViewAllImages.setVisibility(View.VISIBLE);
			getBtnViewMoreImages().setVisibility(View.VISIBLE);
		} else {
			btnViewAllImages.setVisibility(View.GONE);
			getBtnViewMoreImages().setVisibility(View.GONE);
		}
	}

	public ArrayList<aRoute> getRouteList() {
		return routeList;
	}

	public void setRouteList(ArrayList<aRoute> routeList) {
		this.routeList = routeList;
	}

	public Button getBtnViewMoreImages() {
		return btnViewMoreImages;
	}

	public void setBtnViewMoreImages(Button btnViewMoreImages) {
		this.btnViewMoreImages = btnViewMoreImages;
	}

	private String[] thumbnails;
	private boolean[] checkBoxesSelection;
	private boolean[] checkBoxesVisible;
	private String[] arrPath;

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;

			if (Utility.model == null) {
				Utility.model = new ImageConversionFactory();
			}

			Utility.model.setListener(this);

			checkBoxesSelection = new boolean[imgAry.size()];
			checkBoxesVisible = new boolean[imgAry.size()];
			thumbnails = new String[imgAry.size()];
		}

		public int getCount() {
			return imgAry.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			Image img = imgAry.get(position);
			ViewHolder holder;
			ImgGvItem gvItem = null;
			if (convertView == null) {
				// holder = new ViewHolder();
				gvItem = new ImgGvItem(mContext);
				convertView = gvItem;
				gvItem.setImg(img);
				holder = new ViewHolder(gvItem);
				gvItem.getCheckBox().setTag(gvItem);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			gvItem = holder.getImgViewItem();
			Bitmap bmp = Utility.model.getLocalImage(img.getImgThumbNail(), img.getImgThumbNail());
			gvItem.setImageBm(bmp);
			gvItem.setId(position);

			gvItem.getCheckBox().setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					int id = ((ImgGvItem) cb.getTag()).getId();
					if (checkBoxesSelection[id]) {
						cb.setChecked(false);
						checkBoxesSelection[id] = false;
						thumbnails[id] = "";
						imgCount--;
						lytUploadImgButton.setUploadCountText(imgCount);
					} else {
						lytUploadImgButton.setVisibility(View.VISIBLE);
						imgCount++;
						thumbnails[id] = imgAry.get(id).getImgPath();
						lytUploadImgButton.setUploadCountText(imgCount);
						cb.setChecked(true);
						checkBoxesSelection[id] = true;
					}
				}
			});
			gvItem.getIvImage().setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
				}
			});

			gvItem.getCheckBox().setChecked(checkBoxesSelection[position]);

			if (multiSelect) {
				gvItem.getCheckBox().setVisibility(View.VISIBLE);
			} else {
				gvItem.getCheckBox().setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	private void goToMapImageGallery(Image cImage) {

		Intent mapIntent = new Intent(activity, LargeGalleryMapPic.class);
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

	class ViewHolder {
		private ImgGvItem gvImgItem;

		public ViewHolder(ImgGvItem imageView) {
			this.gvImgItem = imageView;
		}

		public ImgGvItem getImgViewItem() {
			return gvImgItem;
		}

		public void setImgViewItem(ImgGvItem imageView) {
			this.gvImgItem = imageView;
		}
	}

	public void resetImages() {

	}

	public ArrayList<Image> getImgList() {

		return null;
	}

}
