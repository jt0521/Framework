package com.net.netretrofit.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

public class HeaderDataUtils {
    /**
     * 获取软件版本名称
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        if (context == null) {
            return "";
        }
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取屏幕宽度
     *
     * @param c the c
     * @return the screen w
     */
    public static int getScreenW(Context c) {
        return c == null ? 0 : c.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param c the c
     * @return the screen h
     */
    public static int getScreenH(Context c) {
        return c == null ? 0 : c.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * @param slotId slotId为卡槽Id，它的值为 0、1；
     * @return
     */
    public static String getIMEI(Context context, int slotId) {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            return (String) method.invoke(manager, slotId);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * if null, use the default format (Mozilla/5.0 (Linux; U;
     * Android %s) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0
     * %sSafari/534.30).
     *
     * @return
     */
    public static String getUserAgent() {
        String webUserAgent = "Android_%s_%s";
        String version = Build.VERSION.RELEASE;
        String name = Build.BRAND + " " + Build.MODEL;
        String userAgent = String.format(webUserAgent, version, name);
        try {
            return URLEncoder.encode(userAgent, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取网络类型
     *
     * @param content
     * @return
     */
    public static final int NETWORD_UNKNOW = 200;
    public static final int NETWORK_WIFI = 201;
    public static final int NETWORK_2G = 202;
    public static final int NETWORK_3G = 203;
    public static final int NETWORK_4G = 204;

    public static int getNetType(Context content) {
        if (content == null) {
            return NETWORD_UNKNOW;
        }
        ConnectivityManager cManager = (ConnectivityManager) content
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cManager.getActiveNetworkInfo();
        int type = NETWORD_UNKNOW;
        if (netInfo != null) {
            switch (netInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    type = NETWORK_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    TelephonyManager telMgr = (TelephonyManager) content
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    switch (telMgr.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            type = NETWORK_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case 14:// TelephonyManager.NETWORK_TYPE_EHRPD
                        case 15:// TelephonyManager.NETWORK_TYPE_HSPAP
                            type = NETWORK_3G;
                            break;
                        case 13:// TelephonyManager.NETWORK_TYPE_LTE
                            type = NETWORK_4G;
                        default:
                            type = NETWORD_UNKNOW;
                            break;
                    }
                    break;
                default:
                    type = NETWORD_UNKNOW;
                    break;
            }
        }
        return type;
    }

    /**
     * 获取网络类型名称
     *
     * @param content
     * @return
     */
    public static String getNetTypeName(Context content) {
        String type = "";
        switch (getNetType(content)) {
            case NETWORD_UNKNOW:
                type = "unknow";
                break;
            case NETWORK_WIFI:
                type = "wifi";
                break;
            case NETWORK_2G:
                type = "2G";
                break;
            case NETWORK_3G:
                type = "3G";
                break;
            case NETWORK_4G:
                type = "4G";
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * Indicates whether network connectivity is possible.
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null)
            return false;

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return false;
        }
        if (networkInfo.isConnected() || networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }
}
