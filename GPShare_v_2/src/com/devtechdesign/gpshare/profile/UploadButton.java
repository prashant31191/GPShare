package com.devtechdesign.gpshare.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devtechdesign.gpshare.R;

public class UploadButton extends LinearLayout {

	private Button btnUpload;
	private TextView tvUploadCount;

	/**
	 * @param context
	 */
	public UploadButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutParams();
		setupView(context);
	}

	/**
	 * 
	 */
	private void setLayoutParams() {

		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
	}

	/**
	 * @param context
	 */
	private void setupView(Context context) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.upload_button_progressbar, this, true);

		btnUpload = (Button) v.findViewById(R.id.btnUpload);
		tvUploadCount = (TextView) findViewById(R.id.tvUploadCount);

	}

	public void setUploadCountText(int count) {
		tvUploadCount.setText(String.valueOf(count));
	}

	public void setClickListener(OnClickListener l) {
		btnUpload.setOnClickListener(l);
	}
}
