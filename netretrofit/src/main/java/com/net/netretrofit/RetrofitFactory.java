package com.net.netretrofit;

import com.net.netretrofit.interceptor.HeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @author tgl
 * 初始创建网络请求管理
 */

public class RetrofitFactory {
    private static final int CONNECT_TIMEOUT = 20 * 1000;
    private static final int DEFAULT_READ_TIMEOUT = 60 * 1000;
    private static final int DEFAULT_CONN_TIMEOUT = 60 * 1000;
    private static OkHttpClient mOkHttpClient;

    public static Retrofit get() {
        return RetrofitManager.getRetrofit(HttpConfigure.getServiceHost(), client(DEFAULT_CONN_TIMEOUT));
    }

    /**
     * 服务器地址
     *
     * @param server
     * @return
     */
    public static Retrofit get(String server) {
        return RetrofitManager.getRetrofit(server, client(DEFAULT_CONN_TIMEOUT));
    }

    private static OkHttpClient client(int writeTimeout) {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .addInterceptor(new HeaderInterceptor())
                    // .addInterceptor(new CookieInterceptor())
                    .build();
        }
        mOkHttpClient.newBuilder()
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return mOkHttpClient;
    }
}
