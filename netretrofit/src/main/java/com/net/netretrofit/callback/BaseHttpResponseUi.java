package com.net.netretrofit.callback;

/**
 * @author tgl
 * http回调响应相关联的默认ui
 */
public interface BaseHttpResponseUi {

    public final static int FLAG_NONE = 0;

    /**
     * 显示http加载进度条(dialog形式)
     */
    public final static int FLAG_SHOW_PROGRESS_DIALOG = 1 << 1;

    /**
     * 加载进度条不可取消
     */
    public final static int FLAG_PROGRESS_DIALOG_NONCANCELABLE = 1 << 2;

    /**
     * 是否默认显示http加载(http请示失败，非业务逻辑)失败的占位view
     */
    public final static int FLAG_SHOW_LOAD_FAIL_VIEW = 1 << 3;

    /**
     * http响应成功后，默认不关闭加载对话，该为手动控制
     */
    public final static int FLAG_PROGRESS_MANUAL_CLOSE = 1 << 4;

    /**
     * @param flag
     * @see #FLAG_NONE
     * @see #FLAG_PROGRESS_DIALOG_NONCANCELABLE
     * @see #FLAG_PROGRESS_MANUAL_CLOSE
     * @see #FLAG_SHOW_LOAD_FAIL_VIEW
     * @see #FLAG_SHOW_PROGRESS_DIALOG
     */
    public void setHttpResponseUi(int flag);

    /**
     * http请求开始前
     */
    public void onPreStart();

    /**
     * http请求开始
     */
    public void onStart();

    /**
     * http成功响应
     *
     * @param code 业务逻辑响应码
     */
    public void onSuccess(int code);

    /**
     * 处理错误
     *
     * @param errorCode
     * @param msg
     * @param httpError 如：401(访问拒绝) 500(服务器内部错误)
     * @param showToast [true,toast]，[false，不显示]
     */
    public void onFailure(int errorCode, String msg, boolean httpError, boolean showToast);

    /**
     * 业务码错误
     *
     * @param errorCode
     * @param msg
     * @param
     */
    public void onFailureDoThing(int errorCode, String msg);

    /**
     * 接口被取消
     */
    public void onCancelled();
}
