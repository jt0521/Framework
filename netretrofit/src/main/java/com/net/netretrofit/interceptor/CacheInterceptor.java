package com.net.netretrofit.interceptor;

import android.content.Context;
import android.util.Log;

import com.net.netretrofit.tool.HeaderDataUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author tgl
 * 缓存拦截器
 */
public class CacheInterceptor implements Interceptor {

    private Context mContext;

    public CacheInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();//获取请求
        //有网络获取网络数据，没有网络取缓存数据
        if (!HeaderDataUtils.isNetworkAvailable(mContext)) {
            request = request.newBuilder()
                    // 缓存策略
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Log.d("CacheInterceptor", "no network");
        }
        Response originalResponse = chain.proceed(request);

        if (HeaderDataUtils.isNetworkAvailable(mContext)) {
            //.header .removeHeader增减字段，祥见源码。
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    //这里设置的为0就是说不进行缓存，我们也可以设置缓存时间
                    .header("Cache-Control", "public, max-age=" + 0)
                    .removeHeader("Pragma")
                    .build();
        } else {
            int maxTime = 2 * 60 * 60; //单位s
            return originalResponse.newBuilder()
                    //没有网络的缓存时间
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxTime)
                    .removeHeader("Pragma")
                    .build();

        }
    }
}
