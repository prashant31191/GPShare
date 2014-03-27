package com.devtechdesign.gpshare.layouts;

import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.map.GpshareMapFragment;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GpshareSlidingPaneLayout extends SlidingPaneLayout {

	public GpshareSlidingPaneLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		if (Globals.getCurrentFrag() instanceof GpshareMapFragment) {
			return false;
		}
		return super.onInterceptTouchEvent(event);
	}
}
