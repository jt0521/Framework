package com.net.netretrofit;

import android.content.Context;

import androidx.annotation.NonNull;

import com.net.netretrofit.listener.UiHandler;


/**
 * @author tgl
 * Http配置初始化
 */
public class HttpConfigure {
    /**
     * 服务器地址
     */
    private static String SERVICE_HOST = "";
    private static Context mAppContext;
    private static UiHandler mUiHandler;

    /**
     * 初始化Http Header常量
     *
     * @param context
     * @param apiVersion    接口版本号
     * @param channelId     渠道号
     * @param clientSubType 客户端子类型 {@link com.net.netretrofit.HttpHeader#CLIENT_SUB_TYPE}
     */
    public static void init(@NonNull Context context, String apiVersion, String channelId,
                            String clientSubType) {
        mAppContext = context.getApplicationContext();
        //初始化 Http header
        HttpHeader.init(context, apiVersion, channelId, clientSubType);
    }

    public static void setUiHandler(UiHandler uiHandler) {
        mUiHandler = uiHandler;
    }

    public static UiHandler getUiHandler() {
        return mUiHandler;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static void setServiceHost(String serviceHost) {
        SERVICE_HOST = serviceHost;
    }

    public static String getServiceHost() {
        return SERVICE_HOST;
    }
}
