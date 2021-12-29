package com.x.holderview;

import android.view.View;

import androidx.annotation.IdRes;

import butterknife.ButterKnife;

/**
 * author: tgl
 * 简化listView、gridView的HolderView
 */
public class BaseHolderView {
    public View mItemView;
    private int mViewType;
    private static final int INVALID_TYPE = -1;

    public BaseHolderView(View itemView) {
        this(itemView, INVALID_TYPE);
    }

    public BaseHolderView(View itemView, int viewType) {
        mItemView = itemView;
        mViewType = viewType;
        ButterKnife.bind(mItemView);
    }

    public int getViewType() {
        return mViewType;
    }

    public <T extends View> T findViewById(@IdRes int viewId) {
        return mItemView.findViewById(viewId);
    }
}
