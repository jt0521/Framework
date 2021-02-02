package com.mobileframe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.gyf.immersionbar.ImmersionBar;
import com.mobileframe.R;
import com.mobileframe.broadcast.NetBean;
import com.mobileframe.common.ActivityStackManager;
import com.mobileframe.common.BaseApplication;
import com.mobileframe.databinding.ActivityBaseBinding;
import com.mobileframe.widegt.LoadingProgressDialog;
import com.mobileframe.widegt.TitleBarView;
import com.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * author: tgl
 * activity基类
 * <p>
 * 载入布局前调用{@link #setContentViewPre()}
 * 重载基础布局{@link #getBaseLayoutId()}
 * 显示标题栏{@link #showTitleBarView()}
 * 载入子布局{@link #getLayoutId()}
 * 初始化布局，已在父类调用{@link #initView()}
 * 是否全屏，默认不{@link #supportFullScreen()}
 * 设置屏幕方向，默认竖屏{@link #setScreenOrientation()}
 * 是否使用沉浸式状态栏，默认使用{@link #isImmersionBarEnabled()}
 * 设置状态栏背景{@link #getStatusBarColorResId()}
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {

    protected BaseActivity mContext;
    protected TitleBarView mBarView;
    protected boolean mIsDestroy = true;
    protected LinearLayout mBaseLayoutLl;//基础容器
    protected FrameLayout mContentFl;//内容容器
    protected LoadingProgressDialog mProgressDialog;
    //数据加载失败时展示的view
    private View mFailedLoadView;
    private BroadcastReceiver mNetReceiver;
    /**
     * 是否已注册监听
     */
    private boolean mNetChangeReceiverFlag = false;
    private boolean mNetNotConnect;
    private boolean mIsShowNotNet;
    public T mViewBinding;
    ActivityBaseBinding mBaseBinding;

    /**
     * 是否设置为全屏,默认不设置
     *
     * @return
     */
    protected boolean supportFullScreen() {
        return false;
    }

    /**
     * 设置屏幕固定方向，默认竖屏
     */
    protected void setScreenOrientation() {
        try {
            // 8.0设置activity透明时，设置固定屏幕方向会抛异常
            //IllegalStateException:Only fullscreen activities can request orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {

        }
    }

    /**
     * 是否可以使用沉浸式
     *
     * @return 默认使用
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 设置状态栏背景色资源id
     *
     * @return
     */
    protected @ColorRes
    int getStatusBarColorResId() {
        return R.color.c_black;
    }

    /**
     * setContentView()前调用
     */
    protected void setContentViewPre() {
    }

    /**
     * 灵活配置基础布局
     *
     * @return
     */
    protected @LayoutRes
    int getBaseLayoutId() {
        return R.layout.activity_base;
    }

    /**
     * 默认有标题栏，若不需要请重写此方法，并返回false
     *
     * @return
     */
    protected boolean showTitleBarView() {
        return true;
    }

    /**
     * 由抽象方法改为非抽象，保留是为了兼容v10.1.9及之前的版本
     *
     * @return
     */
    protected @LayoutRes
    int getLayoutId() {
        return 0;
    }

    /**
     * 初始化布局控件,在setContentView方法中调用,因此不需要手动调用initView()方法
     */
    protected abstract void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStackManager.getInstance().addActivity(this);
        mContext = this;
        mIsDestroy = false;
        if (supportFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setScreenOrientation();
        setContentViewPre();

        mBaseBinding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(mBaseBinding.getRoot());

        mBaseLayoutLl = mBaseBinding.baseLayoutLl;
        mContentFl = mBaseBinding.contentFl;

        loadChildBinding();
        if (!supportFullScreen()) {
            setStatusBarStyle();
            ViewStub viewStub = mBaseBinding.viewStub;
            if (showTitleBarView() && viewStub != null) {
                mBarView = new TitleBarView(this, viewStub.inflate());
            }
        }
        if (needRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
        initView();
    }

    /**
     * 载入子布局
     */
    private void loadChildBinding() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<T> viewBindClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
            if (viewBindClass == null) {
                return;
            }
            try {
                Method method = viewBindClass.getDeclaredMethod("inflate", LayoutInflater.class);
                mViewBinding = (T) method.invoke(null, getLayoutInflater());
                mContentFl.addView(mViewBinding.getRoot());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置状态栏背景
     */
    protected void setStatusBarStyle() {
        if (!isImmersionBarEnabled()) {
            return;
        }
        ImmersionBar mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarView(R.id.fitSystemView)
                .barColorInt(ContextCompat.getColor(this, getStatusBarColorResId()))
                .navigationBarColor(R.color.c_black)
                .init();
    }

    /**
     * 载入二级布局
     *
     * @param layoutId
     */
    protected View inflateLayout(@LayoutRes int layoutId, ViewGroup parent) {
        if (layoutId == 0) {
            return null;
        }
        return getLayoutInflater().inflate(layoutId, parent);
    }

    /**
     * 获取titleBar
     *
     * @return
     */
    public TitleBarView getBarView() {
        return mBarView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetReceiver();
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        super.onDestroy();
        unregisterNetReceiver();
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (needRegisterEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        // cancelAllRequest();
        // 结束Activity从堆栈中移除
        ActivityStackManager.getInstance().finishAssignActivity(this);
        BaseApplication.addRefWatcher(this);
    }

    /**
     * 清除Activity被异常回收时缓存的fragment，解决getActivity（）返回null
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // if (savedInstanceState != null) {
        //   String FRAGMENTS_TAG = "androidx.fragment.app:fragments";
        // savedInstanceState.remove(FRAGMENTS_TAG);
        // }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void showProgressDialog() {
        showProgressDialog(getString(R.string.str_loading));
    }

    public void showProgressDialog(boolean cancelable) {
        showProgressDialog(getString(R.string.str_loading), cancelable);
    }

    /**
     * 显示加载框
     *
     * @param msg
     */
    public void showProgressDialog(String msg) {
        if (mIsDestroy) {
            return;
        }
        showProgressDialog(msg, true);
    }

    /**
     * @param msg
     * @param cancelable 是否可取消
     */
    public void showProgressDialog(String msg, boolean cancelable) {
        if (mIsDestroy) {
            return;
        }
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingProgressDialog(mContext);
            mProgressDialog.setText(msg);
            mProgressDialog.setCanceledOnTouchOutside(false);
            if (progressPureGraphMode()) {
                mProgressDialog.setPureGraphMode();
            }
        } else {
            mProgressDialog.setText(msg);
        }
        mProgressDialog.setCancelable(cancelable);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (mIsDestroy) {
            return;
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 显示数据加载失败view
     */
    public void showFailedLoadView(View.OnClickListener listener) {
        if (mFailedLoadView == null) {
            mFailedLoadView = showFailedLoadView(R.layout.include_failed_to_load, listener);
        } else {
            mContentFl.addView(mFailedLoadView);
        }
        mFailedLoadView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示数据加载失败view
     *
     * @param failedLayoutId
     */
    protected View showFailedLoadView(@LayoutRes int failedLayoutId,
                                      View.OnClickListener listener) {
        mFailedLoadView = getLayoutInflater().inflate(failedLayoutId, null);
        mContentFl.addView(mFailedLoadView);
        mFailedLoadView.setOnClickListener(listener);
        return mFailedLoadView;
    }

    /**
     * 隐藏网络失败view
     */
    public void hideFailedView() {
        if (mFailedLoadView != null) {
            mFailedLoadView.setVisibility(View.GONE);
            mContentFl.removeView(mFailedLoadView);
        }
    }

    /**
     * 在使用第三方库时，引用Activity时使用弱引用，避免第三方库造成内测泄露
     *
     * @return
     */
    public BaseActivity getReferenceContext() {
        Reference<BaseActivity> reference = new WeakReference(this);
        return reference.get();
    }

    /**
     * 重载方法，是否使用切换动画
     *
     * @param intent
     * @param isOverridePending
     */
    public void startActivity(Intent intent, boolean isOverridePending) {
        intent.putExtra("isOverridePending", isOverridePending);
        super.startActivity(intent);
        if (isOverridePending) {
//            overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean isOverridePending) {
        intent.putExtra("isOverridePending", isOverridePending);
        super.startActivityForResult(intent, requestCode);
        if (isOverridePending) {
//            overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent() != null && getIntent().getBooleanExtra("isOverridePending", false)) {
//            overridePendingTransition(0, R.anim.base_slide_right_out);
        }
    }

    @Override
    public boolean isDestroyed() {
        return mIsDestroy;
    }

//    以下代码为自定义管理网络请求id，现网络请求与activity同生命周期，因此不需此处添加管理
//    /**
//     * 添加网络请求唯一标识
//     *
//     * @param tag
//     */
//    public void addRequestTag(String tag) {
//        if (mRequestTag == null) {
//            mRequestTag = new ArrayList<>();
//        }
//        if (!mRequestTag.contains(tag)) {
//            mRequestTag.add(tag);
//        }
//    }
//
//    /**
//     * 删除已完成事件标识
//     */
//    public void removeRequestTag(String tag) {
//        if (mRequestTag != null) {
//            mRequestTag.remove(tag);
//        }
//    }
//
//    /**
//     * 取消所有网络请求
//     */
//    public void cancelAllRequest() {
//        if (mRequestTag != null) {
//            for (int i = mRequestTag.size() - 1; i >= 0; i--) {
//                String tag = mRequestTag.get(i);
//                HttpManager.cancelTag(tag);
//                removeRequestTag(tag);
//            }
//        }
//    }

    public void setContentBg(@ColorRes int color) {
        mContentFl.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    @Override
    public void setTitle(int titleId) {
        if (mBarView != null) {
            mBarView.setTitle(getString(titleId));
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mBarView != null) {
            mBarView.setTitle(title);
        }
    }


    /**
     * 注册网络变化监听器
     */
    private void registerNetReceiver() {
        if (!mNetChangeReceiverFlag && needRegisterNetChange()) {
            mNetChangeReceiverFlag = true;
            createBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetReceiver, filter);
        }
    }

    /**
     * 释放网络变化监听器
     */
    private void unregisterNetReceiver() {
        if (mNetChangeReceiverFlag && needRegisterNetChange()) {
            if (mNetReceiver != null) {
                unregisterReceiver(mNetReceiver);
            }
            mNetChangeReceiverFlag = false;
        }
    }

    private void createBroadcastReceiver() {
        if (mNetReceiver != null) {
            return;
        }
        mNetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                            Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                    if (info != null && info.isAvailable()) {
                        if (mNetNotConnect) {
                            mNetNotConnect = false;
                            mIsShowNotNet = false;
                            EventBus.getDefault().post(new NetBean());
                            int type = info.getType();
                            if (type == ConnectivityManager.TYPE_WIFI) {
                                ToastUtils.showToastCenter(context, "已连接到Wi-Fi");
                            } else {
                                ToastUtils.showToastCenter(context, "已连接到移动数据网络");
                            }
                        }
                    } else {
                        mNetNotConnect = true;
                        if (!mIsShowNotNet && !ActivityStackManager.getInstance().hasActivity(NoNetworkActivity.class)) {
                            mIsShowNotNet = true;
                            Intent intentActivity = new Intent(context, NoNetworkActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentActivity);
                        }
                    }
                } catch (Exception e) {

                }
            }
        };
    }

    /**
     * 是否注册网络监听变化
     *
     * @return
     */
    protected boolean needRegisterNetChange() {
        return true;
    }

    /**
     * 是否需要注册eventBus
     *
     * @return 默认不需要注册eventBus
     */
    protected boolean needRegisterEventBus() {
        return false;
    }

    /**
     * 网络请求框模式，默认
     * 返回true会切换成另一种模式纯图模式
     *
     * @return
     */
    protected boolean progressPureGraphMode() {
        return false;
    }
}
