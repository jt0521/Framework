package com.net.netretrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.net.netretrofit.tool.HeaderDataUtils;

import java.util.UUID;

import okhttp3.Headers;

/**
 * @author tgl
 * 设置Http header拦截器时引用
 *
 * @see com.net.netretrofit.interceptor.HeaderInterceptor
 */
public class HttpHeader {
    private static final Object LOCK = new Object();

    public static final String PREF_HEADER_NAME = "pref_header";
    public static final String PREF_TOKEN = "pref_token";

    //===========================定义时赋值常量=============================
    /**
     * zip 编码类型
     */
    public static final String ENCODING_GZIP = "identity";
    /**
     * Content type
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";
    /**
     * User Agent
     */
    public static final String USER_AGENT = HeaderDataUtils.getUserAgent();
    /**
     * 手机品牌商
     */
    public static final String DEVICE_BRAND = Build.BRAND;
    /**
     * 手机型号
     */
    public static final String DEVICE_MODEL = Build.MODEL;
    /**
     * 发布的版本
     */
    public static final String VERSION_RELEASE = Build.VERSION.RELEASE;
    /**
     * 客户端类型 1:安卓，2:IOS
     */
    public static final String CLIENT_TYPE = "1";

    //===========================初始化时赋值常量==============================
    /**
     * 服务端接口版本
     */
    public static String API_VERSION = "";
    /**
     * 客户端版本 如 1.1.0
     */
    public static String CLIENT_VERSION = "";
    /**
     * 渠道   默认是0，其他渠道后续定义
     */
    public static String CHANNEL_ID = "";
    /**
     * 客户端子类型 如：学生端、教师端
     */
    public static String CLIENT_SUB_TYPE = "";
    /**
     * 屏幕宽
     */
    public static int SCREEN_WIDTH = 0;
    /**
     * 屏幕高
     */
    public static int SCREEN_HEIGHT = 0;
    /**
     * 设备IMEI号
     */
    public static String IMEI = "";

    //==============================静态变量(全局访问)=========================
    /**
     * 用户TOKEN
     */
    public static String USER_TOKEN;
    /**
     * 经度
     */
    public static double LONGITUDE;
    /**
     * 纬度
     */
    public static double LATITUDE;

    /**
     * 初始化Http Header常量
     *
     * @param context
     * @param apiVersion    接口版本号
     * @param channelId     渠道号
     * @param clientSubType 客户端子类型 {@link #CLIENT_SUB_TYPE}
     */
    public static void init(Context context, String apiVersion, String channelId,
                            @NonNull String clientSubType) {
        API_VERSION = apiVersion;
        CHANNEL_ID = channelId;
        CLIENT_SUB_TYPE = clientSubType;
        if (TextUtils.isEmpty(clientSubType)) {
            throw new IllegalArgumentException("client sub type is null!");
        }

        CLIENT_VERSION = HeaderDataUtils.getAppVersionName(context);
        SCREEN_WIDTH = HeaderDataUtils.getScreenH(context);
        SCREEN_HEIGHT = HeaderDataUtils.getScreenH(context);
        getIMEI(context, true);
    }

    public static void saveResponseHeader(Headers headers) {
        if (headers == null) {
            return;
        }

        //save to sharedPreferences
        Context context = HttpConfigure.getAppContext();
        if (context == null) {
            return;
        }
        SharedPreferences sf = context.getSharedPreferences(PREF_HEADER_NAME, Context.MODE_PRIVATE);

        //token
        synchronized (LOCK) {
            String token = headers.get("token");
            if (!TextUtils.isEmpty(token)) {
                HttpHeader.USER_TOKEN = headers.get("token");
                sf.edit().putString(PREF_TOKEN, HttpHeader.USER_TOKEN).commit();
            }
        }
    }

    public static String getToken() {
        String token = HttpHeader.USER_TOKEN;
        if (TextUtils.isEmpty(token)) {
            Context context = HttpConfigure.getAppContext();
            if (context == null) {
                token = "";
                HttpHeader.USER_TOKEN = token;
            } else {
                SharedPreferences sf = context.getSharedPreferences(PREF_HEADER_NAME,
                        Context.MODE_PRIVATE);

                synchronized (LOCK) {
                    token = sf.getString(PREF_TOKEN, "");
                    HttpHeader.USER_TOKEN = token;
                }
            }
        }
        return token;
    }

    public static void clearToken() {
        Context context = HttpConfigure.getAppContext();
        SharedPreferences sf = context.getSharedPreferences(PREF_HEADER_NAME, Context.MODE_PRIVATE);
        synchronized (LOCK) {
            HttpHeader.USER_TOKEN = "";
            sf.edit().remove(PREF_TOKEN).commit();
        }
    }

    /**
     * @param context
     * @param init    true表示在application初始化,此时未获得权限
     *                false 表示请求了权限之后的操作，如果还未获得权限，则生成
     */
    public static void getIMEI(Context context, boolean init) {
        SharedPreferences sf = context.getSharedPreferences(PREF_HEADER_NAME, Context.MODE_PRIVATE);
        String imei = sf.getString("key_imei", "");
        if (TextUtils.isEmpty(imei)) {
            imei = HeaderDataUtils.getIMEI(context, 0);
            if (TextUtils.isEmpty(imei)) {
                if (init) {
                    return;
                }
                imei = (UUID.randomUUID().toString().hashCode() + String.valueOf(System.currentTimeMillis()).hashCode()) + "";
            }
            sf.edit().putString("key_imei", imei).commit();
        }
        IMEI = imei;
    }
}
