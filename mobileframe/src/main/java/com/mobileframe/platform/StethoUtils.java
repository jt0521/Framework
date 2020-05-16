package com.mobileframe.platform;

/*
 * 功能描述：debug使用stetho，release不使用有如下两种解决方法
 * 1、调用此类注册，反射异常已捕获处理，因此不会报错
 * 2、使用第三方空库    releaseImplementation 'com.github.TangHuaiZhe:stetho-no-op:1.0'
 *
 *
 * 作者：Created by tgl on 2018/6/20.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */

import android.content.Context;

import java.lang.reflect.Method;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class StethoUtils {

    public static void init(Context context) {
        try {
            Class<?> stethoClass = Class.forName("com.facebook.stetho.Stetho");
            Method initializeWithDefaults = stethoClass.getMethod("initializeWithDefaults", Context.class);
            initializeWithDefaults.invoke(null, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OkHttpClient configureInterceptor(OkHttpClient httpClient) {
        try {
            Class<?> aClass = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor");
            return httpClient.newBuilder().addNetworkInterceptor((Interceptor) aClass.newInstance()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpClient;
    }
}

