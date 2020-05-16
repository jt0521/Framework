package com.mobileframe.widegt;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileframe.R;

/**
 * 加载进度条
 */
public class LoadingProgressDialog extends Dialog {
    private TextView mTvTip;
    private String mTxt;
    private LoadingView mProgressBar;
    private ImageView mProgressBarPureGraph;
    /**
     * progress bar为纯图片，使用loadingView展示不出效果
     */
    private boolean mPureGraphMode;
    private int mLoadingBg;
    private float mDimAmount = 0.0f;

    public LoadingProgressDialog(Context context) {
        super(context, R.style.style_LoadingDialog);
    }

    public LoadingProgressDialog(Context context, float dimAmount) {
        super(context, R.style.style_LoadingDialog);
        setDimAmount(dimAmount);
    }

    public LoadingProgressDialog(Context context, int loadBg) {
        super(context, R.style.style_LoadingDialog);
        mLoadingBg = loadBg;
    }

    public void setText(String txt) {
        if (!TextUtils.isEmpty(txt)) {
            mTxt = txt;
        }
    }

    public void setDimAmount(float dimAmount) {
        if (dimAmount < 0 || dimAmount > 1) {
            return;
        }
        mDimAmount = dimAmount;
    }

    public void setText(int res) {
        if (res > 0) {
            mTxt = getContext().getString(res);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = mDimAmount;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(params);

        setContentView(R.layout.dialog_progress_loading);
        //使用MATCH_PARENT会出现状态栏顶部为黑色
        int screenH = getContext().getResources().getDisplayMetrics().heightPixels;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, screenH);
        mTvTip = (TextView) findViewById(R.id.progress_txt);
        mProgressBar = (LoadingView) findViewById(R.id.progress_bar);
        mProgressBarPureGraph = findViewById(R.id.progress_bar_pure_graph);
        if (mTvTip != null && !TextUtils.isEmpty(mTxt)) {
            mTvTip.setText(mTxt);
        }

        if (mProgressBar != null && mLoadingBg != 0) {
            mProgressBar.setBackgroundResource(mLoadingBg);
        }
    }

    /**
     * progress bar为纯图片，使用loadingView展示不出效果
     */
    public void setPureGraphMode() {
        mPureGraphMode = !mPureGraphMode;
    }

    private void showProgress() {
        if (mPureGraphMode) {
            mProgressBarPureGraph.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            if (mProgressBarPureGraph.getAnimation() == null) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anim_bar);
                LinearInterpolator lin = new LinearInterpolator();
                animation.setInterpolator(lin);
                mProgressBarPureGraph.setAnimation(animation);
            }
            mProgressBarPureGraph.getAnimation().startNow();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mProgressBarPureGraph.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
        showProgress();
    }

    @Override
    public void dismiss() {
        if (mProgressBarPureGraph.getVisibility() == View.VISIBLE && mProgressBarPureGraph.getAnimation() != null) {
            mProgressBarPureGraph.getAnimation().cancel();
        }
        super.dismiss();
    }
}
