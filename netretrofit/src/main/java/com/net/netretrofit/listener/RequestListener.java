package com.net.netretrofit.listener;


import com.net.netretrofit.callback.ResultBean;

/**
 * @author tgl
 * 网络响应处理回调基类
 */
public abstract class RequestListener<T> {
    /**
     * http请求前
     */
    public void onPreStart() {

    }

    /**
     * http请求开始回调
     */
    public void onStart() {

    }

    /**
     * http请求取消回调
     */
    public void onCancelled() {

    }

    /**
     * http请求超时回调
     */
    public void onLoginTimeout() {

    }

    /**
     * http重新发送请求回调
     */
    public void onReSendReq() {

    }

    /**
     * http 加载中回调
     *
     * @param total
     * @param current
     * @param isUploading
     */
    public void onLoading(long total, long current, boolean isUploading) {

    }

    /**
     * http成功回调
     *
     * @param data
     */
    public abstract void onSuccess(T data);

    public void onResponse(Object responseBody) {

    }

    /**
     * http回调失败
     *
     * @param code
     * @param msg
     * @param netError http层错误，非业务层错误
     * @return 默认返回false，默认底层弹出toast提示;返回true表示上层已消费
     */
    public boolean onFailure(int code, String msg, boolean netError) {
        return false;
    }

    /**
     * http加载完成回调，不管失败与否
     */
    public void onLoadComplete() {

    }
}
