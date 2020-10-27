package com.toast;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import java.lang.reflect.Field;


/**
 * 统一使用，方便更改
 */
public class ToastUtils {

    /**
     * 判断是context是否有用
     *
     * @param context
     * @return
     */
    public static boolean contextInvalid(Context context) {
        if (context == null || context.getResources() == null) {
            return true;
        }
        return false;
    }

    public static void showToast(Context context, String msg) {
        if (contextInvalid(context)) {
            return;
        }
        showToast(context, msg, Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    public static void showToast(Context context, @StringRes int strId) {
        if (contextInvalid(context)) {
            return;
        }
        showToast(context, context.getResources().getString(strId));
    }

    public static void showToastCenter(Context context, @StringRes int strId) {
        showToastCenter(context, context.getResources().getString(strId));
    }

    public static void showToastCenter(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    public static void showToastCenterLong(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG, Gravity.CENTER);
    }

    public static void showToast(Context context, String msg, int duration, int gravity) {
        if (contextInvalid(context)) {
            return;
        }
        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        Toast toast = Toast.makeText(context, msg, duration);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        ((TextView) view.findViewById(R.id.toast_tv)).setText(msg);
        toast.setView(view);
        toast.setGravity(gravity, 0, 0);
        showToast(toast);
    }

    public static void showToast(Toast toast) {
        //这里只针对7.0；7.0以后google已经修复
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            hookToast(toast);
        }
        toast.show();
    }

    /**
     * 代理
     *
     * @param toast
     */
    public static void hookToast(Toast toast) {
        Class<Toast> cToast = Toast.class;
        try {
            //TN是private的
            Field fTn = cToast.getDeclaredField("mTN");
            fTn.setAccessible(true);

            //获取tn对象
            Object oTn = fTn.get(toast);
            //获取TN的class，也可以直接通过Field.getType()获取。
            Class<?> cTn = oTn.getClass();
            Field fHandle = cTn.getDeclaredField("mHandler");

            //重新set->mHandler
            fHandle.setAccessible(true);
            fHandle.set(oTn, new HandlerProxy((Handler) fHandle.get(oTn)));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
