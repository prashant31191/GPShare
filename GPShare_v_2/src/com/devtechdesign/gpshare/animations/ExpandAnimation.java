package com.devtechdesign.gpshare.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout.LayoutParams;

/**
 * This animation class is animating the expanding and reducing the size of a
 * view. The animation toggles between the Expand and Reduce, depending on the
 * current state of the view
 * 
 * @author Udinic
 * 
 */
public class ExpandAnimation extends Animation {
	private View			mAnimatedView;
	private LayoutParams	mViewLayoutParams;
	private int				mMarginStart, mMarginEnd;
	private boolean			mIsVisibleAfter		= false;

	private boolean			mWasEndedAlready	= false;
	private int expanded;

	/**
	 * Initialize the animation
	 * 
	 * @param view
	 *            The layout we want to animate
	 * @param duration
	 *            The duration of the animation, in ms
	 */
	public ExpandAnimation(View view, int duration) {

		setDuration(duration);
		mAnimatedView = view;
		mViewLayoutParams = (LayoutParams) view.getLayoutParams();

		// decide to show or hide the view
		mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);

		mMarginStart = mViewLayoutParams.bottomMargin;
		mMarginEnd = (mMarginStart == 0 ? (0 - view.getHeight()) : 0);

		view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);

		if (interpolatedTime < 1.0f) {
			// Calculating the new bottom margin, and setting it
			mViewLayoutParams.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

			// Invalidating the layout, making us seeing the changes we made
			mAnimatedView.requestLayout();

			// Making sure we didn't run the ending before (it happens!)
		} else if (!mWasEndedAlready) {
			mViewLayoutParams.bottomMargin = mMarginEnd;
			mAnimatedView.requestLayout();

			if (mIsVisibleAfter) {
				mAnimatedView.setVisibility(View.GONE);
			}
			mWasEndedAlready = true;
		}
	}

	@Override
	public boolean hasEnded() { 
		return super.hasEnded();
	}

	public boolean ismIsVisibleAfter() {
		return mIsVisibleAfter;
	}

	public void setmIsVisibleAfter(boolean mIsVisibleAfter) {
		this.mIsVisibleAfter = mIsVisibleAfter;
	}

	public int getExpanded() {
		return expanded;
	}

	public void setExpanded(int expanded) {
		this.expanded = expanded;
	}

}