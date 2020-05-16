package com.mobileframe.widegt;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * 加强版的EditText,可以响应DrawableLeft 和 DrawableRight的点击事件
 * 要实现响应点击,先设置setDrawableListener
 * @author 40483
 */
public class DrawableEditText extends EditText
{
		private DrawableLeftListener mLeftListener ;
		private DrawableRightListener mRightListener ;
		
		final int DRAWABLE_LEFT = 0;
	    final int DRAWABLE_TOP = 1;
	    final int DRAWABLE_RIGHT = 2;
	    final int DRAWABLE_BOTTOM = 3;

		public DrawableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
		}

		public DrawableEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public DrawableEditText(Context context) {
			super(context);
		}

		public void setDrawableLeftListener(DrawableLeftListener listener) {
			this.mLeftListener = listener;
		}

		public void setDrawableRightListener(DrawableRightListener listener) {
			this.mRightListener = listener;
		}

		public interface DrawableLeftListener {
			public void onDrawableLeftClick(View view) ;
		}
		
		public interface DrawableRightListener {
			public void onDrawableRightClick(View view) ;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (mRightListener != null) {
					Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT] ;
					int drawablePadding = getCompoundDrawablePadding();
					if (drawableRight != null && event.getRawX() >= (getRight()
							- drawableRight.getBounds().width()-drawablePadding)) {
						mRightListener.onDrawableRightClick(this) ;
		        		return true ;
					}
				}
				
				if (mLeftListener != null) {
					Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT] ;
					int drawablePadding = getCompoundDrawablePadding();
					if (drawableLeft != null && event.getRawX() <= (drawablePadding+getLeft()
							+ drawableLeft.getBounds().width())) {
						mLeftListener.onDrawableLeftClick(this) ;
		        		return true ;
					}
				}
				break;
			}
			
			return super.onTouchEvent(event);
		}
		//不可编辑
		public void lock(){
			setTag(1);
			setFilters(new InputFilter[] {
					new InputFilter() {
						@Override
						public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
						{
							// TODO Auto-generated method stub
							return null;
						}     
				}});
		}
		public void unLock(){
			setTag(0);
			setFilters(new InputFilter[] {
					new InputFilter() {
						public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
						{
							return source.length() < 1 ? dest.subSequence(dstart, dend) : "";    
						}     
				}});
		}
}
