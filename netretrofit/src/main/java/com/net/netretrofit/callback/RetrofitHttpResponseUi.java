package com.net.netretrofit.callback;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.fragment.app.Fragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.net.netretrofit.R;
import com.net.netretrofit.RequestManager;
import com.net.netretrofit.HttpConfigure;
import com.net.netretrofit.listener.RequestListener;
import com.net.netretrofit.listener.UiHandler;
import com.toast.ToastUtils;

import retrofit2.Call;

/**
 * @author tgl
 * 处理显示加载进度框，取消对话框，弹出toast，登录失败，页面跳转等
 */
public class RetrofitHttpResponseUi implements BaseHttpResponseUi {

    /**
     * 当前请求自什么页面，是谁调用
     * 如果mComeFrom是Activity，或者Fragment，这是将会有对话框显示，失败占位view有关
     */
    private Object mComeFrom;
    /**
     * 是否已经显示加载进度框
     */
    private boolean mBeShownProgress = false;

    /**
     *
     */
    private int mResponseUiFlag = FLAG_NONE;

    private Call mCall;
    private RequestListener mListener;
    private View.OnClickListener mOnFailedViewClickListener;

    /**
     * @param flag
     */
    @Override
    public void setHttpResponseUi(int flag) {
        mResponseUiFlag = flag;
    }

    public RetrofitHttpResponseUi(Object comeFrom, Call call, RequestListener listener) {
        mComeFrom = comeFrom;
        mCall = call;
        mListener = listener;
    }

    @Override
    public void onPreStart() {
        //显示加载进度条
        boolean showProgressDialog = (mResponseUiFlag & FLAG_SHOW_PROGRESS_DIALOG) != 0;
        boolean cancelable = (mResponseUiFlag & FLAG_PROGRESS_DIALOG_NONCANCELABLE) == 0;
        mBeShownProgress = showProgressDialog;

        if (mComeFrom != null && mBeShownProgress) {
            UiHandler listener = HttpConfigure.getUiHandler();
            if (listener != null) {
                listener.showProgressView(mComeFrom, cancelable);
            }
//            else {
//                if (mComeFrom instanceof BaseActivity) {
//                if (showProgressDialog) {
//                    ((BaseActivity) mComeFrom).showProgressDialog(cancelable);
//                } else {
//                    ((BaseActivity) mComeFrom).showProgress(cancelable);
//                }
//            } else if (mComeFrom instanceof BaseFragment) {
//                if (showProgressDialog) {
//                    ((BaseFragment) mComeFrom).showProgressDialog(cancelable);
//                } else {
//                    ((BaseFragment) mComeFrom).showProgress(cancelable);
//                }
//            }
//            }
        }
    }

    @Override
    public void onStart() {
        //do nothing
    }

    /**
     * @param code 业务逻辑响应码
     */
    @Override
    public void onSuccess(int code) {
        if ((mResponseUiFlag & FLAG_PROGRESS_MANUAL_CLOSE) == 0) {
            if (mComeFrom != null && mBeShownProgress) {
                closeLoadingProgress();
            } else {
                //此次请求没有显示，则默认不做关闭
            }
        } else {
            //加载进度框关闭改为手动
        }
        UiHandler listener = HttpConfigure.getUiHandler();
        if (listener != null) {
            listener.interceptedSuccessfully(mComeFrom, code);
            boolean canShowFailureView = (mResponseUiFlag & FLAG_SHOW_LOAD_FAIL_VIEW) != 0;
            if (canShowFailureView) {
                listener.closeFailedView(mComeFrom);
            }
        }
    }

    /**
     * @param errorCode
     * @param msg
     * @param httpError 如：errorCode:401访问拒绝  errorCode:500服务器内部错误
     */
    @Override
    public void onFailure(int errorCode, String msg, boolean httpError, boolean showToast) {
        closeLoadingProgress();

        if (httpError && mComeFrom != null) {
            //http错误
            boolean canShowFailureView = (mResponseUiFlag & FLAG_SHOW_LOAD_FAIL_VIEW) != 0;
            //当显示错误页面是，toast就不用再显示
            UiHandler listener = HttpConfigure.getUiHandler();
            if (listener != null) {
                if (canShowFailureView) {
                    initOnClickLister();
                    listener.showFailedView(mComeFrom, mOnFailedViewClickListener);
                } else if (showToast) {
                    showToast(msg);
                }
            }
        } else {
            UiHandler listener = HttpConfigure.getUiHandler();
            if (listener == null || !listener.failedShowToast(mComeFrom, errorCode, msg)) {
                if (showToast) {
                    showToast(msg);
                }
            }
            if (listener != null) {
                listener.closeFailedView(mComeFrom);
            }
        }
    }

    @Override
    public void onFailureDoThing(int errorCode, String msg) {
        closeLoadingProgress();
    }

    @Override
    public void onCancelled() {
        closeLoadingProgress();
    }

    /**
     * 请求失败点击重试
     */
    private void initOnClickLister() {
        if (mOnFailedViewClickListener == null) {
            mOnFailedViewClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNetWorkAvailable(mComeFrom)) {
                        showToast("网络连接异常，请检查网络");
                        return;
                    }
                    Context context = null;
                    if (mComeFrom instanceof Context) {
                        context = (Context) mComeFrom;
                    } else if (mComeFrom instanceof Fragment) {
                        context = ((Fragment) mComeFrom).getContext();
                    }

                    if (context != null) {
                        if (mCall != null) {
                            onPreStart();
                            RequestManager.getInstance().enqueueRequest(context, mResponseUiFlag,
                                    mCall.clone(), mListener);
                        } else {
                            closeLoadingProgress();
                        }
                    } else {
                        closeLoadingProgress();
                    }
                }
            };
        }
    }

    public void closeLoadingProgress() {
//        if (mComeFrom instanceof BaseActivity) {
//            if (showProgressDialog) {
//                ((BaseActivity) mComeFrom).closeProgressDialog();
//            } else {
//                ((BaseActivity) mComeFrom).closeProgress();
//            }
//        } else if (mComeFrom instanceof BaseFragment) {
//            if (showProgressDialog) {
//                ((BaseFragment) mComeFrom).closeProgressDialog();
//            } else {
//                ((BaseFragment) mComeFrom).closeProgress();
//            }
//        }
        UiHandler listener = HttpConfigure.getUiHandler();
        if (listener != null) {
            listener.closeProgressView(mComeFrom);
        }
    }

    private void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        try {
            if (!(mComeFrom instanceof Context) ||
                    ((mComeFrom instanceof Activity)
                            && (((Activity) mComeFrom).isFinishing()))) {
                return;
            }
            Context appCxt = ((Context) mComeFrom);
            ToastUtils.showToast(appCxt, msg);
        } catch (Exception e) {

        }
    }

    /**
     * 检查网络链接是否可用
     */
    public boolean isNetWorkAvailable(Object mComeFrom) {
        if (mComeFrom instanceof Context) {
            Context appCxt = ((Context) mComeFrom);
            ConnectivityManager connManager = (ConnectivityManager) appCxt
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connManager == null) {
                return false;
            }
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
            return false;
        }
        return true;
    }
}
