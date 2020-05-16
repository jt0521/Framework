package com.net.netretrofit.request;

/**
 * @author tgl
 * 网络请求生命周期管理
 */
public interface LifeCycle {
    void addRequestLifecycle(RequestLifecycle lifecycle);

    void removeRequestLifecycle(RequestLifecycle lifecycle);
}
