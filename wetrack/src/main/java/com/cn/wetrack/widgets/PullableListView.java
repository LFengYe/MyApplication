package com.cn.wetrack.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cn.wetrack.R;


public class PullableListView extends ListView implements Pullable {
	public static final int INIT = 0;
	public static final int LOADING = 1;
	private OnLoadListener mOnLoadListener;
	private View mLoadmoreView;
	private ImageView mLoadingView;
	public TextView mStateTextView;
	private int state = INIT;
	private boolean canLoad = true;
	private boolean autoLoad = true;
	private AnimationDrawable mLoadAnim;

	public PullableListView(Context context) {
		super(context);
		init(context);
	}

	public PullableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mLoadmoreView = LayoutInflater.from(context).inflate(
				R.layout.load_more, null);
		mLoadingView = (ImageView) mLoadmoreView
				.findViewById(R.id.loading_icon);
		mLoadingView.setBackgroundResource(R.anim.loading_anim);
		mLoadAnim = (AnimationDrawable) mLoadingView.getBackground();
		mStateTextView = (TextView) mLoadmoreView
				.findViewById(R.id.loadstate_tv);
		mLoadmoreView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (state != LOADING) {
					load();
				}
			}
		});
		addFooterView(mLoadmoreView, null, false);
	}

	public void enableAutoLoad(boolean enable) {
		autoLoad = enable;
	}


	public void setLoadmoreVisible(boolean v) {
		if (v) {
			addFooterView(mLoadmoreView, null, false);
		} else {
			removeFooterView(mLoadmoreView);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			canLoad = false;
			break;
		case MotionEvent.ACTION_UP:
			canLoad = true;
			checkLoad();
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		checkLoad();
	}

	private void checkLoad() {
		if (reachBottom() && mOnLoadListener != null && state != LOADING
				&& canLoad && autoLoad)
			load();
	}

	private void load() {
		changeState(LOADING);
		mOnLoadListener.onLoad(this);
	}

	private void changeState(int state) {
		this.state = state;
		switch (state) {
		case INIT:
			mLoadAnim.stop();
			mLoadingView.setVisibility(View.INVISIBLE);
			mStateTextView.setText(R.string.more);
			break;

		case LOADING:
			mLoadingView.setVisibility(View.VISIBLE);
			mLoadAnim.start();
			mStateTextView.setText(R.string.loading);
			break;
		}
	}


	public void finishLoading() {
		changeState(INIT);
	}

	public boolean canPullDown() {
		if (getCount() == 0) {
			return true;
		} else if (getFirstVisiblePosition() == 0
				&& getChildAt(0).getTop() >= 0) {
			return true;
		} else
			return false;
	}

	public void setOnLoadListener(OnLoadListener listener) {
		this.mOnLoadListener = listener;
	}

	private boolean reachBottom() {
		if (getCount() == 0) {
			return true;
		} else if (getLastVisiblePosition() == (getCount() - 1)) {
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(
							getLastVisiblePosition()
									- getFirstVisiblePosition()).getTop() < getMeasuredHeight()
					&& !canPullDown())
				return true;
		}
		return false;
	}

	public interface OnLoadListener {
		void onLoad(PullableListView pullableListView);
	}
}
