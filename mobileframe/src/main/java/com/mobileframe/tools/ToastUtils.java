package com.mobileframe.tools;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.mobileframe.R;


/**
 * 统一使用，方便更改
 */
public class ToastUtils {
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
