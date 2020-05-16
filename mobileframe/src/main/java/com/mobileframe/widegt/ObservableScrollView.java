package com.mobileframe.widegt;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.mobileframe.interfaces.OnScrollListener;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/3/19 11:51
 * Description: 滑动监听，兼容23以下
 * History:
 */
public class ObservableScrollView extends ScrollView {
    private OnScrollListener mListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mListener = listener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mListener != null) {
            mListener.onScrollChanged(x, y, oldx, oldy);
        }
    }
}