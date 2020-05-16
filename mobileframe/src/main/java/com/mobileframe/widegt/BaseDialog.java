package com.mobileframe.widegt;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mobileframe.R;
import com.mobileframe.tools.DensityUtil;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/2/8 23:44
 * Description: 基础弹框样式
 * History:
 */
public class BaseDialog extends Dialog {
    private Spannable mTitle;
    private Spannable mContent;
    private View mCustomView;

    private LinearLayout mRootLl;
    protected LinearLayout mContainerLlBd;
    protected TextView mMiddleTv;
    protected TextView mContentTv;
    protected Button mBtn1;
    protected View mSpaceV;
    protected Button mBtn2;
    protected LinearLayout mBtnLl;
    protected int mWindowWidth;
    protected Context mContext;
    /**
     * 顶部留白
     */
    protected View mTopInterval;

    /**
     * 宽带满屏
     */
    private boolean mFullWidth;
    private int mGravity = Gravity.CENTER;

    public BaseDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public BaseDialog(@NonNull Context context, CharSequence content) {
        super(context, R.style.styleDialog);
        mContent = new SpannableString(content);
        mContext = context;
    }

    public BaseDialog(@NonNull Context context, CharSequence title, CharSequence content) {
        super(context, R.style.styleDialog);
        mTitle = new SpannableString(title);
        mContent = new SpannableString(content);
        mContext = context;
    }

    public BaseDialog(@NonNull Context context, Spannable title, Spannable content) {
        super(context, R.style.styleDialog);
        mTitle = title;
        mContent = content;
        mContext = context;
    }

    public BaseDialog(@NonNull Context context, View customView) {
        super(context, R.style.styleDialog);
        mCustomView = customView;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_base_dialog);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        int screenWidth = DensityUtil.getScreenW(getContext());
        mWindowWidth = mFullWidth ? screenWidth : screenWidth * 6 / 7;
        params.width = mWindowWidth;
        dialogWindow.setAttributes(params);
        dialogWindow.setGravity(mGravity);
        initView();

        if (mCustomView == null) {
            setMiddle(mTitle);
            mContentTv.setText(mContent);
        } else if (mFullWidth) {
            mRootLl.removeView(mContainerLlBd);
            mRootLl.addView(mCustomView, 0);
            mRootLl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
        } else {
            mContainerLlBd.removeAllViews();
            mContainerLlBd.addView(mCustomView);
        }
    }

    public void initView() {
        mContainerLlBd = findViewById(R.id.container_ll_bd);
        mMiddleTv = findViewById(R.id.middle_tv);
        mContentTv = findViewById(R.id.content_tv);
        mBtn1 = findViewById(R.id.btn_1);
        mSpaceV = findViewById(R.id.space_v);
        mBtn2 = findViewById(R.id.btn_2);
        mBtnLl = findViewById(R.id.button_ll);
        mRootLl = findViewById(R.id.root_ll);
        mTopInterval = findViewById(R.id.top_interval);
        if (mGravity == Gravity.BOTTOM) {
            ((ViewGroup.MarginLayoutParams) mRootLl.getLayoutParams()).bottomMargin = 0;
        }
    }

    /**
     * {@link #setMiddle(Spannable, int)}
     *
     * @param title
     */
    private void setMiddle(Spannable title) {
        setMiddle(title, Gravity.CENTER);
    }

    /**
     * 设置标题
     *
     * @param title   文本
     * @param gravity 文本位置
     */
    private void setMiddle(Spannable title, int gravity) {
        if (TextUtils.isEmpty(mTitle)) {
            mMiddleTv.setVisibility(mTitle == null ? View.GONE : View.INVISIBLE);
        } else {
            mMiddleTv.setVisibility(View.VISIBLE);
        }
        mMiddleTv.setText(title);
        mMiddleTv.setGravity(gravity);
    }

    public Button setButton1(CharSequence text, View.OnClickListener listener) {
        mBtnLl.setVisibility(View.VISIBLE);
        mBtn1.setVisibility(View.VISIBLE);
        if (mBtn2.getVisibility() == View.VISIBLE) {
            mSpaceV.setVisibility(View.VISIBLE);
        }
        mBtn1.setText(text);
        mBtn1.setOnClickListener(listener);
        return mBtn1;
    }

    public Button setButton2(CharSequence text, View.OnClickListener listener) {
        mBtnLl.setVisibility(View.VISIBLE);
        mBtn2.setVisibility(View.VISIBLE);
        if (mBtn1.getVisibility() == View.VISIBLE) {
            mSpaceV.setVisibility(View.VISIBLE);
        }
        mBtn2.setText(text);
        mBtn2.setOnClickListener(listener);
        return mBtn2;
    }

    /**
     * 单个button，居中
     *
     * @return
     */
    public Button setSingleCenterButton(CharSequence text, View.OnClickListener listener) {
        mBtnLl.setVisibility(View.VISIBLE);
        mBtn2.setVisibility(View.VISIBLE);
        if (mBtn1.getVisibility() == View.VISIBLE) {
            mSpaceV.setVisibility(View.VISIBLE);
        }
        mBtn2.setText(text);
        mBtn2.setOnClickListener(listener);
        mBtnLl.getLayoutParams().width = mWindowWidth / 2 - DensityUtil.dip2px(mContext, 24);
        ((LinearLayout.LayoutParams) mBtnLl.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
        return mBtn1;
    }

    public Button getBtn1() {
        return mBtn1;
    }

    public Button getBtn2() {
        return mBtn2;
    }

    /**
     * 设置dialog宽度满屏
     */
    public void setFullWidth() {
        mFullWidth = true;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }
}
