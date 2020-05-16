package com.mobileframe.tools;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.StringRes;


/**
 * 统一使用，方便更改
 */
public class ToastUtils {
    public static void showToast(Context context, String msg) {
        if (contextInvalid(context)) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, @StringRes int strId) {
        if (contextInvalid(context)) {
            return;
        }
        Toast.makeText(context, context.getResources().getString(strId), Toast.LENGTH_SHORT).show();
    }

    public static void showToastCenter(Context context, @StringRes int strId) {
        showToastCenter(context, context.getResources().getString(strId));
    }

    public static void showToastCenter(Context context, String msg) {
        showToastCenter(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToastCenterLong(Context context, String msg) {
        showToastCenter(context, msg, Toast.LENGTH_LONG);
    }

    public static void showToastCenter(Context context, String msg, int duration) {
        if (contextInvalid(context)) {
            return;
        }
        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

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
}
