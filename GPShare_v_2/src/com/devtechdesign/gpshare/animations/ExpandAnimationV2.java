package com.devtechdesign.gpshare.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout.LayoutParams;

public class ExpandAnimationV2 {
	private View v;
	private LayoutParams mViewLayoutParams;
	private int mMarginStart, mMarginEnd;
	private boolean mIsVisibleAfter = false;

	private boolean mWasEndedAlready = false;
	private int expanded;

	public ExpandAnimationV2() {
	}

	public void expand(final View v) {

		mViewLayoutParams = (LayoutParams) v.getLayoutParams();

		// decide to show or hide the view
		mIsVisibleAfter = (v.getVisibility() == View.VISIBLE);

		mMarginStart = mViewLayoutParams.bottomMargin;
		mMarginEnd = (mMarginStart == 0 ? (0 - v.getHeight()) : 0);

		v.setVisibility(View.VISIBLE);

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {

				mViewLayoutParams.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration(500);
		v.startAnimation(a);
	}

	public void collapse(final View v) {

		mViewLayoutParams = (LayoutParams) v.getLayoutParams();

		// decide to show or hide the view
		mIsVisibleAfter = (v.getVisibility() == View.VISIBLE);

		mMarginStart = mViewLayoutParams.bottomMargin;
		mMarginEnd = (mMarginStart == 0 ? (0 - v.getHeight()) : 0);
		
		if (mMarginEnd == 0)
			return;

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {

				mViewLayoutParams.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration(500);
		v.startAnimation(a);
	}
}
