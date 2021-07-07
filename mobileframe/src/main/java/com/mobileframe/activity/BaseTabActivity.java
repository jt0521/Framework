package com.mobileframe.activity;
/*
 * 功能描述：
 *
 *
 * 作者：Created by tgl on 2018/7/27.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;

import com.mobileframe.R;
import com.mobileframe.tools.ResourceUtils;

public abstract class BaseTabActivity extends BaseActivity implements TabHost.OnTabChangeListener {

    protected FragmentTabHost mFragmentTabHost = null;
    protected int mLastTabIndex = -1;
    private TextView[] mTextViews;


    protected abstract Class<? extends Fragment>[] tabFragmentArray();

    protected Bundle[] tabFragmentBundleArray() {
        return null;
    }

    protected abstract int[] tabItemImgSelectId();

    protected abstract int[] tabItemImgUnSelectId();


    protected abstract String[] tabItemStr();

    /**
     * 默认字体颜色
     *
     * @return
     */
    protected abstract @ColorInt
    int textColorId();

    /**
     * 选中字体颜色
     *
     * @return
     */
    protected abstract @ColorInt
    int textColorIdSelected();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_tab;
    }

    @Override
    protected View inflateLayout(int layoutId, ViewGroup parent) {
        View view = getLayoutInflater().inflate(layoutId, null);
        mBaseLayoutLl.addView(view);
        return view;
    }

    @Override
    protected void initView() {
        //实例化TabHost对象，得到TabHost
        mFragmentTabHost = findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.contentFl);
        mFragmentTabHost.setOnTabChangedListener(this);
        mFragmentTabHost.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        //去掉分割线
        mFragmentTabHost.getTabWidget().setDividerDrawable(android.R.color.transparent);
        int length = 0;
        if (tabItemImgSelectId() != null && tabItemImgSelectId().length > 0) {
            length = tabItemImgSelectId().length;
        }
        if (tabItemImgUnSelectId() != null && tabItemImgUnSelectId().length > 0) {
            length = tabItemImgUnSelectId().length;
        }
        if (tabItemStr() != null && tabItemStr().length > 0) {
            length = tabItemStr().length;
        }
        mTextViews = new TextView[length];
        for (int i = 0; i < length; i++) {
            TabHost.TabSpec tabSpec = mFragmentTabHost.newTabSpec(String.valueOf(i)).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mFragmentTabHost.addTab(tabSpec, tabFragmentArray()[i], tabFragmentBundleArray() == null ? null : tabFragmentBundleArray()[i]);
            //设置item背景颜色
            mFragmentTabHost.getTabWidget().getChildAt(i).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorTransparent));
        }

    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View itemView = getLayoutInflater().inflate(R.layout.inculde_buttom_tab_layout, null);
        ImageView imageView = itemView.findViewById(R.id.imgTab);
        if (tabItemImgUnSelectId() == null && tabItemImgSelectId() == null) {
            imageView.setVisibility(View.GONE);
        } else if (tabItemImgUnSelectId() != null && tabItemImgSelectId() != null) {
            imageView.setImageDrawable(ResourceUtils.getSelector(this,
                    tabItemImgUnSelectId()[index], tabItemImgSelectId()[index]));
        } else if (tabItemImgUnSelectId() != null) {
            imageView.setBackgroundResource(tabItemImgUnSelectId()[index]);
        } else if (tabItemImgSelectId() != null) {
            imageView.setBackgroundResource(tabItemImgSelectId()[index]);
        }
        TextView textView = itemView.findViewById(R.id.textTab);
        if (tabItemStr() != null) {
            textView.setText(tabItemStr()[index]);
        } else {
            textView.setVisibility(View.GONE);
        }
        textView.setTextColor(textColorId());
        mTextViews[index] = textView;
        return itemView;
    }

    @Override
    public void onTabChanged(String tabId) {
        int curIndex = Integer.parseInt(tabId);
        if (mLastTabIndex != curIndex) {
            if (textColorIdSelected() != 0) {
                if (mLastTabIndex != -1) {
                    mTextViews[mLastTabIndex].setTextColor(textColorId());
                }
                mTextViews[curIndex].setTextColor(textColorIdSelected());
            }
            mLastTabIndex = curIndex;
        }
        onTabChange(curIndex);
    }

    protected abstract void onTabChange(int tabIndex);

    @Override
    protected void onDestroy() {
        mFragmentTabHost.removeAllViews();
        mFragmentTabHost = null;
        super.onDestroy();
    }

}
