package com.mobileframe.widegt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.ColorInt;

import com.mobileframe.R;
import com.mobileframe.tools.ViewTool;

import static android.view.ViewGroup.LayoutParams.*;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/2/16 0:47
 * Description:滚动通知
 * History:
 */
public class MarqueeTextView extends LinearLayout {

    private Context mContext;
    private ViewFlipper viewFlipper;
    private View marqueeTextView;
    private String[] textArrays;
    private MarqueeTvClickListener mMarqueeTvClickListener;
    private int mTextSizePx;
    private int mTextColor;

    public MarqueeTextView(Context context) {
        super(context);
        mContext = context;
        initBasicView();
    }


    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBasicView();
    }

    public void setTextSizePx(int mTextSizePx) {
        this.mTextSizePx = mTextSizePx;
    }

    public void setTextColor(@ColorInt int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setTextArraysAndClickListener(String[] textArrays, MarqueeTvClickListener marqueeTextViewClickListener) {
        //1.设置数据源；2.设置监听回调（将textView点击事件传递到目标界面进行操作）
        this.textArrays = textArrays;
        mMarqueeTvClickListener = marqueeTextViewClickListener;
        initMarqueeTextView(textArrays, marqueeTextViewClickListener);
    }

    public void initBasicView() {//加载布局，初始化ViewFlipper组件及效果
        marqueeTextView = LayoutInflater.from(mContext).inflate(R.layout.marquee_tv_layout, null);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(marqueeTextView, layoutParams);
        viewFlipper = (ViewFlipper) marqueeTextView.findViewById(R.id.viewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom_marqee));
        //设置上下的动画效果（自定义动画，所以改左右也很简单）
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_top_marqee));
        viewFlipper.startFlipping();
    }

    public void initMarqueeTextView(String[] textArrays, MarqueeTvClickListener marqueeTextViewClickListener) {
        if (textArrays.length == 0) {
            return;
        }

        int i = 0;
        viewFlipper.removeAllViews();
        while (i < textArrays.length) {
            TextView textView = new TextView(mContext);
            textView.setText(textArrays[i]);
            if (mTextColor > 0) {
                textView.setTextColor(mTextColor);
            }
            if (mTextSizePx > 0) {
                ViewTool.setTextSize(textView, mTextSizePx);
            }
            textView.setSingleLine();
            textView.setOnClickListener(marqueeTextViewClickListener);
            LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            viewFlipper.addView(textView, lp);
            i++;
        }
    }

    public void releaseResources() {
        if (marqueeTextView != null) {
            if (viewFlipper != null) {
                viewFlipper.stopFlipping();
                viewFlipper.removeAllViews();
                viewFlipper = null;
            }
            marqueeTextView = null;
        }
    }

    public interface MarqueeTvClickListener extends View.OnClickListener {
        void onClick(View view);
    }
}