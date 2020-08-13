package com.mobileframe.widegt;

import android.app.Activity;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import android.text.Spanned;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobileframe.R;

/**
 * Created by Administrator on 2016/6/20.
 * 自定义ActionBar
 */
public class TitleBarView {

    private RelativeLayout mBarViewRl;
    private TextView mLeftTv;
    private TextView mSubjectTv;
    private Activity mContext;
    private TextView mRightTv;

    public TitleBarView(Activity context, View barView) {
        this(context, barView, "");
    }

    /**
     * @param context
     * @param barView
     * @param title   标题
     */
    public TitleBarView(Activity context, View barView, String title) {
        this(context, barView, title, false);
    }

    /**
     * @param context
     * @param barView
     * @param title       标题
     * @param isShowRight 是否显示右侧view
     */
    public TitleBarView(Activity context, View barView, String title,
                        boolean isShowRight) {
        this(context, barView, title, isShowRight, true);
    }

    /**
     * @param context
     * @param barView
     * @param title             标题
     * @param isShowRight       是否显示右侧view
     * @param isSetLeftListener 是否设置左侧view监听事件，默认设置
     */
    public TitleBarView(Activity context, View barView, String title,
                        boolean isShowRight, boolean isSetLeftListener) {
        mContext = context;
        mBarViewRl = (RelativeLayout) barView;
        initView(barView, title, isShowRight, isSetLeftListener);
    }

    /**
     * @param view              actionbar布局
     * @param title
     * @param isShowRight       是否显示右边按钮
     * @param isSetLeftListener 是否设置左侧view监听事件，默认设置
     */
    private void initView(View view, String title, boolean isShowRight, boolean isSetLeftListener) {
        mLeftTv = view.findViewById(R.id.leftTv_actionABar);
        mSubjectTv = view.findViewById(R.id.subjectTv_actionBar);
        mRightTv = view.findViewById(R.id.rightTv_actionABar);
        if (isSetLeftListener) {
            mLeftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.onBackPressed();
                }
            });
        }
        mSubjectTv.setText(title);
        if (isShowRight) {
            mRightTv.setVisibility(View.VISIBLE);
        } else {
            mRightTv.setVisibility(View.GONE);
        }
    }


    public void setLeftTv(String leftText) {
        mLeftTv.setText(leftText);
    }

    /**
     * @param leftText 文本
     * @param listener 监听事件
     */
    public void setLeftTv(String leftText, View.OnClickListener listener) {
        setLeftTv(leftText, -1, listener);
    }

    /**
     * @param leftText 文本
     * @param imgId    图片resId
     * @param listener 监听事件
     */
    public void setLeftTv(String leftText, @DrawableRes int imgId, View.OnClickListener listener) {
        mLeftTv.setText(leftText);
        if (imgId == -1) {
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(imgId, 0, 0, 0);
        }
        mLeftTv.setOnClickListener(listener);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        mSubjectTv.setText(title);
    }

    public void setTitle(String title, @ColorRes int colorInt) {
        mSubjectTv.setText(title);
        mSubjectTv.setTextColor(ContextCompat.getColor(mContext, colorInt));
    }

    public void setTitle(Spanned spanned) {
        mSubjectTv.setText(spanned);
    }

    /**
     * 显示标题
     */
    public void showTitle() {
        mSubjectTv.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题
     */
    public void hideTitle() {
        mSubjectTv.setVisibility(View.GONE);
    }

    public void setRightTv(String rightText, View.OnClickListener listener) {
        setRightTv(rightText, -1, listener);
    }

    /**
     * @param rightText 文本
     * @param imgId     图片res id
     * @param listener  监听事件
     */
    public void setRightTv(String rightText, @DrawableRes int imgId, View.OnClickListener listener) {
        mRightTv.setText(rightText);
        if (imgId == -1) {
            mRightTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            mRightTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgId, 0);
        }
        mRightTv.setOnClickListener(listener);
    }

    /**
     * 返回barView
     *
     * @return
     */
    public RelativeLayout getBarView() {
        return mBarViewRl;
    }

    /**
     * 返回左边默认按钮
     *
     * @return
     */
    public TextView getLeftView() {
        return mLeftTv;
    }

    /**
     * 返回title默认按钮
     *
     * @return
     */
    public TextView getTitleView() {
        return mSubjectTv;
    }

    /**
     * 返回右边默认按钮
     *
     * @return
     */
    public TextView getRightView() {
        return mRightTv;
    }

    /**
     * 设置actionBar背景
     *
     * @param resourceId
     */
    public void setBackgroundResource(int resourceId) {
        mBarViewRl.setBackgroundResource(resourceId);
    }

    public void setBackgroundColor(int colorId) {
        mBarViewRl.setBackgroundColor(ContextCompat.getColor(mContext, colorId));
    }


}
