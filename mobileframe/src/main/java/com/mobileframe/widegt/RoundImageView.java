package com.mobileframe.widegt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mobileframe.tools.DensityUtil;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：圆角ImageView
 * 作者：tgl
 * 创建时间：2017/12/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RoundImageView extends ImageView {

    private final int DEFAULT_RADIUS = 4;

    private RectF mRoundRect = new RectF();
    private int x_radius;
    private int y_radius;
    private Path mPath = new Path();

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, 0);
//        x_radius = a.getDimensionPixelSize(R.styleable.RoundImageView_x_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));
//        y_radius = a.getDimensionPixelSize(R.styleable.RoundImageView_y_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));
//        a.recycle();
        x_radius = DensityUtil.dip2px(getContext(), DEFAULT_RADIUS);
        y_radius = x_radius;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRoundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable mDrawable = getDrawable();
        if (mDrawable == null) {
            return;
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;
        }

        canvas.save();
        mPath.reset();
        mPath.addRoundRect(mRoundRect, x_radius, y_radius, Path.Direction.CW);
        canvas.clipPath(mPath);
        mDrawable.draw(canvas);

        canvas.restore();
    }


}

