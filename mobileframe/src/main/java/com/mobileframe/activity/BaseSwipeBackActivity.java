package com.mobileframe.activity;

import com.mobileframe.R;
import com.mobileframe.widegt.SwipeBackLayout;

/**
 * description: 右滑关闭activity基类,需要设置activity样式(style)透明才能达到效果
 * 可使用已有样式 style_theme_translucent_activity
 * author: tgl
 * date: 2017/5/27 14:08
 * update: 2017/5/27
 */

public abstract class BaseSwipeBackActivity extends BaseActivity {

    protected SwipeBackLayout mSwipeBackRootLl;

    @Override
    protected int getBaseLayoutId() {
        return R.layout.activity_base_swipe_back;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mSwipeBackRootLl = (SwipeBackLayout) findViewById(R.id.swipeBackRootLl);
        inflateLayout(R.layout.activity_base, mSwipeBackRootLl);
    }

}
