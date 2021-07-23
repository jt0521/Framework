package com.mobileframe.activity;

import android.view.View;

import androidx.core.content.ContextCompat;

import com.mobileframe.R;
import com.mobileframe.broadcast.NetBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/4/6 11:49
 * Description: 无网络展示
 * History:
 */
public class NoNetworkActivity extends BaseActivity {

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected boolean showTitleBarView() {
        return false;
    }

    @Override
    protected boolean needRegisterNetChange() {
        return true;
    }

    /**
     * 内容布局id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.layout_no_network;
    }

    /**
     * 初始化布局控件,在setContentView方法中调用,因此不需要手动调用initView()方法
     */
    @Override
    protected void initView() {
        mBaseLayoutLl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
        setContentBg(R.color.colorTransparent);
        setFinishOnTouchOutside(false);
        findViewById(R.id.ok_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 是否需要注册eventBus
     *
     * @return 默认不需要注册eventBus
     */
    protected boolean needRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noticeNet(NetBean bean) {
        finish();
    }

}
