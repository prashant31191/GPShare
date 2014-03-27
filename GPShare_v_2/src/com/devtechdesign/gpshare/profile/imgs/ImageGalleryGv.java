package com.devtechdesign.gpshare.profile.imgs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.Images.ImageConversionFactory;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.utility.Utility;

public class ImageGalleryGv extends Activity {

	private static Uri[] mUrls = null;
	private static String[] strUrls = null;
	private String[] mNames = null;
	private GridView gridview = null;
	private Cursor cc = null;
	private Button btnMoreInfo = null;
	private ProgressDialog myProgressDialog = null;
	public ArrayList<Image> imgAry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.image_gallery_gridview);
		// It have to be matched with the directory in SDCard
		cc = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

		if (cc != null) {

			imgAry = Transactions.getImageaFromPhoneDb("");

			gridview = (GridView) findViewById(R.id.gridview);
			gridview.setAdapter(new ImageAdapter(this));
		}

	}

	public class AsyncTaskExample extends AsyncTask<String, Void, String> {
		private String Result = "";
		// private final static String SERVICE_URI = "http://10.0.2.2:8889";
		private final static String SERVICE_URI = "http://10.0.2.2:65031/SampleService.svc";

		public String GetSEssion(String URL) {

			HttpClient client = new DefaultHttpClient();
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				response = httpclient.execute(new HttpGet("http://www.devtechdesign.com/gpshare/svc/GPShare.svc/getRoutesByPersonIdRange/192/1/2000"));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				// TODO Handle problems..
			} catch (IOException e) {
				// TODO Handle problems..
			}
			return responseString;
		}

		@Override
		protected String doInBackground(String... arg0) {
			android.os.Debug.waitForDebugger();
			String t = GetSEssion(SERVICE_URI);
			return t;
		}

		@Override
		protected void onPostExecute(String result) {
			// host.values = Result;
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}

	/**
	 * This class loads the image gallery in grid view.
	 * 
	 */
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;

			if (Utility.model == null) {
				Utility.model = new ImageConversionFactory();
			}

			Utility.model.setListener(this);
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

		public View getView(int position, View convertView, ViewGroup parent) {

			Image img = imgAry.get(position);

			ImgGvItem imageView;
			if (convertView == null) {
				imageView = new ImgGvItem(mContext);
			} else {
				imageView = (ImgGvItem) convertView;
			}

			Bitmap bmp = Utility.model.getLocalImage(img.getImgThumbNail(), img.getImgThumbNail());
			imageView.setImageBm(bmp);

			return imageView;

		}

		private void destroyImage() {
			int index = gridview.getFirstVisiblePosition();

			ImageView iv = (ImageView) gridview.getChildAt(index);
			if (iv != null) {
				iv.setImageBitmap(null);
			}
		}
	}

	/**
	 * This method is to scale down the image
	 */
	public Bitmap decodeURI(String filePath) {

		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Only scale if we need to
		// (16384 buffer for img processing)
		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
		if (options.outHeight * options.outWidth * 2 >= 16384) {
			// Load, scaling to smallest power of 2 that'll get it <= desired
			// dimensions
			double sampleSize = scaleByHeight ? options.outHeight / 100 : options.outWidth / 100;
			options.inSampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize) / Math.log(2d)));
		}

		// Do the actual decoding
		options.inJustDecodeBounds = false;
		options.inTempStorage = new byte[512];
		Bitmap output = BitmapFactory.decodeFile(filePath, options);

		return output;
	}
}