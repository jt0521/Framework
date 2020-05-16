package com.mobileframe.common;

import android.util.Log;

/**
 * Created by Administrator on 2016/9/6.
 * 日志控制类
 */
public class DeBug {

    public static final boolean LOG_DEBUG = Config.isDebugEniv;
    public static final String TAG = "mobileFrame===";

    public static void i(String tag, String msg) {
        if (LOG_DEBUG) {
            Log.i(tag, checkMsg(msg));
        }
    }

    public static void v(String tag, String msg) {
        if (LOG_DEBUG) {
            Log.v(tag, checkMsg(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (LOG_DEBUG) {
            Log.d(tag, checkMsg(msg));
        }
    }

    public static void w(String tag, String msg) {
        if (LOG_DEBUG) {
            Log.w(tag, checkMsg(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (LOG_DEBUG) {
            Log.e(tag, checkMsg(msg));
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LOG_DEBUG) {
            Log.e(tag, checkMsg(msg), tr);
        }
    }

    public static void e(String msg) {
        if (LOG_DEBUG) {
            Log.e(TAG, checkMsg(msg));
        }
    }

    private static String checkMsg(String msg) {
        return msg == null ? "null" : msg;
    }
}
