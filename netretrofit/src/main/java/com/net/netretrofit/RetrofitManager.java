package com.net.netretrofit;

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author tgl
 */
public class RetrofitManager {

    public static Retrofit getRetrofit(String server, OkHttpClient client) {
        return getRetrofit(server, client, new Retrofit2ConverterFactory());
    }

    public static Retrofit getRetrofit(String server, OkHttpClient client, Converter.Factory convertFactory) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(server)
                .client(client);
        if (convertFactory != null) {
            builder.addConverterFactory(convertFactory);
        }

        return builder.build();
    }
}
