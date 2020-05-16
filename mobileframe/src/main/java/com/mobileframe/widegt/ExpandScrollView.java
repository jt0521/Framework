package com.mobileframe.widegt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


/**
 * 监听ScrollView滚动
 */
public class ExpandScrollView extends ScrollView {

	private ScrollListener scrollListener;
	private boolean isScrolling = false;
	private boolean isExpandSpec = false;
	public ExpandScrollView(Context context) {
		super(context);
	}

	public ExpandScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (isExpandSpec){
			int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}else{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setScrollListener(ScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollListener != null) {
			isScrolling = true;
			scrollListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(MotionEvent.ACTION_UP == ev.getAction()){
			if(isScrolling){
				scrollListener.onScrollStoped();
			}
			isScrolling = false;
		}
		return super.onTouchEvent(ev);
	}

	//--------------
	public interface ScrollListener{
		void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);
		void onScrollStoped();
	}

	public void setExpandSpec(boolean expandSpec) {
		isExpandSpec = expandSpec;
	}
}
