package com.mobileframe.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ViewStubCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mobileframe.R;
import com.mobileframe.activity.BaseActivity;
import com.mobileframe.common.BaseApplication;
import com.mobileframe.tools.DensityUtil;
import com.mobileframe.tools.ToastUtils;
import com.mobileframe.widegt.TitleBarView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/8/22.
 * 基础fragment，内置toast和progressDiloag
 */
public abstract class BaseFragment extends Fragment {

    protected View mView;
    protected boolean mIsVisible;
    /**
     * 返回true时 mTitleBarView有值{@link #showTitleBarView()}
     * mTitleBarView：titleBar
     */
    protected LinearLayout mFragmentBaseLl;
    protected TitleBarView mTitleBarView;
    private Unbinder mUnBinder;
    private List<String> mRequestTag;

    /**
     * 获取布局文件
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 默认无标题，要标题请重写此方法并返回true
     *
     * @return
     */
    protected boolean showTitleBarView() {
        return false;
    }

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * onCreateView 方法执行,载入布局执行完后
     */
    protected void createdView() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }

    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
    }

    protected void onInvisible() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_base, container, false);
            if (showTitleBarView() && mTitleBarView == null) {
                ViewStub viewStub = mView.findViewById(R.id.viewStub_titleBar);
                mTitleBarView = new TitleBarView(getActivity(), viewStub.inflate());
            }
            inflaterLayout(inflater, getLayoutId());
            mUnBinder = ButterKnife.bind(this, mView);
            init();
        } else {
            ViewGroup viewGroup = (ViewGroup) mView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mView);
            }
            mUnBinder = ButterKnife.bind(this, mView);
        }
        createdView();
        return mView;
    }

    /**
     * 设入布局
     *
     * @param layoutId
     */
    private void inflaterLayout(LayoutInflater inflater, @LayoutRes int layoutId) {
        if (mFragmentBaseLl == null) {
            mFragmentBaseLl = (LinearLayout) findViewById(R.id.fragment_baseLayout);
        }
        inflater.inflate(layoutId, mFragmentBaseLl);
    }

    /**
     * findViewBy
     *
     * @param viewId
     * @return
     */
    protected View findViewById(int viewId) {
        if (mView == null) {
            throw new RuntimeException("Must be after onCreateView");
        }
        return mView.findViewById(viewId);
    }

    /**
     * 返回actionBar
     *
     * @return
     */
    public TitleBarView getTitleBarView() {
        return mTitleBarView;
    }

    /**
     * 设置view高度，以空出statusBar
     */
    @SuppressLint("RestrictedApi")
    protected void setFitsSystemWindowsViewHeight() {
        ViewStubCompat viewStubCompat =
                (ViewStubCompat) findViewById(R.id.viewStub_fitSystemWindowView);
        setFitsSystemWindowsViewHeight(viewStubCompat.inflate());
    }

    /**
     * 设置view高度，以空出statusBar
     *
     * @param finSystemWindowView
     */
    protected void setFitsSystemWindowsViewHeight(View finSystemWindowView) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams topParams = finSystemWindowView
                    .getLayoutParams();
            topParams.height = DensityUtil.getStatusBarH(finSystemWindowView.getContext());
            finSystemWindowView.setLayoutParams(topParams);
        }
    }

    public BaseActivity getBaseActivity() {
        if (getActivity() != null) {
            if (getActivity() instanceof BaseActivity) {
                return (BaseActivity) getActivity();
            }
        }
        return null;
    }

    public void showProgressDialog() {
        getBaseActivity().showProgressDialog(getResources().getString(R.string.str_loading));
    }

    public void showProgressDialog(String message) {
        getBaseActivity().showProgressDialog(message);
    }

    public void dismissProgressDialog() {
        if (getBaseActivity() != null) {
            getBaseActivity().dismissProgressDialog();
        }
    }

    /**
     * 在使用第三方库时，引用Activity时使用弱引用，避免第三方库造成内测泄露
     *
     * @return
     */
    public FragmentActivity getReferenceContext() {
        return getBaseActivity().getReferenceContext();
    }

    public void startActivity(Intent intent, boolean isOverridePending) {
        if (getBaseActivity() != null) {
            getBaseActivity().startActivity(intent, isOverridePending);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean isOverridePending) {
        if (getBaseActivity() != null)
            getBaseActivity().startActivityForResult(intent, requestCode, isOverridePending);
    }


    /**
     * 显示数据加载失败view
     */
    public void showFailedLoadView(View.OnClickListener listener) {
        if (getBaseActivity() != null) {
            getBaseActivity().showFailedLoadView(listener);
        }
    }

    /**
     * 隐藏网络失败view
     */
    public void hideFailedView() {
        if (getBaseActivity() != null) {
            getBaseActivity().hideFailedView();
        }
    }

//    已改为依赖activity生命周期，不需手动添加管理
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.needRegisterEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        //  cancelAllRequest();
        BaseApplication.addRefWatcher(this);
    }

    protected boolean needRegisterEventBus() {
        return false;
    }

    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtils.showToast(getActivity(), msg);
    }
}
