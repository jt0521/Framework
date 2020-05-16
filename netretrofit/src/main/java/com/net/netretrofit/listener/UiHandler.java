package com.net.netretrofit.listener;

import android.view.View;

/**
 * @author tgl
 * 网络响应ui处理监听,建议由application实现
 */
public interface UiHandler {
    /**
     * @param code 自定义状态码
     * @return http响应成功，逻辑失败，拦截统一处理事件，比如处理登录失效后退出app
     */
    public void interceptedSuccessfully(Object mComeFrom, int code);

    /**
     * 显示网络请求失败view
     *
     * @param mComeFrom
     * @param listener  失败view点击事件监听
     */
    public void showFailedView(Object mComeFrom, View.OnClickListener listener);

    /**
     * 隐藏网络请求失败view
     *
     * @param mComeFrom
     */
    public void closeFailedView(Object mComeFrom);

    /**
     * 显示网络请求等待view
     *
     * @param mComeFrom  请求页面对象
     * @param cancelable 显示时是否可手动关闭
     */
    public void showProgressView(Object mComeFrom, boolean cancelable);

    /**
     * 网络完成，关闭等待view
     *
     * @param mComeFrom
     */
    public void closeProgressView(Object mComeFrom);

    /**
     * 错误回调吐丝
     *
     * @param mComeFrom
     * @param msg
     * @return
     */
    public boolean failedShowToast(Object mComeFrom, int code, String msg);
}