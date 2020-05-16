package com.mobileframe.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * tools in common use
 *
 * @author GuYiYong
 */
public class Tools {

    /**
     * 获取activityManager
     *
     * @param context context
     * @return activityManager
     */
    public static ActivityManager getActivityManager(Context context) {
        if (context == null) {
            throw new NullPointerException("context can not be empty");
        }
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * Whether the service is running.
     *
     * @param context       context
     * @param className     className
     * @param maxServiceNum maxServiceNum获取服务列表的最大容量
     * @return true or false
     */
    public static boolean isServiceRunning(Context context, String className, int maxServiceNum) {
        List<RunningServiceInfo> runningServiceInfos = getActivityManager(context).getRunningServices(maxServiceNum);
        for (RunningServiceInfo runningServiceInfo : runningServiceInfos) {
            if (runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether the activity is running.
     *
     * @param context    context
     * @param className  className
     * @param maxTaskNum maxTaskNum获取活动任务列表的最大容量
     * @return true or false
     * @deprecated see {@link ActivityManager#getRunningTasks(int)}
     */
    public boolean isActivityRunning(Context context, ComponentName className, int maxTaskNum) {
        List<RunningTaskInfo> runningTaskInfos = getActivityManager(context).getRunningTasks(maxTaskNum);
        for (RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (runningTaskInfo.topActivity.equals(className)) {
                return true;
            }
        }
        return false;
    }

    //-------------------------------------------------------------------------------------
//                                                正则表达式

    /**
     * Whether is true name.
     *
     * @param name name
     * @return true or false
     */
    public boolean isTrueName(String name) {
        if (!TextUtils.isEmpty(name)) {
            String matcher = "^[\u4e00-\u9fa5a-zA-Z0-9~!@#$%^&*()_+.~-]{2,10}$";
            if (name.matches(matcher)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether is true phoneNumber.
     *
     * @param phoneNumber phoneNumber
     * @return true or false
     */
    public boolean isTrueNumber(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            String matcher = "0\\d{2,3}-\\d{7,8}|0\\d{2,3}\\s?\\d{7,8}|13[0-9]\\d{8}|18[0-9]\\d{8}|14[0-9]\\d{8}|15[0-9]\\d{8}";
            if (phoneNumber.matches(matcher)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether is true password.
     *
     * @param password password
     * @return true or false
     */
    public boolean isTruePassword(String password) {
        if (!TextUtils.isEmpty(password)) {
            String matcher = "^\\w[a-zA-Z0-9~!@#$%^&*()_+.~-]{5,15}$";
            if (password.matches(matcher)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符串中是否包含数字
     *
     * @param matches
     * @return
     */
    public static boolean hasNumber(String matches) {
        return matches.matches(".*\\d+.*");
    }

    /**
     * 获取字符串中的数字
     *
     * @return
     */
    public static String getNumTime(String string) {
        String reg = "[^0-9]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(string);
        return mat.replaceAll("");
    }

    /**
     * 获取数字时间
     *
     * @return
     */
    public static String getNumTimes(String string) {
        String reg = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(string);
        return mat.replaceAll("");
    }

    /**
     * 是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainsChineseCharacters(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        return matcher.find();
    }


//    ——————————————————————————————————————————————————————

    /**
     * 检查网络链接是否可用
     */
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断字符串是否为空，或者是否无效
     *
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        if (TextUtils.isEmpty(text)) {
            return true;
        }
        if ("-".equals(text)) {
            return true;
        }
        return false;
    }

}
