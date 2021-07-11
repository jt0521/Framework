package com.mobileframe.common;
/**
 * 基础application
 */

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.multidex.MultiDex;

import com.mobileframe.activity.BaseActivity;
import com.mobileframe.fragments.BaseFragment;
import com.mobileframe.platform.StethoUtils;
import com.mobileframe.tools.AppUtils;
import com.mobileframe.tools.CrashExceptionHandler;
import com.mobileframe.tools.PkgUtil;
import com.net.netretrofit.HttpConfigure;
import com.net.netretrofit.listener.UiHandler;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import okhttp3.OkHttpClient;

public abstract class BaseApplication extends Application implements UiHandler {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    private static RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        //防止被services启动，多次初始化
        if (!AppUtils.isApplicationProcess(this)) {
            return;
        }
        if (Config.isDebugEniv) {
            // 反射方式注册有缺陷
            // ThirdSdkManagement.initLeakCanary(this);
            mRefWatcher = initLeakCanary(this);
            StethoUtils.init(this);
            StethoUtils.configureInterceptor(new OkHttpClient.Builder().build());
            CrashExceptionHandler.getInstance().init(this);
        }
        initHttp();
        init();
    }

    /**
     * application onCreate中调用，避免多进程多次初始化
     */
    public abstract void init();

    /**
     * 注册LeakCanary
     *
     * @param application
     * @return
     */
    private RefWatcher initLeakCanary(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(application);
    }

    /**
     * 只做初始化只能检测activity；需要检测其他类需在onDestroy里RefWatcher.watch（this）,如fragment
     *
     * @param object
     * @return
     */
    public static void addRefWatcher(Object object) {
        if (getRefWatcher() != null) {
            //暂时不检查fragment 注释
            getRefWatcher().watch(object);
        }
    }

    public static RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    /**
     * 默认初始化网络
     */
    public void initHttp() {
        HttpConfigure.setServiceHost(getHttpHost());
        HttpConfigure.init(this, PkgUtil.getAppVersionName(this), "custom", "app");
        HttpConfigure.setUiHandler(this);
    }

    /**
     * 网络主机地址
     *
     * @return
     */
    protected abstract String getHttpHost();

    @Override
    public void showFailedView(Object mComeFrom, View.OnClickListener listener) {
        showFailedView(true, mComeFrom, listener);
    }

    @Override
    public void closeFailedView(Object mComeFrom) {
        showFailedView(false, mComeFrom, null);
    }

    @Override
    public void showProgressView(Object mComeFrom, boolean cancelable) {
        showProgress(true, mComeFrom, cancelable);
    }

    @Override
    public void closeProgressView(Object mComeFrom) {
        showProgress(false, mComeFrom, false);
    }

    /**
     * 显示或者隐藏失败view
     */
    private void showFailedView(boolean isShow, Object mComeFrom, View.OnClickListener listener) {
        if (mComeFrom == null) {
            return;
        }
        if (mComeFrom instanceof BaseActivity && (!((BaseActivity) mComeFrom).isDestroyed())) {
            if (isShow) {
                ((BaseActivity) mComeFrom).showFailedLoadView(listener);
            } else {
                ((BaseActivity) mComeFrom).hideFailedView();
            }
        } else if (mComeFrom instanceof BaseFragment) {
            BaseFragment fragment = (BaseFragment) mComeFrom;
            if (fragment.isAdded()) {
                if (isShow) {
                    fragment.showFailedLoadView(listener);
                } else {
                    fragment.hideFailedView();
                }
            }
        }
    }

    /**
     * 显示waitview
     *
     * @param isShow
     * @param mComeFrom
     * @param cancelable
     */
    private void showProgress(boolean isShow, Object mComeFrom, boolean cancelable) {
        if (mComeFrom == null) {
            return;
        }
        if (mComeFrom instanceof BaseActivity && (!((BaseActivity) mComeFrom).isDestroyed())) {
            if (isShow) {
                ((BaseActivity) mComeFrom).showProgressDialog(cancelable);
            } else {
                ((BaseActivity) mComeFrom).dismissProgressDialog();
            }
        } else if (mComeFrom instanceof BaseFragment) {
            BaseFragment fragment = (BaseFragment) mComeFrom;
            if (fragment.isAdded()) {
                if (isShow) {
                    fragment.showProgressDialog();
                } else {
                    fragment.dismissProgressDialog();
                }
            }
        }
    }

    public Context getContextFrom(Object mComeFrom) {
        if (mComeFrom == null) {
            return null;
        }
        if (mComeFrom instanceof BaseActivity) {
            return ((BaseActivity) mComeFrom);
        }
        if (mComeFrom instanceof BaseFragment) {
            return ((BaseFragment) mComeFrom).getActivity();
        }
        return null;
    }

}
