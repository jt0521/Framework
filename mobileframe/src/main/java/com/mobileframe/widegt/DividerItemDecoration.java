package com.mobileframe.widegt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

/**
 * Created by tgl on 2016/3/31.
 *
 * @Directions :绘制RecyclerView 的分割线,水平、竖直、九宫格间隙
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 横、竖列表
     */
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    public static final int SPACING_GRID = 100;

    /**
     * android Theme中的属性
     *
     * @ return Drawable
     */
    private final int[] Attr = {
            android.R.attr.listDivider
    };
    private Context mContext;
    /**
     * 分割线图片
     */
    private Drawable mDividerDrawable;
    /**
     * 列表样式：横竖,九宫格
     */
    private int mOrientation;

    /**
     * 此属性在列表样式为九宫格时有效，表示间隙大小，默认为10
     */
    private int spaceSize = 10;

    /**
     * 列表样式为九宫格时使用，表示间隙大小
     *
     * @param spaceSize
     */
    public void setSpaceSize(int spaceSize) {
        this.spaceSize = spaceSize;
    }

    /**
     * @param mContext    上下文
     * @param orientation 方向
     */
    public DividerItemDecoration(Context mContext, int orientation) {
        super();
        init(mContext, orientation, -1);
    }

    /**
     * @param mContext
     * @param orientation
     * @param resDrawable
     */
    public DividerItemDecoration(Context mContext, int orientation, @DrawableRes int resDrawable) {
        super();
        init(mContext, orientation, resDrawable);
    }

    private void init(Context mContext, int orientation, @DrawableRes int resDrawable) {
        this.mContext = mContext;
        if (orientation == HORIZONTAL_LIST || orientation == VERTICAL_LIST) {
            if (resDrawable > 0) {
                mDividerDrawable = ContextCompat.getDrawable(mContext, resDrawable);
            } else {
                //android theme中的属性，可以直接设置获取
                TypedArray a = mContext.obtainStyledAttributes(Attr);
                mDividerDrawable = a.getDrawable(0);
                a.recycle();
            }
        } else if (orientation == SPACING_GRID) {//九宫格列表

        } else {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_LIST) {
            drawHORIZONTALDivider(c, parent);
        } else if (mOrientation == VERTICAL_LIST) {
            drawVERTICALDivider(c, parent);
        } else {

        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDividerDrawable.getIntrinsicHeight());
        } else if (mOrientation == HORIZONTAL_LIST) {
            outRect.set(0, 0, mDividerDrawable.getIntrinsicWidth(), 0);
        } else {//grid布局分割线
            //不是第一个的格子都设一个左边和底部的间距
            outRect.left = spaceSize;
            outRect.bottom = spaceSize;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) % ((GridLayoutManager) parent.getLayoutManager()).getSpanCount() == 0) {
                outRect.left = 0;
            }
        }
    }

    /**
     * 竖直列表分割线
     */
    private void drawVERTICALDivider(Canvas canvas, RecyclerView recyclerView) {
        int left = recyclerView.getPaddingLeft();
        int right = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = recyclerView.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            int top = view.getBottom() + lp.bottomMargin;
            int bottom = top + mDividerDrawable.getIntrinsicHeight();
            mDividerDrawable.setBounds(left, top, right, bottom);
            mDividerDrawable.draw(canvas);
        }
    }

    /**
     * 横向列表分割线
     */
    private void drawHORIZONTALDivider(Canvas canvas, RecyclerView recyclerView) {
        int top = recyclerView.getPaddingTop();
        int bottom = recyclerView.getHeight() - recyclerView.getPaddingBottom();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = recyclerView.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int left = childView.getLeft() + params.leftMargin;
            int right = childView.getRight() + params.rightMargin;
            mDividerDrawable.setBounds(left, top, right, bottom);
            mDividerDrawable.draw(canvas);
        }
    }
}
